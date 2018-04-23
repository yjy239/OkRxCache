package com.yjy.okrxcache.rx.core;


import com.yjy.okrxcache.rx.Annonation.LifeCache;
import com.yjy.okrxcache.rx.core.RxInterceptor.Interceptor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

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

    public CacheCore(ArrayList<Interceptor> mInterceptors){
        this.mInterceptors = mInterceptors;
    }

    public <T> Observable<CacheResult<T>> loadResource(Observable<T> observable){
        return observable.map(new Func1<T, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(T t) {
                return null;
            }
        });
    }

    public void start(){

    }
}
