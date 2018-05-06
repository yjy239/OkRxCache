package com.yjy.okrxcache_core.Request;

import android.util.Log;

import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.Cache.Key.Key;
import com.yjy.okrxcache_core.CacheMethod;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.Convert.GsonConvert;
import com.yjy.okrxcache_core.Convert.IConvert;
import com.yjy.okrxcache_core.Engine.CacheEngine;
import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.Utils.Util;
import com.yjy.okrxcache_core.Utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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

    private List<Interceptor> mInterceptors = new ArrayList<>();
    private IConvert mConvert = new GsonConvert();
    private CacheStragry mCacheStagry = CacheStragry.ALL;
    private boolean isForce = true;
    private int mDiskSize = 0;

    private CacheEngine mEngine;
    private CacheResult result;
    private boolean isNetTime = false;
    private long mNetTime = 0;

    private boolean isHadGetCache = false;
    private Type mReturnType;


    public static <T>Request obtain(CacheEngine engine,List<Interceptor> interceptors, int  diskSize,
                                    IConvert convert,CacheStragry cacheStragry,
                                    boolean isForce){
        Request request =  REQUEST_POOL.poll();


        if(request == null){
            request = new Request();
        }else {
            request.clear();
            request.recycleall();
        }

        request.init(engine,interceptors,diskSize,convert,cacheStragry,isForce);


        return request;
    }

    public void init(CacheEngine engine,List<Interceptor> interceptors, int  diskSize,
                     IConvert convert,CacheStragry cacheStragry,
                     boolean isForce){
        this.mInterceptors = interceptors;
        this.mDiskSize = diskSize;
        this.mConvert = convert;
        this.mCacheStagry = cacheStragry;
        this.isForce = isForce;
        this.mEngine = engine;
    }


    public void init2(Request request,Key key,T data,boolean interceptor,Observable observable,CacheMethod mMethod){
        request.clear();
        request.recycle();

        this.key = key;
        this.data = data;
        this.interceptor = interceptor;
        this.observable = observable;
        this.mMethod = mMethod;
        if(mMethod != null&&mMethod.getMethod()!=null){
            Log.e("returnType",mMethod.getMethod()
                    .getGenericReturnType()+"");
            mReturnType = Utils.getReturnType(mMethod.getMethod().getGenericReturnType());
        }

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

    public List<Interceptor> getInterceptors() {
        return mInterceptors;
    }

    public CacheStragry getCacheStagry() {
        return mCacheStagry;
    }

    public int getDiskSize() {
        return mDiskSize;
    }

    public IConvert getConvert() {
        return mConvert;
    }


    public boolean isForce() {
        return isForce;
    }

    public CacheResult getResult() {
        return result;
    }

    public void setResult(CacheResult result) {
        this.result = result;
    }

    public boolean isNetTime() {
        return isNetTime;
    }

    public void setNetTime(boolean netTime) {
        isNetTime = netTime;
    }

    public long getNetTime() {
        return mNetTime;
    }

    public void setNetTime(long mNetTime) {
        this.mNetTime = mNetTime;
    }

    public boolean isHadGetCache() {
        return isHadGetCache;
    }

    public void setHadGetCache(boolean hadGetCache) {
        isHadGetCache = hadGetCache;
    }

    public Type getReturnType() {
        return mReturnType;
    }

    public void setReturnType(Type returnType) {
        this.mReturnType = returnType;
    }

    public void clear(){
        if(mEngine != null){
            mEngine.release(this);
        }
    }

    public void recycle(){
        this.key = null;
        this.data = null;
        this.interceptor = false;
        this.observable = null;
        this.mMethod = null;
        this.mReturnType = null;
    }


    public void recycleall(){
        this.key = null;
        this.data = null;
        this.interceptor = false;
        this.observable = null;
        this.mMethod = null;
        this.mInterceptors = null;
        this.mDiskSize = 0;
        this.mConvert = new GsonConvert();
        this.mCacheStagry = CacheStragry.ALL;
        isForce = true;
        this.result = null;
        this.mNetTime = 0;
        this.isNetTime = false;
        this.isHadGetCache = false;
    }
}
