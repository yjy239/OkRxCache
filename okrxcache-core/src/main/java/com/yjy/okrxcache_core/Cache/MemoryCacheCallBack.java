package com.yjy.okrxcache_core.Cache;

import com.yjy.okrxcache_core.Cache.Key.Key;
import com.yjy.okrxcache_core.CacheResult;


/**
 * Created by software1 on 2018/2/1.
 */

public interface MemoryCacheCallBack {

    /***内存**/
    CacheResult<?> loadFromCache(Key key, boolean isMemoryCacheable);

    /**活跃内存*/
    CacheResult<?> loadFromActiveResources(Key key, boolean isMemoryCacheable);

    /***任务完成**/
    boolean complete(Key key, CacheResult resource);

    /***任务取消**/
    boolean remove(Key key);


    boolean clear();

}
