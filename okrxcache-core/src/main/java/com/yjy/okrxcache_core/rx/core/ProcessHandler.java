package com.yjy.okrxcache_core.rx.core;

import android.util.Log;

import com.yjy.okexcache_base.LifeCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.rx.core.Request.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

import rx.Observable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :the dymc of rxcache
 *     version: 1.0
 * </pre>
 */

class ProcessHandler<T> implements InvocationHandler {
    private Object mUsingClass;
    private CacheCore mCore;
    private HashMap<Method,CacheMethod> mCacheMethodMap = new HashMap<>();
    private Request mRequest;


    public ProcessHandler(Object usingClass,CacheCore core,Request request){
        this.mUsingClass = usingClass;
        this.mCore = core;
        this.mRequest = request;
    }

    @Override
    public Object invoke(Object o, final Method method, Object[] objects) throws Throwable {
        Log.e("ProcessHandler","method "+method.getName()+" objects"+objects[0]);
        //动态代理生成CacheMethod
        CacheMethod cacheMethod = null;

        //
        if(method.getReturnType() == Observable.class){
            for(Annotation annotation : method.getDeclaredAnnotations()){
                if(annotation instanceof LifeCache){
                    cacheMethod = loadCacheMethod(method,objects);
                    break;
                }
            }


            if(cacheMethod == null){
                return method.invoke(mUsingClass,objects);
            }
            // the problem is how to get the header of the response which we can use to controller the life,
            Class<?>[] types = method.getParameterTypes();

            Method proxyMethod = mUsingClass.getClass().getMethod(method.getName()+"$$proxy",types);
            if(proxyMethod == null){
                Log.e("OkRxCache","create interface failed,we couldn,t use okrxcache,please check your interface");
                return method.invoke(mUsingClass,objects);
            }

            Observable observable = (Observable) proxyMethod.invoke(mUsingClass,objects);



            return mCore.start(observable,cacheMethod,mRequest);

        }
        Log.e("ProcessHandler0","method "+method.getName()+" objects"+objects[0]);
        return method.invoke(mUsingClass,objects);
    }





    private CacheMethod loadCacheMethod(Method method,Object[] objs){
        CacheMethod result = mCacheMethodMap.get(method);
        if(result != null){
            return result;
        }

        synchronized (mCacheMethodMap){
            result = mCacheMethodMap.get(method);
            if (result == null) {
                result = new CacheMethod.Builder(mCore, method,objs).build();
                mCacheMethodMap.put(method, result);
            }
        }

        result.process(objs);
        return result;
    }


}
