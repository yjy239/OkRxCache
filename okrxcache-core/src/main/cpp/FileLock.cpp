//
// Created by 余均宇 on 2020-05-11.
//

#include <asm/fcntl.h>
#include "FileLock.h"
#include <sys/file.h>

namespace mmcache{
    bool mmcache::FileLock::lock(mmcache::LockType lockType) {
        return doLock(lockType,true);
    }

    bool mmcache::FileLock::tryLock(mmcache::LockType lockType) {
        return doLock(lockType,false);
    }


    bool mmcache::FileLock::doLock(mmcache::LockType lockType, bool wait) {
        if (!isFileLockValid()) {
            return false;
        }

        bool unLockFirstIfNeeded = false;

        //上锁计数
        //如果是共享锁
        if(lockType == SharedLockType){
            //已经上了共享锁或者排他锁
            //文件其实已经锁上了，直接增一个计数即可
            if(m_sharedLockCount>0||m_exclusiveLockCount>0){
                m_sharedLockCount++;
                return true;
            }
        } else{
            //是排他锁
            if(m_exclusiveLockCount >0){
                //已经上了排他锁，就没有必要在加一次
                m_exclusiveLockCount++;
                return true;
            }
            //如果是已经加了一个共享锁
            if(m_sharedLockCount>0){
                //需要避免死锁。因为可能出现两个进程都需要从读锁升级为写锁
                unLockFirstIfNeeded = true;
            }
        }

        bool result = platformLock(lockType,wait,unLockFirstIfNeeded);

        //上锁成功，需要计数
        if(result){
            if(lockType == SharedLockType){
                m_sharedLockCount++;
            } else{
                m_exclusiveLockCount++;
            }
        }

        return result;
    }

    static int32_t LockType2FlockType(LockType lockType) {
        switch (lockType) {
            case SharedLockType:
                return LOCK_SH;
            case ExclusiveLockType:
                return LOCK_EX;
        }
        return LOCK_EX;
    }

    bool
    mmcache::FileLock::platformLock(mmcache::LockType lockType, bool wait, bool unLockFirstIfNeeded) {
        //执行上锁
        auto reallocktype = LockType2FlockType(lockType);

        auto cmd = wait ? reallocktype : (reallocktype|LOCK_NB);

        if(unLockFirstIfNeeded){
            //破坏死锁四要素之一，等待循环条件
            auto ret = flock(m_fd,reallocktype | LOCK_NB);
            if(ret == 0){
                return true;
            }

            //上锁失败，先解锁，在上锁
            ret = flock(m_fd,LOCK_UN);
            if(ret != 0){
                LOGE("fail to try unlock first fd=%d, ret=%d, error:%s", m_fd, ret, strerror(errno));
            }
        }

        //正常步骤中继续上锁
        auto ret = flock(m_fd,cmd);

        if(ret!=0){
            LOGE("fail to lock fd=%d, ret=%d, error:%s", m_fd, ret, strerror(errno));
            if(unLockFirstIfNeeded){
                //上锁失败了，那就上一个共享锁
                ret = flock(m_fd,LockType2FlockType(SharedLockType));
                if (ret != 0) {
                    // let's hope this never happen
                    LOGE("fail to recover shared-lock fd=%d, ret=%d, error:%s", m_fd, ret, strerror(errno));
                }
            }

            return false;
        } else{
            return true;
        }
    }

    bool mmcache::FileLock::platformUnLock(bool unLockFirstIfNeeded) {
        //解锁还是下降为共享锁
        int cmd = unLockFirstIfNeeded?LOCK_SH:LOCK_UN;
        int result = flock(m_fd,cmd);
        if(result != 0){
            LOGE("fail to unlock fd=%d, ret=%d, error:%s", m_fd, result, strerror(errno));
            return false;
        } else{
            return true;
        }
    }

    bool FileLock::unlock(LockType lockType) {
        if(!isFileLockValid()){
            return false;
        }
        //下降到共享锁
        bool unlockToSharedLock = false;

        if(lockType == SharedLockType){
            //如果是共享锁，且共享锁计数为0，直接返回
            if(m_sharedLockCount == 0){
                return false;
            }

            if(m_sharedLockCount > 1||m_exclusiveLockCount>0){
                //多个共享锁，或者已经加了排他锁
                //共享锁计数减1
                m_sharedLockCount--;
                return true;
            }
        } else{
            //排他锁
            if(m_exclusiveLockCount == 0){
                return false;
            }
            //添加了多个共享锁
            if(m_exclusiveLockCount > 1){
                m_exclusiveLockCount--;
                return true;
            }

            //如果有了共享锁,同时要解锁排他锁，则下降为共享锁
            if(m_sharedLockCount>0){
                unlockToSharedLock = true;
            }
        }

        bool result = platformUnLock(unlockToSharedLock);
        if(result){
            //如果是共享锁
            if(lockType == SharedLockType){
                m_sharedLockCount--;
            } else{
                m_exclusiveLockCount--;
            }
        }
        return result;
    }

    FileLock::FileLock(int fd):m_fd(fd) {

    }


}

