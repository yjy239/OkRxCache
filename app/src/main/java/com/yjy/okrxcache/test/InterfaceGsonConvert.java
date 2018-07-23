package com.yjy.okrxcache.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.Convert.IConvert;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/07/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class InterfaceGsonConvert implements IConvert {

    private Gson mGson;
    public InterfaceGsonConvert(){
        this.mGson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ITest.class,new TestTypeAdapter())
                .create();
    }

    @Override
    public byte[] getBytes(CacheResult cacheResult) {
        String result = mGson.toJson(cacheResult);
        return result.getBytes();
    }

    @Override
    public CacheResult setResult(File file, Type returnType) {
        FileReader reader = null;
        CacheResult result = null;
        try {
            reader = new FileReader(file);
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
