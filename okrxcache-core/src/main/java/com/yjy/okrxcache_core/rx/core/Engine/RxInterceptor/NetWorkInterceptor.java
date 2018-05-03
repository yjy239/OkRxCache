package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
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
 *     desc   :interceptor for network
 *     version: 1.0
 * </pre>
 */

public class NetWorkInterceptor<T> implements Interceptor {
    private int mMode = 0;

    public NetWorkInterceptor(){

    }

    @Override
    public Observable intercept(final Chain chain) {

//        Log.e("NetWorkInterceptor","NetWorkInterceptor");
        return  chain.request().getObservable().map(new Func1<Response<ResponseBody>, T>() {
            @Override
            public T call(Response<ResponseBody> responseBodyResponse)  {
                //可能需要处理无法用gosn转化的对象

                if(chain.request().getMethod().isNetContronller()){
//                    Log.e("header",responseBodyResponse.headers()+"");
                    chain.request().setNetTime(true);
                }
                Gson gson = new Gson();
                JsonReader jsonReader = gson.newJsonReader(responseBodyResponse.body().charStream());
                TypeAdapter adapter = gson.getAdapter(TypeToken.get(chain.request()
                        .getReturnType()));
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
        });
    }

    @Override
    public void setMode(int mode) {
        this.mMode = mode;
    }


}
