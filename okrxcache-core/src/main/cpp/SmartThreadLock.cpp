//
// Created by 余均宇 on 2020/5/25.
//

#include "SmartThreadLock.h"

#include "CacheDef.h"

SmartThreadLock::SmartThreadLock() {
    pthread_mutexattr_t  attr;
    pthread_mutexattr_init(&attr);
    //互斥锁
    pthread_mutexattr_settype(&attr,PTHREAD_MUTEX_RECURSIVE);

    pthread_mutex_init(&mutex_t,&attr);

    pthread_mutexattr_destroy(&attr);
}

SmartThreadLock::~SmartThreadLock() {
    pthread_mutex_destroy(&mutex_t);
}

void SmartThreadLock::lock() {
    auto ret = pthread_mutex_lock(&mutex_t);
    if(ret != 0){
        LOGE("fail to thread lock  %p, ret=%d, errno=%s",&mutex_t,ret,strerror(errno));
    }
}

void SmartThreadLock::unlock() {
    auto ret = pthread_mutex_unlock(&mutex_t);
    if(ret != 0){
        LOGE("fail to thread unlock  %p, ret=%d, errno=%s",&mutex_t,ret,strerror(errno));
    }
}

void SmartThreadLock::ThreadInitOnce(ThreadOnceToken_t *onceToken, void (*callback)(void)) {
    pthread_once(onceToken, callback);
}
