package com.yjy.okrxcache.rx.core.RxInterceptor;

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

    void intercept(Chain chain) throws Exception;

    interface Chain{
        void onInterceptor();
    }
}
