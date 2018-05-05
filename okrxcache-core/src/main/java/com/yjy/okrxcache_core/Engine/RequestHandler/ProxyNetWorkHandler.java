package com.yjy.okrxcache_core.Engine.RequestHandler;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;



import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/05/05
 *     desc   :代理时候调用的，擦除对象
 *     version: 1.0
 * </pre>
 */
public class ProxyNetWorkHandler<T> implements RequestHandler{


    @Override
    public <T> Observable load(final Interceptor.Chain chain) {
        return  chain.request().getObservable().map(new Func1<Response<ResponseBody>, T>() {
            @Override
            public T call(Response<ResponseBody> responseBodyResponse)  {
                //可能需要处理无法用gosn转化的对象

                if(chain.request().getMethod().isNetContronller()){
                    Log.e("header",responseBodyResponse.headers()+"");
                    chain.request().setNetTime(true);
                }
                Gson gson = new Gson();
                JsonReader jsonReader = gson.newJsonReader(responseBodyResponse.body().charStream());
                TypeAdapter adapter = gson.getAdapter(TypeToken.get(chain.request()
                        .getReturnType()));
                T o = null;
                try {
                    o = (T)adapter.read(jsonReader);
//                    Log.e("type",""+o.getClass());
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
}
