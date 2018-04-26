package com.yjy.okrxcache_core.rx.core;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.rx.core.Cache.CacheStrategy;
import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.EmptySignature;
import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.rx.core.Engine.CacheEngine;

import com.yjy.okrxcache_core.rx.core.Engine.EngineCallBack;
import com.yjy.okrxcache_core.rx.core.Engine.Request;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.DiskInterceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.MemoryInterceptor;
import com.yjy.okrxcache_core.rx.core.Utils.Utils;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
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

public class CacheCore implements EngineCallBack{

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private CacheEngine mEngine;
    private DiskCache.Factory mDiskFactory;
    private Key signature = EmptySignature.obtain();


    public CacheCore(ArrayList<Interceptor> mInterceptors,DiskCache.Factory diskFactory){
        this.mInterceptors = mInterceptors;
        this.mDiskFactory = diskFactory;
        mInterceptors.add(new MemoryInterceptor());
        mInterceptors.add(new DiskInterceptor());
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

        return mEngine.run(observable,method);




    }




    @Override
    public Observable getResult() {
        return null;
    }
}
