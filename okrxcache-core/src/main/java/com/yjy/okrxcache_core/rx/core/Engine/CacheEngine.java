package com.yjy.okrxcache_core.rx.core.Engine;

import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.RealInterceptorChain;

import java.util.ArrayList;

import rx.Observable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/25
 *     desc   :the engine of Cache
 *     version: 1.0
 * </pre>
 */

public class CacheEngine<T> {

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();


    public CacheEngine(ArrayList<Interceptor> mInterceptors,DiskCache.Factory diskFactory){
        this.mInterceptors = mInterceptors;
    }

    /**
     * 拦截器普通模式运行
     * @param request
     * @param <T>
     * @return
     */
    public <T>Observable run(final Request request){
        RealInterceptorChain chain = new RealInterceptorChain(mInterceptors,0,request,InterceptorMode.RUN);
        return chain.process();
    }

    /**
     * 操作缓存的时候,拦截器以其他方式运行
     * @param request
     * @param mode
     * @param <T>
     * @return
     */
    public <T>Observable operator(Request request,int mode){
        RealInterceptorChain chain = new RealInterceptorChain(mInterceptors,0,request,mode);
        return chain.process();

    }



}
