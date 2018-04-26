package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;

import com.yjy.okrxcache_core.rx.core.CacheResult;

import java.util.Observable;

import rx.Subscriber;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class RealInterceptorChain<T> implements Interceptor.Chain {
    @Override
    public void onInterceptor() {

    }

    @Override
    public Observable request() {
        return null;
    }

    @Override
    public CacheResult<T> process() {
        return null;
    }
}
