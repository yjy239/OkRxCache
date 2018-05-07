package com.yjy.okrxcache_core;

import android.content.Context;

import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.Cache.MemoryCache.MemoryCache;
import com.yjy.okrxcache_core.Convert.GsonConvert;
import com.yjy.okrxcache_core.Convert.IConvert;
import com.yjy.okrxcache_core.Engine.CacheEngine;
import com.yjy.okrxcache_core.Engine.InterceptorMode;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :the door of the OkRxCache
 *     version: 1.0
 * </pre>
 */

public class OkRxCache {

    private String mDirPath;
    private Class<?> mUsingClass;
    private CacheCore mCore;
    private volatile static OkRxCache sOkRxcache;
    private MemoryCache mMemoryCache;
    private CacheStragry mCacheStagry;
    private IConvert mConvert;
    private DiskCache.Factory mDiskCacheFactory;
    private CacheEngine mEngine;


    synchronized static OkRxCache get(Context context){
        if(sOkRxcache == null){
            synchronized (OkRxCache.class){
                if(sOkRxcache == null){
                    Context applicationContext = context.getApplicationContext();
                    RxCacheBuilder builder = new RxCacheBuilder(applicationContext);
                    sOkRxcache = builder.createCache();
                }
            }
        }
        return sOkRxcache;
    }


    /**
     * 全局初始化
     * @param context
     * @return
     */
    public static OkRxCache init(Context context){
        return get(context);
    }


    /**
     * 全局初始化之后的with
     * @return
     */
    public static RequestBuilder with(){
        if(sOkRxcache == null){
            throw new NullPointerException("the Instance of okrxcache is null,please use init() to init it");
        }
        return new RequestBuilder(sOkRxcache);
    }





    /**
     * 传入context 要创建Cachedir,不然就需要filepath的完全路径
     * 最好传入context ，创建符合android规范的，因为全路径名称可能会在某些目录无权限
     * @param context
     * @return
     */
    public static RequestBuilder with(Context context){
        return new RequestBuilder(context);
    }



    public OkRxCache(CacheCore core,CacheEngine mEngine,DiskCache.Factory diskFactory,MemoryCache cache
            ,IConvert convert){
        this.mDiskCacheFactory = diskFactory;
        this.mMemoryCache = cache;
        this.mConvert = convert;
        this.mCore = core;
        this.mEngine = mEngine;
    }

    CacheCore getCore(){
        return mCore;
    }

    CacheEngine getEngine(){
        return mEngine;
    }



}
