package com.yjy.okrxcache.rx.core;

import java.lang.reflect.Proxy;
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
 *     desc   :
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

    public <T>Observable excute(final Observable<T> observable){
//        if(mUsingClass == null){
//            throw new IllegalArgumentException("必须传入Retrfit的API接口");
//        }

        //我最后要用map转化为下流
        return observable.compose(this.<T>transformeToCacheResult())
                .map(new Func1<CacheResult<T>, T>() {
                    @Override
                    public T call(CacheResult<T> tCacheResult) {
                        return tCacheResult.getData();
                    }
                });
    }

    //先转化为cacheresult的observable
//    private <T>Observable.Transformer<T,CacheResult<T>> transformeToCacheResult(){
//        return new Observable.Transformer<T, CacheResult<T>>() {
//            @Override
//            public Observable<CacheResult<T>> call(final Observable<T> tObservable) {
//                return mCore.loadResource(tObservable);
//            }
//        };
//    }

    private <T>Observable.Transformer<T,CacheResult<T>> transformeToCacheResult(){
        return new Observable.Transformer<T, CacheResult<T>>() {
            @Override
            public Observable<CacheResult<T>> call(Observable<T> tObservable) {

                return tObservable.map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T t) {
                        return new CacheResult(t,CacheStrategy.DISK,false);
                    }
                });
            }
        };
    }


    public Object getProxyClass(Object orgin){
        ProcessHandler handler = new ProcessHandler(orgin);

        return Proxy.newProxyInstance(orgin.getClass().getClassLoader(),new Class<?>[]{mUsingClass},handler);
    }




    public static class Builder{
        private String mFilePath;
        private Class<?> mUsingClass;
        private CacheCore mCore;


        public Builder setCacheDir(String filePath){
            this.mFilePath = filePath;
            return this;
        }

        public Builder using(Class<?> usingClass){
            this.mUsingClass = usingClass;

            return this;
        }

        public OkRxCache build(){
            mCore = new CacheCore(mUsingClass);

            return new OkRxCache(this);
        }

    }
}
