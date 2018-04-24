package com.yjy.okrxcache_core.rx.core;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.yjy.okexcache_base.LifeCache;
import com.yjy.okrxcache_core.rx.core.Cache.CacheStrategy;

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
    private Object mProxy;

    public ProcessHandler(Object usingClass,Object proxy,CacheCore core){
        this.mUsingClass = usingClass;
        this.mCore = core;
        this.mProxy = proxy;
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


            Observable<Response<ResponseBody>> proxy = null;
            if(method.getName().equals("getUsers")){
                Class<?>[] classes = new Class[2];
                classes[0] = int.class;
                classes[1]=int.class;
               Method proxyMethod =  mProxy.getClass().getMethod("getUsers",classes);
                proxy = (Observable<Response<ResponseBody>>)proxyMethod.invoke(mProxy,objects);
//                proxy.observeOn(Schedulers.io())
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(new Subscriber() {
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.e("error",e.toString());
//                            }
//
//                            @Override
//                            public void onNext(Object o) {
//                                Log.e("num","header : "+((Response<ResponseBody>)o).headers());
//                            }
//                        });
            }else {
                return null;
            }

            // the problem is how to get the header of the response which we can use to controller the life
            Observable observable = (Observable) method.invoke(mUsingClass,objects);
            method.getGenericReturnType();

//                    .observeOn(Schedulers.io())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new Subscriber() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Log.e("error",e.toString());
//                        }
//
//                        @Override
//                        public void onNext(Object o) {
//                            Log.e("num","header : "+(ArrayList)o);
//                        }
//                    });

            return proxy.map(new Func1<Response<ResponseBody>, T>() {
                @Override
                public T call(Response<ResponseBody> responseBodyResponse)  {
                    Gson gson = new Gson();
                    JsonReader jsonReader = gson.newJsonReader(responseBodyResponse.body().charStream());
                    TypeAdapter adapter = gson.getAdapter(TypeToken.get(getReturnType(method.getGenericReturnType())));
                    T o = null;
                    try {
                        o = (T)adapter.read(jsonReader);
                        Log.e("type",""+o.getClass());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return o;
                }
            }).compose(this.<T>transformeToCacheResult())
                    .map(new Func1<CacheResult<T>, T>() {
                        @Override
                        public T call(CacheResult<T> tCacheResult) {
                            return tCacheResult.getData();
                        }
                    });
        }


        return null;
    }

    //模仿rxjava获取returntype
    private Type getReturnType(Type returnType){
        Class<?> rawType = getRawType(returnType);
        boolean isSingle = rawType == Single.class;
        boolean isCompletable = rawType == Completable.class;
        if (rawType != Observable.class && !isSingle && !isCompletable) {
            return null;
        }

        boolean isResult = false;
        boolean isBody = false;
        Type responseType;
        if (!(returnType instanceof ParameterizedType)) {
            String name = isSingle ? "Single" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }

        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized"
                        + " as Response<Foo> or Response<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
        } else if (rawObservableType == Result.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Result must be parameterized"
                        + " as Result<Foo> or Result<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            isResult = true;
        } else {
            responseType = observableType;
            isBody = true;
        }
        return responseType;
    }

    private static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
            throw new IllegalArgumentException(
                    "Index " + index + " not in range [0," + types.length + ") for " + type);
        }
        Type paramType = types[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
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
