package com.yjy.okrxcache_core.rx.core;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.yjy.okexcache_base.LifeCache;
import com.yjy.okrxcache_core.rx.core.Cache.CacheStrategy;
import com.yjy.okrxcache_core.rx.core.Utils.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.google.gson.internal.$Gson$Types.getRawType;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :the dymc of rxcache
 *     version: 1.0
 * </pre>
 */

public class ProcessHandler<T> implements InvocationHandler {
    private Object mUsingClass;
    private CacheCore mCore;
    private HashMap<Method,CacheMethod> mCacheMethodMap = new HashMap<>();


    public ProcessHandler(Object usingClass,CacheCore core){
        this.mUsingClass = usingClass;
        this.mCore = core;
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
                    cacheMethod = loadCacheMethod(method);
                    break;
                }
            }

            if(cacheMethod == null){
                return method.invoke(mUsingClass,objects);
            }
            // the problem is how to get the header of the response which we can use to controller the life,
            Class<?>[] types = method.getParameterTypes();

            Method proxyMethod = mUsingClass.getClass().getMethod(method.getName()+"$$proxy",types);

            Observable observable = (Observable) proxyMethod.invoke(mUsingClass,objects);

            return mCore.start(observable,cacheMethod);
        }
        return method.invoke(mUsingClass,objects);
    }





    private CacheMethod loadCacheMethod(Method method){
        CacheMethod result = mCacheMethodMap.get(method);
        if(result != null){
            return result;
        }

        synchronized (mCacheMethodMap){
            result = mCacheMethodMap.get(method);
            if (result == null) {
                result = new CacheMethod.Builder(mCore, method).build();
                mCacheMethodMap.put(method, result);
            }

        }
        return result;
    }


}
