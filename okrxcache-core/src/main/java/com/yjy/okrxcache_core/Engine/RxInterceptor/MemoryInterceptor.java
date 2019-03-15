package com.yjy.okrxcache_core.Engine.RxInterceptor;


import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.Cache.MemoryCacheCallBack;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.Engine.CacheBack;
import com.yjy.okrxcache_core.Engine.InterceptorMode;
import com.yjy.okrxcache_core.Request.Request;
import com.yjy.okrxcache_core.Utils.LogUtils;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observable;
import io.reactivex.functions.Function;


/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/26
 *     desc   :interceptor for MemoryInterceptor
 *     version: 1.0
 * </pre>
 */

public class MemoryInterceptor<T> implements Interceptor {

    private CacheStragry mCacheStagry;
    private int mMode = 0;
    private MemoryCacheCallBack mEngine;

    private static final String TAG = "MemoryInterceptor";


    public MemoryInterceptor(MemoryCacheCallBack mMemory,CacheStragry cacheStagry){
        this.mCacheStagry = cacheStagry;
        this.mEngine = mMemory;
    }

    @Override
    public Observable intercept(Interceptor.Chain chain) {
//        Log.e("MemoryInterceptor","MemoryInterceptor");
        Request request = chain.request();

        Observable memoryObservale = loadFromMemoryCache(request);


        //判断拦截器执行模式
        if(mMode == InterceptorMode.GET){
            if(mCacheStagry == CacheStragry.ONLYDISK || mCacheStagry == CacheStragry.NOMEMORY){
                return chain.process();
            }else if(mCacheStagry == CacheStragry.NODISK || mCacheStagry == CacheStragry.ONLYMEMORY){
                return memoryObservale;
            }
            return Observable.concat(memoryObservale,chain.process());
        }else if(mMode == InterceptorMode.SAVE){
            return chain.process().compose(isSucessSaveFromDisk(request));
        }else if(mMode == InterceptorMode.REMOVE){
            return chain.process().compose(deleteResultIsSuccess(request));
        }else if(mMode == InterceptorMode.CLEAR){
            return chain.process().compose(clearCacheIsSuccess());
        }

        if(mMode == InterceptorMode.RUN){
            if(mCacheStagry == CacheStragry.ALL){
                //优先缓存显示,之后会显示网络
                return Observable.merge(memoryObservale,chain.process().compose(save2CacheResult(request)));
            }else if(mCacheStagry == CacheStragry.FIRSTCACHE){
                //优先显示缓存，找到了就不找网络
                return memoryObservale.switchIfEmpty(chain.process().compose(save2CacheResult(request)));
            }else if(mCacheStagry == CacheStragry.ONLYNETWORK){
                //只获取网络
                return chain.process().compose(save2CacheResult(request));
            }else if(mCacheStagry == CacheStragry.ONLYMEMORY){
                return memoryObservale;
            }else if (mCacheStagry == CacheStragry.NOMEMORY){
                return chain.process();
            }else if(mCacheStagry == CacheStragry.NODISK){
                return Observable.merge(memoryObservale,chain.process().compose(save2CacheResult(request)));
            }
        }


        return chain.process();
    }

    @Override
    public void setMode(int mode) {
        this.mMode = mode;
    }

    /**
     * 转化为真正的result
     * @param observable
     * @return
     */
    private Observable getRealData(Observable observable){
        return observable.map(new Function<CacheResult<T>, T>() {
            @Override
            public T apply(CacheResult<T> tCacheResult) {
                return tCacheResult.getData();
            }
        });
    }

    /**
     * 从Memory中读取
     * @param request
     * @return
     */
    private Observable loadFromMemoryCache(final Request request){
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> subscriber) throws Exception {
                CacheResult result =null;
                //判断缓存是否存在
                result = mEngine.loadFromCache(request.getKey(),true);
//                Log.e("MemoryInterceptor","Memory");

                if(result == null){
                    //获取活跃的资源是否存在
                    result = mEngine.loadFromActiveResources(request.getKey(),true);
                }

                if(mCacheStagry == CacheStragry.ONLYMEMORY){
                    CacheResult empty = new CacheResult<>(null,0,0);;
                    empty.setFromCache(CacheBack.MEMORY);
                    subscriber.onNext(result);
                    subscriber.onComplete();
                }

                if(result != null){
                    if(!mCacheStagry.isOutDate() &&result.getLifeTime()+result.getCurrentTime() < System.currentTimeMillis()) {
                        LogUtils.getInstance().e("okrxcache"+request.getKey(),"result is outdate"+result.toString());
                        result = null;
                    }

                    if(result != null){
                        request.setHadGetCache(true);
                    }

                    result.setFromCache(CacheBack.MEMORY);
                    LogUtils.getInstance().e("okrxcache"+request.getKey(),"load succcess");
                    subscriber.onNext(result);
                }

                subscriber.onComplete();
            }

        });
    }

    /**
     * 判断上个拦截器是否保存成功
     * @param request
     * @return
     */
    private ObservableTransformer<Boolean,Boolean> isSucessSaveFromDisk(final Request request){
        return new ObservableTransformer<Boolean, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(Observable<Boolean> observable) {

                return observable.map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) {
                        if(!aBoolean){
                            LogUtils.getInstance().e("okrxcache"+request.getKey(),"Disk save failed");
                        }
                        return mEngine.complete(request.getKey(),request.getResult());
                    }
                });
            }
        };
    }


    /**
     * 网络拦截器返回之后，转化为cacheresult，并且保存
     * @param request
     * @param <T>
     * @return
     */
    private <T>ObservableTransformer<T,CacheResult<T>> save2CacheResult(final Request request){
        return new ObservableTransformer<T, CacheResult<T>>() {
            @Override
            public ObservableSource<CacheResult<T>> apply(Observable<T> tObservable) {

                return tObservable.map(new Function<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(T t) {
                        LogUtils.getInstance().e("okrxcache"+request.getKey(),"save2CacheResult");
                        if(t ==null){
                            throw new IllegalArgumentException(" network error");
                        }else {
                            mEngine.complete(request.getKey(),(CacheResult)t);
                            request.setResult((CacheResult)t);
                        }


                        return (CacheResult)t;
                    }
                });
            }
        };
    }

    /**
     * 删除成功的转化器
     * @param <T>
     * @return
     */
    private <T>ObservableTransformer<T,Boolean> clearCacheIsSuccess(){
        return new ObservableTransformer<T, Boolean>() {
            @Override
            public Observable<Boolean> apply(Observable<T> tObservable) {

                return tObservable.map(new Function<T, Boolean>() {
                    @Override
                    public Boolean apply(T t) {
                        LogUtils.getInstance().e("MemoryInterceptor","clearCacheIsSuccess");
                        return mEngine.clear();
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
            public Observable<Boolean> apply(Observable<T> tObservable) {

                return tObservable.map(new Function<T, Boolean>() {
                    @Override
                    public Boolean apply(T t) {
                        LogUtils.getInstance().e("okrxcache"+request.getKey(),"deleteResultIsSuccess");
                        return mEngine.remove(request.getKey());
                    }
                });
            }
        };
    }
}
