package com.yjy.okrxcache_core;


import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.Cache.MemoryCache.MemoryCache;
import com.yjy.okrxcache_core.Cache.MemoryCacheCallBack;
import com.yjy.okrxcache_core.Convert.IConvert;
import com.yjy.okrxcache_core.Engine.CacheEngine;

import com.yjy.okrxcache_core.Request.Request;

import java.lang.reflect.Type;
import java.util.ArrayList;

import rx.Observable;

import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;
/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :the core of the RxCache
 *     version: 1.0
 * </pre>
 */

public class CacheCore {

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();


    private DiskCache.Factory mDiskCacheFactory;
    private IConvert mConvert;
    private CacheStragry mCacheStragry;
    private MemoryCache mMemoryCache;
    private CacheEngine mEngine;


    public CacheCore(CacheEngine engine){
//        this.mInterceptors = mInterceptors;
        this.mEngine = engine;

    }



    public <T>Observable start(Observable observable,final CacheMethod method,Request request){

        return run(observable,method,request);
    }


    public <T>Observable run(Observable observable, final CacheMethod method,Request request){

        RequestKey key = new RequestKey(method.getKey());

        request.init2(request,key,null,false,observable,method);

        return mEngine.run(request);

    }

    public Observable operator(Observable observable, String Key,int mode,Request request,Type type){
        RequestKey key = null;
        if(Key != null){
            key = new RequestKey(Key);
        }

        request.init2(request,key,null,false,observable,null);

        request.setReturnType(type);

        return mEngine.operator(request, mode);
    }








}
