package com.yjy.okrxcache_core.rx.core;



import com.yjy.okexcache_base.LifeCache;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   : data which  the Cache Mode of every method or class
 *     version: 1.0
 * </pre>
 */

public class CacheMethod {
    private long mLifeTime;
    private boolean mFromNet;


    public CacheMethod(long mLifeTime,boolean mFromNet){
        this.mLifeTime = mLifeTime;
        this.mFromNet = mFromNet;
    }

    public static class Builder{
        private long mLifeTime;
        private CacheCore mCore;
        private Method mMethod;
        private boolean mFromNet;

        public Builder(CacheCore core,Method method){
            this.mCore = core;
            this.mMethod = method;
        }

        public CacheMethod build(){
            LifeCache life = mMethod.getAnnotation(LifeCache.class);
            long duaration = life.duaration();
            TimeUnit unit = life.unit();
            long time = unit.toSeconds(duaration);
            mFromNet = life.setFromNet();
            return new CacheMethod(time,mFromNet);
        }

    }
}
