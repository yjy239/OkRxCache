package com.yjy.okrxcache_core.rx.core.Engine;

import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.CacheMethod;

import java.lang.reflect.Method;

import rx.Observable;


/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/26
 *     desc   :数据流包裹类
 *     version: 1.0
 * </pre>
 */

public class Request<T> {

    private Key key;

    private T data;

    private boolean interceptor = false;

    private Observable observable;

    private CacheMethod mMethod;

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
}
