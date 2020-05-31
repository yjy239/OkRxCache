package com.yjy.okrxcache_core.Cache.MMCache;

import com.yjy.okrxcache_core.Cache.Key.Key;
import com.yjy.okrxcache_core.Cache.MemoryCache.MemoryCache;
import com.yjy.okrxcache_core.CacheResult;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/05/07
 *     desc   : 基于mmap的读取策略
 *
 *     version: 1.0
 * </pre>
 */
public class MMCache implements MemoryCache {
    //地址指针
    private long mPtr;

    /**
     * first time find in jni:
     * MMCache:
     * 1.find: --> cache jourlist
     * 2.jourlist --> File
     * 3.save into unorder_map key-->file
     */

    public MMCache(){
        mPtr = init();
    }



    @Override
    public int getCurrentSize() {
        return 0;
    }

    @Override
    public int getMaxSize() {
        return 0;
    }

    @Override
    public void setSizeMultiplier(float multiplier) {

    }

    @Override
    public CacheResult<?> remove(Key key) {
        return null;
    }

    @Override
    public CacheResult<?> put(Key key, CacheResult<?> resource) {
        return null;
    }

    @Override
    public void setResourceRemovedListener(ResourceRemovedListener listener) {

    }

    @Override
    public void clearMemory() {

    }

    @Override
    public void trimMemory(int level) {

    }

    public native long init();
}
