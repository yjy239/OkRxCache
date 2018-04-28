package com.yjy.okrxcache_core.rx.core.Request;

import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.CacheMethod;
import com.yjy.okrxcache_core.rx.core.Utils.Util;

import java.lang.reflect.Method;
import java.util.Queue;

import rx.Observable;


/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/26
 *     desc   :数据流包裹类,最好享元模式
 *     version: 1.0
 * </pre>
 */

public class Request<T> {

    private Key key;

    private T data;

    private boolean interceptor = false;

    private Observable observable;

    private CacheMethod mMethod;

    private static final Queue<Request> REQUEST_POOL = Util.createQueue(0);


    public static <T>Request obtain(Key key,T data,boolean interceptor,Observable observable,CacheMethod mMethod){
        Request request =  REQUEST_POOL.poll();

        if(request == null){
            request = new Request();
        }else {
            request.recycle();
        }

        request.init(key,data,interceptor,observable,mMethod);

        return request;
    }


    public void init(Key key,T data,boolean interceptor,Observable observable,CacheMethod mMethod){
        this.key = key;
        this.data = data;
        this.interceptor = interceptor;
        this.observable = observable;
        this.mMethod = mMethod;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isInterceptor() {
        return interceptor;
    }

    public void setInterceptor(boolean interceptor) {
        this.interceptor = interceptor;
    }

    public Observable getObservable() {
        return observable;
    }

    public void setObservable(Observable observable) {
        this.observable = observable;
    }

    public CacheMethod getMethod() {
        return mMethod;
    }

    public void setMethod(CacheMethod mMethod) {
        this.mMethod = mMethod;
    }


    public void recycle(){
        this.key = null;
        this.data = null;
        this.interceptor = false;
        this.observable = null;
        this.mMethod = null;
    }
}
