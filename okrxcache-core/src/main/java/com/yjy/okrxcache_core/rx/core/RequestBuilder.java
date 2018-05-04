package com.yjy.okrxcache_core.rx.core;

import android.content.Context;

import com.yjy.okrxcache_base.AutoCache;
import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Convert.GsonConvert;
import com.yjy.okrxcache_core.rx.core.Convert.IConvert;
import com.yjy.okrxcache_core.rx.core.Engine.InterceptorMode;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.rx.core.Request.Request;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;

import rx.Observable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RequestBuilder {

    private Class<?> mUsingClass;
    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private CacheCore mCore;
    private DiskCache.Factory mDiskCacheFactory;
    private Context mContext;
    private int mDiskSize = 0;
    private IConvert mConvert = new GsonConvert();
    private CacheStragry mCacheStagry = CacheStragry.ALL;
    private boolean isForce = true;
    private OkRxCache mOkRxCache;


    public RequestBuilder(Context context){
        this.mContext = context;
        mOkRxCache =  OkRxCache.get(context);
        mCore = mOkRxCache.getCore();
    }

//    /**
//     * 设置缓存目录。必须全路径
//     * @param filePath
//     * @return
//     */
//    public RequestBuilder setCacheDir(String filePath){
//        this.mFilePath = filePath;
//        return this;
//    }

    /**
     * cache 需要代理的对象
     * @param usingClass
     * @return
     */
    public RequestBuilder using(Class<?> usingClass){
        this.mUsingClass = usingClass;
        return this;
    }

    /**
     * 添加拦截器
     * @param interceptor
     * @return
     */
    public RequestBuilder addInterceptor(Interceptor interceptor){
        mInterceptors.add(interceptor);
        return this;
    }


    /**
     * diskcache 大小
     * @param diskSize
     * @return
     */
    public RequestBuilder size(int diskSize){
        this.mDiskSize = diskSize;
        return this;
    }

    /**
     * 设置从缓存获取的数据的解码器和转化byte[] 器
     * @param convert
     * @return
     */
    public RequestBuilder setConvert(IConvert convert){
        this.mConvert = convert;
        return this;
    }

    /**
     * 缓存模式
     * @param cacheStagry
     * @return
     */
    public RequestBuilder setStragry(CacheStragry cacheStagry){
        this.mCacheStagry = cacheStagry;
        return this;
    }

    /**
     * 是否强制获取过期缓存
     * @param isForce
     * @return
     */
    public RequestBuilder force(boolean isForce){
        this.isForce = isForce;
        return this;
    }

    public <T>Request build(){
        return Request.obtain(mOkRxCache.getEngine(),mInterceptors,
                mDiskSize,mConvert,mCacheStagry,isForce);
    }

    //此处为核心。我们将开始动态代理
    public <T>T create(Object orgin){
        if(mUsingClass == null){
            throw new IllegalArgumentException("you miss a proxy class,please set the using()");
        }
        AutoCache cache = mUsingClass.getAnnotation(AutoCache.class);

        Request request = build();
        ProcessHandler handler = new ProcessHandler(orgin,mCore,request,cache);

        return (T) Proxy.newProxyInstance(orgin.getClass().getClassLoader(),new Class<?>[]{mUsingClass},handler);
    }


    /**
     * 通过key获取结果,无论是否过期
     * @param key
     * @return
     */
    public Observable<CacheResult> get(String key,Type type){
        Observable orgin = Observable.just(key);
        Request request = build();
        return mCore.operator(orgin,key, InterceptorMode.GET,request,type);
    }


    /**
     * 将结果通过key保存在disk
     * @param key
     * @param data
     * @return
     */
    public  <T>Observable<Boolean> put(String key, T data,int lifetime){
        CacheResult cacheResult = new CacheResult(data,System.currentTimeMillis(),lifetime);
        Observable orgin = Observable.just(cacheResult);
        Request request = build();
        return mCore.operator(orgin,key, InterceptorMode.SAVE,request,null);
    }


    /**
     * 清空缓存
     * @return
     */
    public rx.Observable<Boolean> clear(){
        Observable orgin = Observable.just(false);
        Request request = build();
        return mCore.operator(orgin,null,InterceptorMode.CLEAR,request,null);
    }

    /**
     * 清除key对应的缓存
     * @param key
     * @return
     */
    public rx.Observable<Boolean> remove(String key){
        Observable orgin = Observable.just(key);
        Request request = build();
        return mCore.operator(orgin,key,InterceptorMode.REMOVE,request,null);
    }

}
