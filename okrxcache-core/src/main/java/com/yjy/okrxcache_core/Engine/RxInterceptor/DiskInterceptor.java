package com.yjy.okrxcache_core.Engine.RxInterceptor;


import android.util.Log;

import com.google.gson.Gson;
import com.yjy.okrxcache_core.Cache.ByteArrayPool;
import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.Cache.Key.Key;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.Convert.IConvert;
import com.yjy.okrxcache_core.Engine.CacheBack;
import com.yjy.okrxcache_core.Engine.InterceptorMode;
import com.yjy.okrxcache_core.Request.Request;
import com.yjy.okrxcache_core.Utils.LogUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.Observable;


/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/26
 *     desc   :interceptor for Disk
 *     version: 1.0
 * </pre>
 */

public class DiskInterceptor<T> implements Interceptor {

    private DiskCache diskCache;
    private IConvert mConvert;
    private CacheStragry mCacheStagry;
    private int mMode = 0;

    private static final String TAG = "DiskInterceptor";

    public DiskInterceptor(DiskCache diskCache,IConvert convert,CacheStragry cacheStagry){
        this.diskCache = diskCache;
        this.mConvert = convert;
        this.mCacheStagry = cacheStagry;
    }

    @Override
    public Observable intercept(Interceptor.Chain chain) {
//        Log.e("DiskInterceptor","DiskInterceptor");
        final Request request = chain.request();

        //获取disk中的数据
        Observable loadDiskObservable = loadFromDiskCache(request);

        //判断拦截器执行模式
        if(mMode == InterceptorMode.GET){
            if(request.isHadGetCache()){
                return request.getObservable();
            }
            return loadDiskObservable;
        }else if(mMode == InterceptorMode.SAVE){
            return request.getObservable().compose(saveResultIsSuccess(request));
        }else if(mMode == InterceptorMode.REMOVE){
            return request.getObservable().compose(deleteResultIsSuccess(request));
        }else if(mMode == InterceptorMode.CLEAR){
            return request.getObservable().compose(clearCacheIsSuccess(request));
        }

        if(mCacheStagry == CacheStragry.ALL){
            //优先缓存显示,之后会显示网络
            //一旦发现request告诉你已经拿到缓存了，没必要再从disk中获取
            return Observable.merge(loadDiskObservable,transFormToCache(chain.process(),request));
        }else if(mCacheStagry == CacheStragry.FIRSTCACHE){
            //优先显示缓存，找到了就不找网络
            return loadDiskObservable.switchIfEmpty(transFormToCache(chain.process(),request));
        }else if(mCacheStagry == CacheStragry.ONLYNETWORK){
            //只获取网络
            return transFormToCache(chain.process(),request);
        }else if(mCacheStagry == CacheStragry.ONLYDISK){
            return loadDiskObservable;
        }else if(mCacheStagry == CacheStragry.NOMEMORY){
            return Observable.merge(loadDiskObservable,transFormToCache(chain.process(),request));
        }else if(mCacheStagry == CacheStragry.NODISK){
            return transFormToCache(chain.process(),request);
        }


        return Observable.merge(loadDiskObservable,transFormToCache(chain.process(),request));
    }

    @Override
    public void setMode(int mode) {
        this.mMode = mode;
    }

    /**
     * 把responsebody转化为cacheresult
     * @param observable
     * @param request
     * @param <T>
     * @return
     */
    private <T>Observable transFormToCache(Observable observable,Request request){
        return observable.compose(this.<T>transformToCacheResult(request));
    }

    /**
     * 从disk中读取
     * @param request
     * @return
     */
    private Observable loadFromDiskCache(final Request request){
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> subscriber) throws Exception {
                if(!request.isHadGetCache()){
                    CacheResult result = loadFromDisk(request.getKey(),request.getReturnType());
                    if(result == null){
                        CacheResult empty = new CacheResult(null,0,0);
                        if(mMode == InterceptorMode.GET){
                            subscriber.onNext(empty);
                        }
                    }else {
                        result.setFromCache(CacheBack.DISK);
                        LogUtils.getInstance().e("okrxcache :"+request.getKey()," DiskInterceptor: loadFromDiskCache"+result.toString());
                        subscriber.onNext(result);
                    }
                }

                subscriber.onComplete();
            }

        });
    }

    /**
     * 网络拦截器返回之后，转化为cacheresult，并且保存
     * @param request
     * @param <T>
     * @return
     */
    private <T>ObservableTransformer<T,CacheResult<T>> transformToCacheResult(final Request request){
        return new ObservableTransformer<T, CacheResult<T>>() {

            @Override
            public ObservableSource<CacheResult<T>> apply(Observable<T> tObservable) {

                return tObservable.map(new Function<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(T t) {
//                        Log.e("DiskInterceptor","transformeToCacheResult");
                        CacheResult result = null;
                        request.setResult(result);
                        result = new CacheResult(t,System.currentTimeMillis(),
                                request.getMethod().getLifeTime());
                        result.setFromCache(CacheBack.NETWORK);
                        request.setResult(result);
                        save2DiskCache(request.getKey(),result);
                        LogUtils.getInstance().e("okrxcache :"+request.getKey()," DiskInterceptor: save2DiskCache : "+true);
                        return result;
                    }
                });
            }
        };
    }

    /**
     * 保存成功之后的转化器
     * @param request
     * @param <T>
     * @return
     */
    private <T>ObservableTransformer<T,Boolean> saveResultIsSuccess(final Request request){
        return new ObservableTransformer<T, Boolean>() {
            @Override
            public Observable<Boolean> apply(Observable<T> tObservable) {

                return tObservable.map(new Function<T, Boolean>() {
                    @Override
                    public Boolean apply(T t) throws Exception {
                        LogUtils.getInstance().e("okrxcache :"+request.getKey()," DiskInterceptor opterator put: save2DiskCache");
                        request.setResult((CacheResult) t);
                        return save2DiskCache(request.getKey(),(CacheResult) t);
                    }
                });
            }
        };
    }

    /**
     * 删除成功的转化器
     * @param request
     * @param <T>
     * @return
     */
    private <T>ObservableTransformer<T,Boolean> deleteResultIsSuccess(final Request request){
        return new ObservableTransformer<T, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(Observable<T> tObservable) {

                return tObservable.map(new Function<T, Boolean>() {
                    @Override
                    public Boolean apply(T t) {

                        LogUtils.getInstance().e("okrxcache :"+request.getKey()," DiskInterceptor opterator delete: deleteFromDisk");

                        return deleteFromDisk(request.getKey());
                    }
                });
            }
        };
    }

    private <T>ObservableTransformer<T,Boolean> clearCacheIsSuccess(final Request request){
        return new ObservableTransformer<T, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(Observable<T> tObservable) {

                return tObservable.map(new Function<T, Boolean>() {
                    @Override
                    public Boolean apply(T t) {
                        LogUtils.getInstance().e("okrxcache :"+request.getKey()," DiskInterceptor opterator clear: clearFromDisk");
                        return clearFromDisk();
                    }
                });
            }
        };
    }


    /**
     * 保存result
     * @param key
     * @param result
     */
    private boolean save2DiskCache(Key key,CacheResult result){

        InputStream in = null;
        try {
            in = new ByteArrayInputStream(mConvert.getBytes(result));
            DiskWriter diskWriter = new DiskWriter(in);
            diskCache.put(key,diskWriter);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(in != null){
                    in.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取disk
     * @param key
     * @return
     */
    private CacheResult loadFromDisk(Key key, Type type){
        File cacheFile = diskCache.get(key);
        if (cacheFile == null) {
            return null;
        }
        CacheResult<?> result = null;
        try {
            result = decodeFile2CacheResult(cacheFile,type);
            //关闭强制获取过期数据
            if(result != null&&!mCacheStagry.isOutDate()){
                LogUtils.getInstance().e(TAG,"result time :"+(result.getLifeTime()+result.getCurrentTime())+" current :"+System.currentTimeMillis());
                if(result.getLifeTime()+result.getCurrentTime() < System.currentTimeMillis()) {
                    LogUtils.getInstance().e(TAG,"result is outdate"+result.toString());
                    result = null;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(result == null){
                try {
                    diskCache.delete(key);
                } catch (IOException e) {
                    if (Log.isLoggable(TAG, Log.WARN)) {
                    Log.w(TAG, "Unable to delete from disk cache", e);
                    }
                }

            }
        }
        LogUtils.getInstance().e("okrxcache"+key,TAG+"result load back");
        return result;
    }

    /**
     * 删除disk文件中的动作
     * @param key
     * @return
     */
    private boolean deleteFromDisk(Key key){
        File cacheFile = diskCache.get(key);
        if (cacheFile == null) {
            return false;
        }
        try {
            diskCache.delete(key);
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to delete from disk cache", e);
            }
            return false;
        }

        return true;
    }


    /**
     * 清空缓存
     * @return
     */
    private boolean clearFromDisk(){
        try {
            diskCache.clear();
        }catch (IOException e){

            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 文件转化为cacheresult
     * @param cacheFile
     * @return
     */
    private CacheResult decodeFile2CacheResult(File cacheFile,Type type){
        Gson gson = new Gson();
        FileReader reader = null;
        CacheResult result=null;
        try{
//            reader = new FileReader(cacheFile);
//            JsonReader jsonReader = gson.newJsonReader(reader);
//            TypeAdapter adapter = gson.getAdapter(CacheResult.class);
//            result = (CacheResult) adapter.read(jsonReader);
            result = mConvert.setResult(cacheFile,type);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader != null){
                try {
                    reader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return result;
    }

    private class DiskWriter implements DiskCache.Writer{
        private InputStream data;
        public DiskWriter(InputStream data){
            this.data = data;
        }

        private boolean canWrite(OutputStream os, InputStream data){
            byte[] buffer = ByteArrayPool.get().getBytes();
            try {
                int read;
                while ((read = data.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                return true;
            } catch (IOException e) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Failed to encode data onto the OutputStream", e);
                }
                return false;
            } finally {
                ByteArrayPool.get().releaseBytes(buffer);
            }
        }

        @Override
        public boolean write(File file) {
            boolean success = false;
            BufferedOutputStream opener = null;
            try {
                opener = new BufferedOutputStream(new FileOutputStream(file));
                success = canWrite(opener,data);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }finally {
                if (opener != null) {
                    try {
                        opener.close();
                    } catch (IOException e) {
                        // Do nothing.
                    }
                }
            }


            return success;
        }
    }
}
