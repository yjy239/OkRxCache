//
// Created by 余均宇 on 2020-05-12.
//

#ifndef OKRXCACHE_SMARTLOCK_H
#define OKRXCACHE_SMARTLOCK_H
#ifdef  __cplusplus


namespace mmcache{

    template <typename T>
    class SmartLock{
        T *m_lock;

        void lock() {
            if (m_lock) {
                m_lock->lock();
            }
        }

        void unlock() {
            if (m_lock) {
                m_lock->unlock();
            }
        }

    public:
        explicit SmartLock(T *oLock) : m_lock(oLock) {
                lock();
        }

        ~SmartLock() {
            unlock();
            m_lock = nullptr;
        }

        //禁止拷贝构造函数
        explicit SmartLock(const SmartLock<T> &other) = delete;
        SmartLock &operator=(const SmartLock<T> &other) = delete;
    };


}

#include <type_traits>
//获取指针类型,并且设置参数名
#define LOCK(lock) _LOCK(lock,__COUNTER__)
#define _LOCK(lock,counter) __LOCK(lock,counter)
#define __LOCK(lock,counter)                                                                                    \
    mmcache::SmartLock<std::remove_pointer<decltype(lock)>::type> __smartLock##counter(lock)

#endif
#endif //OKRXCACHE_SMARTLOCK_H
