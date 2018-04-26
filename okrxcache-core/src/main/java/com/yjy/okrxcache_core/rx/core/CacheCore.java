package com.yjy.okrxcache_core.rx.core;


import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.EmptySignature;
import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.rx.core.Engine.CacheEngine;

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

public class CacheCore {

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private CacheEngine mEngine;
    private DiskCache.Factory mDiskFactory;
    private Key signature = EmptySignature.obtain();


    public CacheCore(ArrayList<Interceptor> mInterceptors,DiskCache.Factory diskFactory){
        this.mInterceptors = mInterceptors;
        this.mDiskFactory = diskFactory;
        mInterceptors.add(new MemoryInterceptor());
        mInterceptors.add(new DiskInterceptor());
        mInterceptors.add(new NetWorkInterceptor());
        mEngine = new CacheEngine(mInterceptors,mDiskFactory);
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

        return mEngine.run(observable,request);

    }



}
