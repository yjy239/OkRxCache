package com.yjy.okrxcache_core.rx.core;


import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.EmptySignature;
import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.rx.core.Convert.IConvert;
import com.yjy.okrxcache_core.rx.core.Engine.CacheEngine;

import com.yjy.okrxcache_core.rx.core.Engine.InterceptorMode;
import com.yjy.okrxcache_core.rx.core.Engine.Request;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.DiskInterceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.MemoryInterceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.NetWorkInterceptor;

import java.util.ArrayList;

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

class CacheCore {

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private CacheEngine mEngine;
    private DiskCache.Factory mDiskFactory;
    private Key signature = EmptySignature.obtain();
    private int mCacheStagry = 0;

//    private static final int MODE_RUN = 0;
//    private static final int MODE_SAVE = 1;
//    private static final int MODE_GET = 2;
//    public static final int MODE_CLEAR = 3;
//    public static final int MODE_REMOVE = 4;

    private int mMode = 0;


    public CacheCore(ArrayList<Interceptor> mInterceptors,DiskCache.Factory diskFactory
            ,IConvert convert,CacheStragry cacheStagry){
        this.mInterceptors = mInterceptors;
        this.mDiskFactory = diskFactory;
        mInterceptors.add(new MemoryInterceptor(cacheStagry));
        mInterceptors.add(new DiskInterceptor(mDiskFactory.build(),convert,cacheStagry));
        mInterceptors.add(new NetWorkInterceptor());
        mEngine = new CacheEngine(mInterceptors,mDiskFactory);


    }

    public void setMode(int mode){
        mMode = mode;
    }


    //
    public <T>Observable start(Observable observable,final CacheMethod method){
        return run(observable,method);
    }

    public <T>Observable run(Observable observable, final CacheMethod method){

        RequestKey key = new RequestKey(method.getKey());

        Request request = new Request();
        request.setKey(key);
        request.setObservable(observable);
        request.setMethod(method);

        return mEngine.run(request);
    }

    public Observable operator(Observable observable, String Key,int mode){

        RequestKey key = new RequestKey(Key);

        Request request = new Request();
        request.setKey(key);
        request.setObservable(observable);

        return mEngine.operator(request, mode);
    }



}
