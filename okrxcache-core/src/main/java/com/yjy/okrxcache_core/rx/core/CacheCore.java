package com.yjy.okrxcache_core.rx.core;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.rx.core.Cache.MemoryCache.MemoryCache;
import com.yjy.okrxcache_core.rx.core.Cache.MemoryCacheCallBack;
import com.yjy.okrxcache_core.rx.core.Convert.IConvert;
import com.yjy.okrxcache_core.rx.core.Engine.CacheEngine;

import com.yjy.okrxcache_core.rx.core.Request.Request;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.Observable;

import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;
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

        request.init2(key,null,false,observable,method);

        return mEngine.run(request);

    }

    public Observable operator(Observable observable, String Key,int mode,Request request){


        RequestKey key = new RequestKey(Key);

        request.init2(key,null,false,observable,null);

        return mEngine.operator(request, mode);
    }








}
