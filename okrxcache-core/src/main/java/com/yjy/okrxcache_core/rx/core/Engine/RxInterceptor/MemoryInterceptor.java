package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;


import android.util.Log;

import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.CacheResult;
import com.yjy.okrxcache_core.rx.core.Engine.InterceptorMode;

import rx.Observable;
import rx.functions.Func1;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/26
 *     desc   :interceptor for MemoryInterceptor
 *     version: 1.0
 * </pre>
 */

public class MemoryInterceptor<T> implements Interceptor {

    private CacheStragry mCacheStagry;
    private int mMode = 0;

    public MemoryInterceptor(CacheStragry cacheStagry){
        this.mCacheStagry = cacheStagry;
    }

    @Override
    public Observable intercept(Interceptor.Chain chain) {
        Log.e("MemoryInterceptor","MemoryInterceptor");
        chain.request();


//        if(mMode == InterceptorMode.SAVE){
//            return chain.process();
//        }else if(mMode == InterceptorMode.GET){
//            return chain.process();
//        }

        if(mMode == InterceptorMode.RUN){
            return getRealData(chain.process());
        }




        return chain.process();
    }

    @Override
    public void setMode(int mode) {
        this.mMode = mode;
    }

    /**
     * 转化为真正的result
     * @param observable
     * @return
     */
    private Observable getRealData(Observable observable){
        return observable.map(new Func1<CacheResult<T>, T>() {
            @Override
            public T call(CacheResult<T> tCacheResult) {
                return tCacheResult.getData();
            }
        });
    }
}
