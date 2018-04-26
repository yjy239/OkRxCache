package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;

import com.yjy.okrxcache_core.rx.core.CacheResult;
import com.yjy.okrxcache_core.rx.core.Engine.Request;


import rx.Observable;
import rx.Subscriber;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface Interceptor {

    Observable intercept(Interceptor.Chain chain);

    interface Chain<T>{

        Request request();

        Observable process();

    }
}
