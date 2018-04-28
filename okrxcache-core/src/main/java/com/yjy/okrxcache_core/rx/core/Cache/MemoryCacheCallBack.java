package com.yjy.okrxcache_core.rx.core.Cache;

import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.CacheResult;


/**
 * Created by software1 on 2018/2/1.
 */

public interface MemoryCacheCallBack {

    /***存入内存**/
    CacheResult<?> loadFromCache(Key key, boolean isMemoryCacheable);

    /**存入活跃内存*/
    CacheResult<?> loadFromActiveResources(Key key, boolean isMemoryCacheable);

    /***任务完成**/
    void complete(Key key, CacheResult resource);

    /***任务取消**/
    void cancelled(Key key);

}
