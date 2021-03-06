package com.yjy.okrxcache_core.Convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.Utils.Utils;

import java.io.File;
import java.io.FileReader;
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
    public CacheResult setResult(File cacheFile, Type returnType) {
        FileReader reader = null;
        CacheResult result = null;
        try {
            reader = new FileReader(cacheFile);
            JsonReader jsonReader = mGson.newJsonReader(reader);
            TypeToken objectType = null;
            if(returnType != null){
                objectType = TypeToken.getParameterized(CacheResult.class,returnType);
            }else {
                objectType = TypeToken.get(CacheResult.class);
            }
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
