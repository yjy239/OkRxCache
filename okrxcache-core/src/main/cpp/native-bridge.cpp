//
// Created by 余均宇 on 2020-05-06.
//

#include <jni.h>
#include "CacheDef.h"
#include <unistd.h>
#include "MemoryFile.h"
#include "SmartLock.hpp"
#include "FileLock.h"
#include "SmartThreadLock.h"


size_t mmcache::DEFAULT_PAGE_SIZE;

namespace mmcache{

    static string jstring2string(JNIEnv *env, jstring str) {
        if (str) {
            const char *kstr = env->GetStringUTFChars(str, nullptr);
            if (kstr) {
                string result(kstr);
                env->ReleaseStringUTFChars(str, kstr);
                return result;
            }
        }
        return "";
    }



    static jlong init(JNIEnv *env, jobject obj,jstring rootDir){
        if(!rootDir){
            return (jlong)0;
        }
        const char *kstr = env->GetStringUTFChars(rootDir, nullptr);
        if (kstr) {
            string result(kstr);
            env->ReleaseStringUTFChars(rootDir, kstr);

            if(!result.empty()){
                MemoryFile *file = new MemoryFile(kstr);
                return (jlong)file;
            }

        }


        return (jlong)0;

    }

    static MemoryFile *getMemoryFile(JNIEnv *env, jobject obj,jlong ptr) {
        return reinterpret_cast<MemoryFile *>(ptr);
    }

    static void unmmap(JNIEnv *env, jobject obj,jlong ptr){
        MemoryFile *file = getMemoryFile(env,obj,ptr);
        if(!file||!file->isFileValid()){
            return;
        }
        file->doCleanMemoryCache();
    }


    static int getLength(JNIEnv *env, jobject obj,jlong ptr){
        MemoryFile *file = getMemoryFile(env,obj,ptr);
        if(!file||!file->isFileValid()){
            return 0;
        }
        return file->getActualFileSize();
    }


    static void write(JNIEnv *env, jobject obj,jlong ptr,jbyteArray jarray,jboolean reset){
        MemoryFile *file = getMemoryFile(env,obj,ptr);
        if(!file||!file->isFileValid()){
            return;
        }

        jbyte *array = env->GetByteArrayElements(jarray,JNI_FALSE);
        int len = env->GetArrayLength(jarray);
        file->write((char*)array,len,reset);
        env->ReleaseByteArrayElements(jarray,array,JNI_FALSE);
    }



    static jbyteArray read(JNIEnv *env, jobject obj,jlong ptr){
        MemoryFile *file = getMemoryFile(env,obj,ptr);
        if(!file||!file->isFileValid()){
            return nullptr;
        }

        jbyte * bytes = file->read();
        jbyteArray jarray = env->NewByteArray(file->getUsedSize());
        env->SetByteArrayRegion(jarray,0,file->getUsedSize(),bytes);
        free(bytes);
        return jarray;
    }


    static bool clear(JNIEnv *env, jobject obj,jlong ptr){
        MemoryFile *file = getMemoryFile(env,obj,ptr);
        if(!file||!file->isFileValid()){
            return nullptr;
        }

        return file->clearFile();
    }
}





static JNINativeMethod g_mmfile_methods[] = {
        {"native_init", "(Ljava/lang/String;)J", (void*)mmcache::init},
        {"native_unmmap", "(J)V", (void*)mmcache::unmmap},
        {"native_getLength", "(J)I", (void*)mmcache::getLength},
        {"native_write", "(J[BZ)V", (void*)mmcache::write},
        {"native_read", "(J)[B", (void*)mmcache::read},
        {"native_clear", "(J)Z", (void*)mmcache::clear}
};

static int registerNativeMethods(JNIEnv *env, jclass cls) {
    return env->RegisterNatives(cls, g_mmfile_methods, sizeof(g_mmfile_methods) / sizeof(g_mmfile_methods[0]));
}


extern "C" JNIEXPORT JNICALL
jint JNI_OnLoad(JavaVM *vm, void *reserved){
    JNIEnv *env;
    if(vm->GetEnv(reinterpret_cast<void **>(&env),JNI_VERSION_1_6) != JNI_OK){
        return -1;
    }


    //反射mmcache
    static const char* clazz_name = "com/yjy/okrxcache_core/Cache/MMCache/MMemoryFile";

    jclass instance = env->FindClass(clazz_name);

    mmcache::DEFAULT_PAGE_SIZE = getpagesize();

    registerNativeMethods(env,instance);


    return JNI_VERSION_1_6;

}
