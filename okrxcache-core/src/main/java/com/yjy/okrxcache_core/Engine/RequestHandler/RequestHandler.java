package com.yjy.okrxcache_core.Engine.RequestHandler;

import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;

import rx.Observable;


/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/05/05
 *     desc   : 策略模式，两种不同的网络获取策略
 *     version: 1.0
 * </pre>
 */
public interface RequestHandler {

    <T> Observable load(Interceptor.Chain chain);
}
