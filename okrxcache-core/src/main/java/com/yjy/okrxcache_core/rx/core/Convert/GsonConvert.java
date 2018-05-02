package com.yjy.okrxcache_core.rx.core.Convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.rx.core.CacheResult;
import com.yjy.okrxcache_core.rx.core.OkRxCache;
import com.yjy.okrxcache_core.rx.core.Utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class GsonConvert implements IConvert{

    private Gson mGson;
    public GsonConvert(){
        this.mGson = new Gson();
    }

    @Override
    public byte[] getBytes(CacheResult cacheResult) {
        String result = mGson.toJson(cacheResult);
        return result.getBytes();
    }

    @Override
    public CacheResult setResult(File cacheFile, Method method) {
        FileReader reader = null;
        CacheResult result = null;
        try {
            reader = new FileReader(cacheFile);
            JsonReader jsonReader = mGson.newJsonReader(reader);
            final Type token = Utils.getReturnType(method.getGenericReturnType());
            TypeToken objectType = TypeToken.getParameterized(CacheResult.class,token);
            TypeAdapter adapter = mGson.getAdapter(objectType);
            result = (CacheResult) adapter.read(jsonReader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return result;
    }
}
