//
// Created by 余均宇 on 2020-05-08.
//

#ifndef OKRXCACHE_CACHEDEF_H
#define OKRXCACHE_CACHEDEF_H

#include <string>
#include <android/log.h>
#include <unordered_map>
using MMCPath_t = const std::string;
using MMCKey_t = const std::string;
using MMCFileHandle_t = int;
namespace mmcache{
    extern size_t DEFAULT_PAGE_SIZE;

}



#define TAG "OKRXCACHE"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)


#endif //OKRXCACHE_CACHEDEF_H
