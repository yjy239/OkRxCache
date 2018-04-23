package com.yjy.okrxcache.rx.core;


import com.yjy.okrxcache.rx.Annonation.LifeCache;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import rx.Observable;

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

    private Class<?> mApiClass;

    public CacheCore(Class<?> mApiClass){
        this.mApiClass = mApiClass;
    }

    //解码class
    private void decodeClass(){
        if(mApiClass != null){
//            for(Annotation annonation : mApiClass.getAnnotations()){
//                if(annonation instanceof LifeCache){
//                    LifeCache life = (LifeCache)annonation;
//                    long duaration = life.duaration();
//                    TimeUnit unit = life.unit();
//                }
//            }
//            ServiceMethod method = new ServiceMethod();
//            method.setLifeTime();
        }

    }


//    public <T> Observable<CacheResult<T>> loadResource(Observable observable){
//
//    }
}
