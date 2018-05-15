package com.yjy.okrxcache_core.Engine.RequestHandler;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/05/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class OkHttpNetWorkHandler implements RequestHandler {

    private OkHttpClient mClient;

    public OkHttpNetWorkHandler(OkHttpClient okHttpClient){
        this.mClient = okHttpClient;
    }

    @Override
    public <T> Observable load(final Interceptor.Chain chain){
        return chain.request().getObservable().map(new Func1<Request, Response>() {
            @Override
            public Response call(Request request) {
                if(mClient == null){
                    return new Response.Builder().build();
                }

                Response response = null;
                try {
                    response = mClient.newCall(request).execute();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return response;
            }
        }).map(new Func1<Response,T>() {
            @Override
            public T call(Response response) {
                if(chain.request().getReturnType() == null ){
                    T result = null;
                    try {
                        result = (T)response.body().string();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return (T)result;
                }
                Gson gson = new Gson();
                JsonReader jsonReader = gson.newJsonReader(response.body().charStream());
                TypeAdapter adapter = gson.getAdapter(TypeToken.get(chain.request()
                        .getReturnType()));
                T result = null;
                try {
                    result = (T)adapter.read(jsonReader);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return result;
            }
        });
    }
}
