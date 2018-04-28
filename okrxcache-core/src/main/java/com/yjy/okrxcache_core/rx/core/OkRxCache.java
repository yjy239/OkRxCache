package com.yjy.okrxcache_core.rx.core;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.DiskCache.InternalCacheDiskCacheFactory;
import com.yjy.okrxcache_core.rx.core.Convert.GsonConvert;
import com.yjy.okrxcache_core.rx.core.Convert.IConvert;
import com.yjy.okrxcache_core.rx.core.Engine.InterceptorMode;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

import rx.Observable;

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
    public <T>T create(Object orgin){

        if(mUsingClass == null){
            throw new IllegalArgumentException("you miss a proxy class,please set the using()");
        }
        ProcessHandler handler = new ProcessHandler(orgin,mCore);

        return (T)Proxy.newProxyInstance(orgin.getClass().getClassLoader(),new Class<?>[]{mUsingClass},handler);
    }


    /**
     * 通过key获取结果,无论是否过期
     * @param key
     * @return
     */
    public  Observable<CacheResult> get(String key){
        Observable orgin = Observable.just(key);
        return mCore.operator(orgin,key,InterceptorMode.GET);
    }


    /**
     * 将结果通过key保存在disk
     * @param key
     * @param data
     * @return
     */
    public  Observable<Boolean> put(String key, Object data,int lifetime){
        CacheResult cacheResult = new CacheResult(data,System.currentTimeMillis(),lifetime);
        Observable orgin = Observable.just(cacheResult);
        return mCore.operator(orgin,key, InterceptorMode.SAVE);
    }


    /**
     * 清空缓存
     * @param key
     * @return
     */
    public rx.Observable clear(String key){
        Observable orgin = Observable.just(key);
        return mCore.operator(orgin,key,InterceptorMode.CLEAR);
    }

    /**
     * 清除key对应的缓存
     * @param key
     * @return
     */
    public rx.Observable remove(String key){
        Observable orgin = Observable.just(key);
        return mCore.operator(orgin,key,InterceptorMode.REMOVE);
    }


    public static class Builder{
        private String mFilePath;
        private Class<?> mUsingClass;
        private CacheCore mCore;
        private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
        private DiskCache.Factory mDiskCacheFactory;
        private Context mContext;
        private int mDiskSize = 0;
        private IConvert mConvert = new GsonConvert();
        private CacheStragry mCacheStagry = CacheStragry.ALL;
        private boolean isForce = true;


        /**
         * 设置缓存目录。必须全路径
         * @param filePath
         * @return
         */
        public Builder setCacheDir(String filePath){
            this.mFilePath = filePath;
            return this;
        }

        /**
         * cache 需要代理的对象
         * @param usingClass
         * @return
         */
        public Builder using(Class<?> usingClass){
            this.mUsingClass = usingClass;
            return this;
        }

        /**
         * 添加拦截器
         * @param interceptor
         * @return
         */
        public Builder addInterceptor(Interceptor interceptor){
            mInterceptors.add(interceptor);
            return this;
        }

        /**
         * 传入context 要创建Cachedir,不然就需要filepath的完全路径
         * 最好传入context ，创建符合android规范的敌人，因为全路径名称可能会在某些目录无权限
         * @param context
         * @return
         */
        public Builder with(Context context){
            //不传递context的话这边就要考虑是否要关闭内存缓存
            //不传递的话无法处理内存过小的时候，我的memory lru map的缩小
            this.mContext = context;
            return this;
        }

        private long calute( long m){
            return m / 1024 / 1024;
        }

        /**
         * diskcache 大小
         * @param diskSize
         * @return
         */
        public Builder size(int diskSize){
            this.mDiskSize = diskSize;
            return this;
        }

        /**
         * 设置从缓存获取的数据的解码器和转化byte[] 器
         * @param convert
         * @return
         */
        public Builder setConvert(IConvert convert){
            this.mConvert = convert;
            return this;
        }

        /**
         * 缓存模式
         * @param cacheStagry
         * @return
         */
        public Builder setStragry(CacheStragry cacheStagry){
            this.mCacheStagry = cacheStagry;
            return this;
        }

        /**
         * 是否强制获取过期缓存
         * @param isForce
         * @return
         */
        public Builder force(boolean isForce){
            this.isForce = isForce;
            return this;
        }



        public OkRxCache build(){
            if(mConvert == null){
                mConvert = new GsonConvert();
            }
            if(mCacheStagry == null){
                mCacheStagry = CacheStragry.ALL;
            }
            mDiskCacheFactory = new InternalCacheDiskCacheFactory(mContext,mFilePath,mDiskSize);

            mCore = new CacheCore(mInterceptors,mDiskCacheFactory,
                    mConvert,mCacheStagry,isForce);
            return new OkRxCache(this);
        }

    }
}
