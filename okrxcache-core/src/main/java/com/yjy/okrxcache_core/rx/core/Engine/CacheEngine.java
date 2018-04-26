package com.yjy.okrxcache_core.rx.core.Engine;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.rx.core.Cache.CacheStrategy;
import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.EmptySignature;
import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.rx.core.CacheMethod;
import com.yjy.okrxcache_core.rx.core.CacheResult;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.RealInterceptorChain;
import com.yjy.okrxcache_core.rx.core.Utils.Utils;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/25
 *     desc   :the engine of Cache
 *     version: 1.0
 * </pre>
 * 拦截请求，获取缓存，如果gson转化失败了，直接做一次网络请求
 */

public class CacheEngine {

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private DiskCache.Factory mDiskFactory;


    public CacheEngine(ArrayList<Interceptor> mInterceptors,DiskCache.Factory diskFactory){
        this.mInterceptors = mInterceptors;
        this.mDiskFactory = diskFactory;
    }

    public <T>Observable run(Observable observable, final CacheMethod method){
//        RealInterceptorChain chain = new RealInterceptorChain(mInterceptors,0,request);
//        Observable chainObservale = chain.process();
        return observable.map(new Func1<Response<ResponseBody>, T>() {
            @Override
            public T call(Response<ResponseBody> responseBodyResponse)  {
                //可能需要处理无法用gosn转化的对象
                Log.e("header",responseBodyResponse.headers()+"");
                Gson gson = new Gson();
                JsonReader jsonReader = gson.newJsonReader(responseBodyResponse.body().charStream());
                TypeAdapter adapter = gson.getAdapter(TypeToken.get(Utils.getReturnType(method.getMethod().getGenericReturnType())));
                T o = null;
                try {
                    o = (T)adapter.read(jsonReader);
                    Log.e("type",""+o.getClass());
                }catch (Exception e){

                    e.printStackTrace();
                }
                return o;
            }
        }).onErrorReturn(new Func1() {
            @Override
            public Object call(Object o) {
                return "error";
            }
        }).compose(this.<T>transformeToCacheResult())
                .map(new Func1<CacheResult<T>, T>() {
                    @Override
                    public T call(CacheResult<T> tCacheResult) {
                        return tCacheResult.getData();
                    }
                });
    }



    private Observable getDataFromNet(Observable observable){
        return observable;
    }


    private Observable getDataFromInterceptor(Observable observable){

        return observable;
    }



    private <T>Observable.Transformer<T,CacheResult<T>> transformeToCacheResult(){
        return new Observable.Transformer<T, CacheResult<T>>() {
            @Override
            public Observable<CacheResult<T>> call(Observable<T> tObservable) {

                return tObservable.map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T t) {
                        Log.e("transformeToCacheResult","toResult");
                        return new CacheResult(t, CacheStrategy.MEMORY,System.currentTimeMillis(),11) ;
                    }
                });
            }
        };
    }


}
