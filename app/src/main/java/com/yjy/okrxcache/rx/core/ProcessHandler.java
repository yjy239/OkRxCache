package com.yjy.okrxcache.rx.core;

import android.util.Log;

import com.yjy.okrxcache.rx.Annonation.LifeCache;
import com.yjy.okrxcache.rx.core.Cache.CacheStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import rx.Observable;
import rx.functions.Func1;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :
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
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Log.e("ProcessHandler","method "+method.getName()+" objects"+objects[0]);
        //动态代理生成CacheMethod
        CacheMethod cacheMethod = null;

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

            Observable observable = (Observable) method.invoke(mUsingClass,objects);
            return observable.compose(this.<T>transformeToCacheResult())
                    .map(new Func1<CacheResult<T>, T>() {
                        @Override
                        public T call(CacheResult<T> tCacheResult) {
                            return tCacheResult.getData();
                        }
                    });
        }


        return null;
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

//    public <T>Observable excute(final Observable<T> observable){
////        if(mUsingClass == null){
////            throw new IllegalArgumentException("必须传入Retrfit的API接口");
////        }
//
//        //我最后要用map转化为下流
//        return observable.compose(this.<T>transformeToCacheResult())
//                .map(new Func1<CacheResult<T>, T>() {
//                    @Override
//                    public T call(CacheResult<T> tCacheResult) {
//                        return tCacheResult.getData();
//                    }
//                });
//    }

    //先转化为cacheresult的observable
//    private <T>Observable.Transformer<T,CacheResult<T>> transformeToCacheResult(){
//        return new Observable.Transformer<T, CacheResult<T>>() {
//            @Override
//            public Observable<CacheResult<T>> call(final Observable<T> tObservable) {
//                return mCore.loadResource(tObservable);
//            }
//        };
//    }

    private <T>Observable.Transformer<T,CacheResult<T>> transformeToCacheResult(){
        return new Observable.Transformer<T, CacheResult<T>>() {
            @Override
            public Observable<CacheResult<T>> call(Observable<T> tObservable) {

                return tObservable.map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T t) {
                        Log.e("transformeToCacheResult","toResult");
                        return new CacheResult(t, CacheStrategy.DISK,false);
                    }
                });
            }
        };
    }
}
