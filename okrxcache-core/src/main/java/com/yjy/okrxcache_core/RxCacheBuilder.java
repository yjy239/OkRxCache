package com.yjy.okrxcache_core;

import android.content.Context;

import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.Cache.DiskCache.InternalCacheDiskCacheFactory;
import com.yjy.okrxcache_core.Cache.MemoryCache.LruResourceCache;
import com.yjy.okrxcache_core.Cache.MemoryCache.MemoryCache;
import com.yjy.okrxcache_core.Convert.GsonConvert;
import com.yjy.okrxcache_core.Convert.IConvert;
import com.yjy.okrxcache_core.Engine.CacheEngine;
import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;

import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class RxCacheBuilder {

    private String mFilePath;
    private Class<?> mUsingClass;
    private CacheCore mCore;
    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private DiskCache.Factory mDiskCacheFactory;
    private DiskCache mDiskcache;
    private Context mContext;
    private int mDiskSize = 0;
    private IConvert mConvert = new GsonConvert();
    private CacheStragry mCacheStagry;
    private boolean isForce = true;
    private MemoryCache mMemoryCache;
    private static final int DEFAULT_MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);//运行内存的8分之1



    public RxCacheBuilder(Context context){
        this.mContext = context.getApplicationContext();
    }


    public OkRxCache createCache(){
        if(mDiskCacheFactory == null){
            mDiskCacheFactory = new InternalCacheDiskCacheFactory(mContext,
                    null,0);
            mDiskcache = mDiskCacheFactory.build();
        }

        if(mMemoryCache == null){
            mMemoryCache = new LruResourceCache(DEFAULT_MEMORY_CACHE_SIZE);
        }

        if(mConvert == null){
            mConvert = new GsonConvert();
        }

        if(mCacheStagry == null){
            mCacheStagry = CacheStragry.ALL;
        }

        CacheEngine engine = new CacheEngine(mDiskcache,mMemoryCache,mConvert,mCacheStagry);
        mCore = new CacheCore(engine);

        return new OkRxCache(mCore,engine,mDiskCacheFactory,mMemoryCache,mConvert);
    }






}
