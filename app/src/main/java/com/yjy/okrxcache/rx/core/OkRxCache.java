package com.yjy.okrxcache.rx.core;

import com.yjy.okrxcache.rx.core.RxInterceptor.Interceptor;
import com.yjy.okrxcache.rx.core.RxInterceptor.MemoryInterceptor;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import okhttp3.Cache;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    private Builder mBuilder;
    private String mDirPath;
    private Class<?> mUsingClass;
    private CacheCore mCore;

    public OkRxCache(Builder builder){
        this.mBuilder = builder;
        this.mDirPath = builder.mFilePath;
        this.mUsingClass = builder.mUsingClass;
        this.mCore = builder.mCore;
    }


    //此处为核心。我们将开始动态代理
    public <T>T create(T orgin){
        ProcessHandler handler = new ProcessHandler(orgin,mCore);

        return (T)Proxy.newProxyInstance(orgin.getClass().getClassLoader(),new Class<?>[]{mUsingClass},handler);
    }


    public static class Builder{
        private String mFilePath;
        private Class<?> mUsingClass;
        private CacheCore mCore;
        private ArrayList<Interceptor> mInterceptors = new ArrayList<>();


        public Builder setCacheDir(String filePath){
            this.mFilePath = filePath;
            return this;
        }

        public Builder using(Class<?> usingClass){
            this.mUsingClass = usingClass;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor){
            mInterceptors.add(interceptor);
            return this;
        }

        public OkRxCache build(){

            mInterceptors.add(new MemoryInterceptor());
            mCore = new CacheCore(mInterceptors);
            return new OkRxCache(this);
        }

    }
}
