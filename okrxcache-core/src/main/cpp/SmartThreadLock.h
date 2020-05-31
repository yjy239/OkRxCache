//
// Created by 余均宇 on 2020/5/25.
//

#ifndef OKRXCACHE_SMARTTHREADLOCK_H
#define OKRXCACHE_SMARTTHREADLOCK_H

#include <pthread.h>


#define ThreadOnceToken_t pthread_once_t
#define ThreadOnceUninitialized PTHREAD_ONCE_INIT

//线程锁
class SmartThreadLock {
private:
    pthread_mutex_t mutex_t;

public:
    SmartThreadLock();

    ~SmartThreadLock();

    void lock();

    void unlock();

    static void ThreadInitOnce(ThreadOnceToken_t *onceToken, void (*callback)(void));


    // just forbid it for possibly misuse
    explicit SmartThreadLock(const SmartThreadLock &other) = delete;
    SmartThreadLock &operator=(const SmartThreadLock &other) = delete;

};


#endif //OKRXCACHE_SMARTTHREADLOCK_H

