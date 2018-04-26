package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;


import android.util.Log;

import com.yjy.okrxcache_core.rx.core.Engine.Request;

import rx.Observable;

/**
 * Created by yjy on 2018/4/23.
 */

public class MemoryInterceptor implements Interceptor {

    @Override
    public Observable intercept(Interceptor.Chain chain) {
        Log.e("MemoryInterceptor","MemoryInterceptor");
        chain.request();



        return chain.process();
    }
}
