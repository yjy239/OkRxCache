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
    public Observable intercept(Chain chain) {

        return  null;
    }


}
