//
// Created by 余均宇 on 2020-05-07.
// thank for mmkv

#ifndef OKRXCACHE_MEMORYFILE_H
#define OKRXCACHE_MEMORYFILE_H

#include <string>
#include <jni.h>


#include "SmartLock.hpp"
#include "FileLock.h"

#include "SmartThreadLock.h"

using namespace std;
namespace mmcache{



class MemoryFile {
public:
    //文件的名字的句柄
    MMCPath_t m_name;
    MMCFileHandle_t m_fd = -1;
    SmartThreadLock *m_lock;

    mmcache::FileLock *fileLock;
    mmcache::ProcessLock *shared_lock;
    mmcache::ProcessLock *ex_lock;

    //文件映射的起始地址
    void* m_ptr;
    //文件大小
    size_t m_size;




public:
    MemoryFile(string path,size_t size = DEFAULT_PAGE_SIZE);

    //进行文件的映射
    bool mmap();

    //解开映射
    void doCleanMemoryCache();

    //重置File
    bool clearFile();


    bool isFileValid() { return m_fd >= 0 && m_size > 0 && m_ptr; }

    //扩容
    bool truncate(size_t size);

    // 获取已经使用了的大小

    //重新读取
    void reloadFromFile();

    void *getMemory() { return m_ptr; }

    const MMCPath_t &getName() { return m_name; }

    MMCFileHandle_t getFd() { return m_fd; }

    ~MemoryFile();

    bool fillZero(MMCFileHandle_t fd, int size, size_t fillZero);

    bool write(char* bytes, long len,bool reset);

    void writeTo(int& used_size,void* content,size_t write_len);

    int getUsedSize();

    jbyte* read();

    size_t getActualFileSize();



};
}




#endif //OKRXCACHE_MEMORYFILE_H
