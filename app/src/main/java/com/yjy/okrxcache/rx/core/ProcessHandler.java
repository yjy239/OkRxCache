package com.yjy.okrxcache.rx.core;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ProcessHandler implements InvocationHandler {
    private Object mUsingClass;


    public ProcessHandler(Object usingClass){
        this.mUsingClass = usingClass;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Log.e("ProcessHandler","00000000");
        //动态代理生成ServiceMethod

        return method.invoke(mUsingClass,objects);
    }
}
