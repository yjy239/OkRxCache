package com.yjy.okrxcache_core.Engine.RequestHandler;

import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.Request.Request;

import rx.Observable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/05/05
 *     desc   :原始的Observable的网络请求
 *     version: 1.0
 * </pre>
 */
public class OrginNetWorkHandler implements RequestHandler {

    @Override
    public <T>Observable load(Interceptor.Chain chain) {
        return chain.request().getObservable();
    }
}
