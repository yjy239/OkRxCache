package com.yjy.okrxcache_core.Engine.RxInterceptor;

import com.yjy.okrxcache_core.Request.Request;


import io.reactivex.Observable;

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

    void setMode(int mode);

    interface Chain<T>{


        Request request();

        Observable process();

    }
}
