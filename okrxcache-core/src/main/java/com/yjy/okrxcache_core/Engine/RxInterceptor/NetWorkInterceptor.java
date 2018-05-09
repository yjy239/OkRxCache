package com.yjy.okrxcache_core.Engine.RxInterceptor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.Engine.RequestHandler.RequestHandler;
import com.yjy.okrxcache_core.Utils.LogUtils;
import com.yjy.okrxcache_core.Utils.Utils;

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

    private RequestHandler mHandler;

    public NetWorkInterceptor(RequestHandler handler){
        this.mHandler = handler;
    }

    @Override
    public Observable intercept(final Chain chain) {

        LogUtils.getInstance().e("okrxcache","NetWorkInterceptor wait to request");


        if(mHandler == null){
            return chain.request().getObservable();
        }

        return mHandler.load(chain);
    }

    @Override
    public void setMode(int mode) {
        this.mMode = mode;
    }


}
