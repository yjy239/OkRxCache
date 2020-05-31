//
// Created by 余均宇 on 2020-05-11.
//

#ifndef OKRXCACHE_FILELOCK_H
#define OKRXCACHE_FILELOCK_H

#include "CacheDef.h"


namespace mmcache{
    enum LockType {
        SharedLockType,
        ExclusiveLockType,
    };

    class FileLock {
        //文件锁
        int m_fd;
        //共享锁
        size_t m_sharedLockCount;
        //排他锁
        size_t m_exclusiveLockCount;
    public:
        bool lock(LockType lockType);
        bool tryLock(LockType lockType);
        bool unlock(LockType lockType);
        bool doLock(LockType lockType, bool wait);
        bool platformLock(LockType lockType, bool wait, bool unLockFirstIfNeeded);
        bool platformUnLock(bool unLockFirstIfNeeded);

        inline bool isFileLockValid() { return m_fd >= 0; }

        explicit FileLock(int fd);

        //禁止拷贝构造函数，避免计数异常
        explicit FileLock(const FileLock &other) = delete;
        FileLock &operator=(const FileLock &other) = delete;
    };

    class ProcessLock{
    public:
        FileLock *m_fileLock;
        LockType m_lockType;
        bool m_enable;
    public:
        ProcessLock(FileLock *fileLock,LockType lockType)
        :m_fileLock(fileLock),m_lockType(lockType),m_enable(true){

        }

        void lock() {
            if (m_enable&&m_fileLock) {
                m_fileLock->lock(m_lockType);
            }
        }

        bool tryLock() {
            if (m_enable&&m_fileLock) {
                return m_fileLock->tryLock(m_lockType);
            }
            return false;
        }

        void unlock() {
            if (m_enable&&m_fileLock) {
                m_fileLock->unlock(m_lockType);
            }
        }


    };

}


#endif //OKRXCACHE_FILELOCK_H
