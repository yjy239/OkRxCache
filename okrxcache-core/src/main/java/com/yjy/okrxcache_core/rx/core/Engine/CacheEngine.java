package com.yjy.okrxcache_core.rx.core.Engine;

import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.CacheCore;
import com.yjy.okrxcache_core.rx.core.Convert.IConvert;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.DiskInterceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.MemoryInterceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.NetWorkInterceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.RealInterceptorChain;
import com.yjy.okrxcache_core.rx.core.Request.Request;

import java.util.ArrayList;

import rx.Observable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/25
 *     desc   :the engine of Cache
 *     version: 1.0
 *     目的是为了缓存的map挂载到CacheCore
 * </pre>
 */

public class CacheEngine<T> {

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();


    public CacheEngine(CacheCore core, ArrayList<Interceptor> mInterceptors, DiskCache diskCache
            , IConvert convert, CacheStragry cacheStagry){
        this.mInterceptors = mInterceptors;
        mInterceptors.add(new MemoryInterceptor(core,cacheStagry));
        mInterceptors.add(new DiskInterceptor(diskCache,convert,cacheStagry));
        mInterceptors.add(new NetWorkInterceptor());
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
