package com.yjy.okrxcache_core.rx.core;

import android.content.Context;

import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskLruCache;
import com.yjy.okrxcache_core.rx.core.Cache.DisCache.InternalCacheDiskCacheFactory;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.MemoryInterceptor;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

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


    //(T)Proxy.newProxyInstance(orgin.getClass().getClassLoader(),new Class<?>[]{mUsingClass},handler)
    //此处为核心。我们将开始动态代理
    public <T>T create(Object orgin){

        ProcessHandler handler = new ProcessHandler(orgin,mCore);

        return (T)Proxy.newProxyInstance(orgin.getClass().getClassLoader(),new Class<?>[]{mUsingClass},handler);
    }


    public static class Builder{
        private String mFilePath;
        private Class<?> mUsingClass;
        private CacheCore mCore;
        private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
        private DiskCache.Factory mDiskCacheFactory;
        private Context mContext;
        private int mDiskSize = 0;

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

        public Builder with(Context context){
            this.mContext = context;
            return this;
        }

        public Builder size(int diskSize){
            this.mDiskSize = diskSize;
            return this;
        }

        public OkRxCache build(){

            mDiskCacheFactory = new InternalCacheDiskCacheFactory(mContext,mFilePath,mDiskSize);
            mCore = new CacheCore(mInterceptors,mDiskCacheFactory);
            return new OkRxCache(this);
        }

    }
}
