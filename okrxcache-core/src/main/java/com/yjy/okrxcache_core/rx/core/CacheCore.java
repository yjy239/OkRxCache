package com.yjy.okrxcache_core.rx.core;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Cache.Key.Key;
import com.yjy.okrxcache_core.rx.core.Cache.Key.RequestKey;
import com.yjy.okrxcache_core.rx.core.Cache.MemoryCacheCallBack;
import com.yjy.okrxcache_core.rx.core.Convert.IConvert;
import com.yjy.okrxcache_core.rx.core.Engine.CacheEngine;

import com.yjy.okrxcache_core.rx.core.Request.Request;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.Observable;

import com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor.Interceptor;
/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :the core of the RxCache
 *     version: 1.0
 * </pre>
 */

public class CacheCore implements MemoryCacheCallBack{

    private ArrayList<Interceptor> mInterceptors = new ArrayList<>();
    private CacheEngine mEngine;
    private ReferenceQueue<CacheResult<?>> resourceReferenceQueue;
    private Handler MAIN_HANDLER = new Handler(Looper.getMainLooper(),new MainThreadCallback());
    private HashMap<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>> sendObj = new HashMap<>();
    private final int MSG_PUT = 0;
    private static final String TAG = "CacheCore";
    private final Map<Key, WeakReference<CacheResult<?>>> activeCaches;
    private volatile int working = 0;


    public CacheCore(ArrayList<Interceptor> mInterceptors,DiskCache.Factory diskFactory
            ,IConvert convert,CacheStragry cacheStagry,boolean isForce){
        this.mInterceptors = mInterceptors;
        cacheStagry.setOutdata(isForce);
        mEngine = new CacheEngine(this,mInterceptors,diskFactory.build(),convert,cacheStagry);
        activeCaches = new HashMap<>();
    }



    public <T>Observable start(Observable observable,final CacheMethod method){
        return run(observable,method);
    }


    public <T>Observable run(Observable observable, final CacheMethod method){

        RequestKey key = new RequestKey(method.getKey());

        Request request = Request.obtain(key,null,false,observable,method);

        return mEngine.run(request);

    }

    public Observable operator(Observable observable, String Key,int mode){

        RequestKey key = new RequestKey(Key);

        Request request = Request.obtain(key,null,false,observable,null);

        return mEngine.operator(request, mode);
    }


    /**
     * 从内存缓存中读取
     * @param key
     * @param isMemoryCacheable
     * @return
     */
    @Override
    public CacheResult<?> loadFromCache(Key key, boolean isMemoryCacheable) {
        return null;
    }

    /**
     * 从活跃的缓存中读取
     * @param key
     * @param isMemoryCacheable
     * @return
     */
    @Override
    public CacheResult<?> loadFromActiveResources(Key key, boolean isMemoryCacheable) {
        return null;
    }

    @Override
    public void complete(Key key, CacheResult resource) {

    }

    @Override
    public void cancelled(Key key) {

    }



    private ReferenceQueue<CacheResult<?>> getReferenceQueue() {
        if (resourceReferenceQueue == null) {
            resourceReferenceQueue = new ReferenceQueue<CacheResult<?>>();
            sendObj.put(activeCaches,resourceReferenceQueue);
            MAIN_HANDLER.obtainMessage(MSG_PUT,sendObj).sendToTarget();
        }
        return resourceReferenceQueue;
    }



    /**
     * 继承weakrefence,为的是能够存储key
     */
    private static class CacheWeakReference extends WeakReference<CacheResult<?>> {
        private final Key key;

        public CacheWeakReference(Key key, CacheResult<?> r, ReferenceQueue<? super CacheResult<?>> q) {
            super(r, q);
            this.key = key;
        }
    }

    // Responsible for cleaning up the active resource map by remove weak references that have been cleared.
    private static class RefQueueIdleHandler implements MessageQueue.IdleHandler {
        private final Map<Key, WeakReference<CacheResult<?>>> activeResources;
        private final ReferenceQueue<CacheResult<?>> queue;

        public RefQueueIdleHandler(Map<Key, WeakReference<CacheResult<?>>> activeResources,
                                   ReferenceQueue<CacheResult<?>> queue) {
            this.activeResources = activeResources;
            this.queue = queue;
        }

        @Override
        public boolean queueIdle() {
            CacheWeakReference ref = (CacheWeakReference) queue.poll();
            if (ref != null) {
                activeResources.remove(ref.key);
            }

            return true;
        }
    }

    public class MainThreadCallback implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_PUT:
                    if(msg.obj instanceof  HashMap){
                        HashMap<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>> map = (HashMap) msg.obj;
                        Iterator<Map.Entry<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>>> entry = map.entrySet().iterator();
                        while (entry.hasNext()){
                            Map.Entry<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>> item = entry.next();
                            MessageQueue queue = Looper.myQueue();
                            queue.addIdleHandler(new RefQueueIdleHandler(item.getKey(), item.getValue()));
                            Log.e(TAG,"PUT");
                        }
                    }

                    break;
            }
            return false;
        }
    }

}
