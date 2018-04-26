package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.rx.core.Cache.CacheStrategy;
import com.yjy.okrxcache_core.rx.core.CacheResult;
import com.yjy.okrxcache_core.rx.core.Utils.Utils;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class NetWorkInterceptor<T> implements Interceptor {
    @Override
    public Observable intercept(final Chain chain) {

        Log.e("NetWorkInterceptor","NetWorkInterceptor");
        return  chain.request().getObservable().map(new Func1<Response<ResponseBody>, T>() {
            @Override
            public T call(Response<ResponseBody> responseBodyResponse)  {
                //可能需要处理无法用gosn转化的对象
                Log.e("header",responseBodyResponse.headers()+"");
                Gson gson = new Gson();
                JsonReader jsonReader = gson.newJsonReader(responseBodyResponse.body().charStream());
                TypeAdapter adapter = gson.getAdapter(TypeToken.get(Utils.getReturnType(chain.request()
                        .getMethod().getMethod().getGenericReturnType())));
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
