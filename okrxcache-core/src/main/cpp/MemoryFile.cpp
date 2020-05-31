//
// Created by 余均宇 on 2020-05-07.
//


#include <sys/mman.h>
#include <fcntl.h>
#include <sys/stat.h>
#include "CacheDef.h"
#include "MemoryFile.h"

namespace mmcache{

    static bool getFileSize(MMCFileHandle_t fd,size_t &size){
        struct stat st = {};
        if(fstat(fd,&st) != -1){
            size = (size_t)st.st_size;
            return true;
        }

        return false;
    }



    MemoryFile::MemoryFile(MMCPath_t path,size_t page_size)
    :m_name(path),m_size(page_size),
    m_lock(new SmartThreadLock()) {
        //重新读取文件
        reloadFromFile();
    }



    void MemoryFile::reloadFromFile() {
        if(isFileValid()){
            //强制清除内存中数据
            doCleanMemoryCache();
        }

        m_fd = ::open(m_name.c_str(),O_RDWR|O_CREAT|O_CLOEXEC,S_IRWXU);
        if(m_fd<0){
            LOGE("fail to open [%s]",m_name.c_str());
        } else{
            //扩容和映射都需要保护
            fileLock = new FileLock(m_fd);
            ex_lock = new ProcessLock(fileLock,ExclusiveLockType);
            shared_lock = new ProcessLock(fileLock,SharedLockType);

            LOCK(m_lock);
            LOCK(ex_lock);

            //打开文件成功，开始映射内存
            bool isSuccess = mmcache::getFileSize(m_fd,m_size);

            if(isSuccess){
                //如果空间不是整页的倍数需要进行扩容
                if(m_size < DEFAULT_PAGE_SIZE||(m_size % DEFAULT_PAGE_SIZE)!=0){
                    size_t roundSize = ((m_size/DEFAULT_PAGE_SIZE) + 1)*DEFAULT_PAGE_SIZE;
                    truncate(roundSize);
                } else{
                    bool result = mmap();
                    if(!result){
                        LOGE("mmap fail clean");
                        doCleanMemoryCache();
                    }
                }
            }
        }
    }


    bool MemoryFile::mmap() {
        //开始映射
        m_ptr =(char *) ::mmap(m_ptr,m_size,PROT_READ|PROT_WRITE,MAP_SHARED,m_fd,0);
        if(m_ptr == MAP_FAILED){
            LOGE("fail to mmap [%s]",m_name.c_str());
            m_ptr = nullptr;
            return false;
        }
        return true;
    }

    bool MemoryFile::truncate(size_t size) {
        //扩容
        if(m_fd<0){
            return false;
        }

        if(size == m_size){
            return true;
        }

        //记录当前的大小，避免申请失败之后，size失去了真实大小
        int OldSize = m_size;

        //再度确认一次大小在一页的倍数
        if(m_size < DEFAULT_PAGE_SIZE||(m_size % DEFAULT_PAGE_SIZE)!=0){
            m_size = ((m_size/DEFAULT_PAGE_SIZE) + 1)*DEFAULT_PAGE_SIZE;
        }

        //扩容
        if(::ftruncate(m_fd,m_size) != 0){
            //失败了
            m_size = OldSize;
            LOGE("fail to truncate [%s]",m_name.c_str());
            return false;
        }

        //把剩下的空间全部初始化
        if(m_size > OldSize){
            if(!fillZero(m_fd,OldSize,m_size - OldSize)){
                m_size = OldSize;
                return false;
            }
        }

        //扩容后需要重新映射
        if (m_ptr) {
            if (munmap(m_ptr, OldSize) != 0) {
                LOGE("fail to munmap [%s], %s", m_name.c_str(), strerror(errno));
            }
        }
        auto ret = mmap();
        if (!ret) {
            doCleanMemoryCache();
        }

        return true;
    }

    bool MemoryFile::fillZero(MMCFileHandle_t fd, int start, size_t size) {
        if(m_fd<0){
            return false;
        }


        //file一口气跳到size的位置
        if(lseek(m_fd,start,SEEK_SET) < 0){
            LOGE("fail to lseek when fill zero [%s]",m_name.c_str());
            return false;
        }
        static const char zeros[4096] = {};
        while (size >= sizeof(zeros)){
            if(::write(fd,zeros, sizeof(zeros)) < 0){
                LOGE("step1 fail to write when fill zero [%s]",m_name.c_str());
                return false;
            }

            size -= sizeof(zeros);
        }

        if(size > 0){
            if(::write(fd,zeros, size)< 0){
                LOGE("step2 fail to write when fill zero [%s]",m_name.c_str());
                return false;
            }
        }

        return true;
    }



    void MemoryFile::doCleanMemoryCache() {
        if(m_fd<0){
            return ;
        }
        //说明已经映射过了，需要解绑映射
        if(m_ptr&&m_ptr!=MAP_FAILED){
            if(munmap(m_ptr,m_size)){
                LOGE("fail to mumap [%s]",m_name.c_str());
            }
        }
        //设置空避免也指针
        m_ptr = nullptr;

        //关闭对应的fd
        if(m_fd>=0){
            if(::close(m_fd)!=0){
                LOGE("fail to close [%s]",m_name.c_str());
            }
        }

        m_fd = -1;
        m_size = 0;
    }

    MemoryFile::~MemoryFile() {
        doCleanMemoryCache();

        if(m_lock){
            delete m_lock;
            m_lock = NULL;
        }
        if(shared_lock){
            delete shared_lock;
            shared_lock = NULL;
        }

        if(ex_lock){
            delete ex_lock;
            ex_lock = NULL;
        }

        if(fileLock){
            delete fileLock;
            fileLock = NULL;
        }

    }

    size_t MemoryFile::getActualFileSize() {
        if(m_fd<0){
            return -1;
        }
        size_t size = 0;
        mmcache::getFileSize(m_fd, size);
        return size;
    }

    bool MemoryFile::write(char* bytes, long len,bool reset) {
        if(!isFileValid()){
            return false;
        }
        LOCK(m_lock);
        LOCK(ex_lock);
        //检测是否有足够的大小进行映射
        size_t size = getActualFileSize();
        if(reset){
            memset(m_ptr,0,size);
            size = 0;
        }

        //每一次写入，会写入当前的大小。
        // 默认最小一页是DEFAULT_PAGE_SIZE 也就是4kb
        //格式如下：头4个字节永远是指当前已经写入的大小，最大一个Int的数据
        //之后的字节数字就是内容

        //可以拿到已经有暂用了多少字节

        int used_size = getUsedSize();

        LOGE("used_size:[%d]",used_size);

        if(used_size < 0){
            return false;
        }

        //预计要申请多少字节
        int predict_size = used_size+len;


        if(predict_size > size){
            //进行一次扩容
            auto ret = truncate(predict_size);

            if(!ret){
                return false;
            }
        }

        //扩容结束后
        //说明可以进行映射了
        //直接拷贝到对应的尾巴
        //先写入大小
        //再写入内容



        memcpy((uint8_t *) m_ptr ,&predict_size,  sizeof(used_size));
        //已经用了多少+存储用了多少的大小 + 追加内容
        memcpy((uint8_t *) m_ptr+sizeof(used_size)+used_size ,bytes, len* sizeof(char));

        return true;
    }

    void MemoryFile::writeTo(int& used_size,void* content,size_t write_len){

    }


    jbyte* MemoryFile::read() {
        if(!isFileValid()){
            return nullptr;
        }

        LOCK(m_lock);
        int file_size = getActualFileSize();
        int size = getUsedSize();

        char* bytes = (char*)malloc(size*sizeof(char));
        memcpy(bytes,(uint8_t *)m_ptr+sizeof(int), size*sizeof(char));
        return (jbyte *)bytes;
    }

    int MemoryFile::getUsedSize() {
        if(!isFileValid()){
            return 0;
        }
        int used_size = 0;
        memcpy(&used_size,m_ptr, sizeof(int));
        return used_size;
    }

    bool MemoryFile::clearFile() {
        if(!isFileValid()){
            return false;
        }

        memset(m_ptr,0,getActualFileSize());

        return true;
    }


}



