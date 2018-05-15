package com.yjy.okrxcache_core.Engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.Cache.DiskCache.DiskCache;
import com.yjy.okrxcache_core.Cache.Key.Key;
import com.yjy.okrxcache_core.Cache.MemoryCache.MemoryCache;
import com.yjy.okrxcache_core.Cache.MemoryCacheCallBack;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.Convert.IConvert;
import com.yjy.okrxcache_core.Engine.RequestHandler.OkHttpNetWorkHandler;
import com.yjy.okrxcache_core.Engine.RequestHandler.RequestHandler;
import com.yjy.okrxcache_core.Engine.RxInterceptor.DiskInterceptor;
import com.yjy.okrxcache_core.Engine.RxInterceptor.Interceptor;
import com.yjy.okrxcache_core.Engine.RxInterceptor.MemoryInterceptor;
import com.yjy.okrxcache_core.Engine.RxInterceptor.NetWorkInterceptor;
import com.yjy.okrxcache_core.Engine.RxInterceptor.RealInterceptorChain;
import com.yjy.okrxcache_core.Request.Request;
import com.yjy.okrxcache_core.Utils.LogUtils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import rx.Observable;
import rx.functions.Func1;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/25
 *     desc   :the engine of Cache
 *     version: 1.0
 *     目的是为了缓存的map挂载到CacheCore
 * </pre>
 */

public class CacheEngine<T> implements MemoryCacheCallBack{

    private ReferenceQueue<CacheResult<?>> resourceReferenceQueue;
    private HashMap<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>> sendObj = new HashMap<>();
    private final int MSG_PUT = 0;
    private static final String TAG = "CacheCore";
    private final Map<Key, WeakReference<CacheResult<?>>> activeCaches;
    private volatile int working = 0;
    private List<Interceptor> mInterceptors = new ArrayList<>();
    private DiskCache mDiskCache;
    private CacheStragry mCacheStragry;
    private MemoryCache mMemoryCache;
    private IConvert mConvert;
    private Handler MAIN_HANDLER = new Handler(Looper.getMainLooper(),new MainThreadCallback());


    public CacheEngine(DiskCache diskCache,MemoryCache cache
            ,IConvert convert,CacheStragry cacheStagry){
        this.mDiskCache = diskCache;
        this.mConvert = convert;
        this.mCacheStragry = cacheStagry;
        this.mMemoryCache = cache;
        activeCaches = new HashMap<>();

    }


    /**
     * 启动前的准备
     * @param request
     */
    public void runInit(Request request,RequestHandler handler){
        mInterceptors.clear();
        mInterceptors.addAll(request.getInterceptors());
        mInterceptors.add(new MemoryInterceptor(this,request.getCacheStagry()));

        CacheStragry stragry = request.getCacheStagry() == null?mCacheStragry:request.getCacheStagry();
        stragry.setOutdata(request.isForce());

        mInterceptors.add(new DiskInterceptor(mDiskCache,
                request.getConvert() == null? mConvert:request.getConvert(),
                stragry));

        mInterceptors.add(new NetWorkInterceptor(handler));

        request.setResult(new CacheResult(null,0,0));
    }


    /**
     * 从内存缓存中读取
     * @param key
     * @param isMemoryCacheable
     * @return
     */
    @Override
    public synchronized CacheResult<?> loadFromCache(Key key, boolean isMemoryCacheable) {
        try {
            while (working>0){
                wait();
            }
        }catch (InterruptedException e){
        }
        working++;

        if (!isMemoryCacheable) {
            working--;
            return null;
        }

        CacheResult<?> cached = getEngineResourceFromCache(key);

        if (cached != null) {

            //放置活跃资源的时候切一次线程,把资源挂在在主线程，
            // 也需要在主线程空闲的时候，把不需要的资源释放掉，而且，放到主线程，Looper将不会持有该线程对象
            //好回收
            activeCaches.put(key, new CacheWeakReference(key, cached, getReferenceQueue()));
        }
        working--;
        notifyAll();
        return cached;
    }

    private CacheResult<?> getEngineResourceFromCache(Key key) {
        CacheResult<?> cached = mMemoryCache.remove(key);

        return cached;
    }

    /**
     * 从活跃的缓存中读取
     * @param key
     * @param isMemoryCacheable
     * @return
     */
    @Override
    public synchronized CacheResult<?> loadFromActiveResources(Key key, boolean isMemoryCacheable) {
        try {
            while (working>0){
                wait();
            }
        }catch (InterruptedException e){
        }
        working++;
        if (!isMemoryCacheable) {
            working--;
            return null;
        }

        CacheResult<?> active = null;
        WeakReference<CacheResult<?>> activeRef = activeCaches.get(key);
        if (activeRef != null) {
            active = activeRef.get();
            if (active == null) {
                activeCaches.remove(key);
            }
        }
        working--;
        notifyAll();
        return active;
    }

    /**
     * 加载数据完成挂载到活跃内存上
     * @param key
     * @param resource
     */
    @Override
    public synchronized boolean complete(Key key, CacheResult resource) {
        try {
            while (working>0){
                wait();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        working++;
        try {
            if (resource != null) {
                activeCaches.put(key, new CacheWeakReference(key, resource, getReferenceQueue()));
            }else {
                working--;
                notifyAll();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            working--;
            notifyAll();
            return false;
        }

        working--;
        notifyAll();
        return true;

    }

    @Override
    public synchronized boolean remove(Key key) {
        try {
            while (working>0){
                wait();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        working++;

        try {
            activeCaches.remove(key);

            mMemoryCache.remove(key);
        }catch (Exception e){
            e.printStackTrace();
            working--;
            notifyAll();
            return false;
        }


        working--;
        notifyAll();

        return true;
    }

    @Override
    public synchronized boolean clear() {

        try {
            while (working>0){
                wait();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        working++;

        try {
            activeCaches.clear();

            mMemoryCache.clearMemory();
        }catch (Exception e){
            working--;
            notifyAll();
            e.printStackTrace();
            return false;
        }


        working--;
        notifyAll();

        return true;

    }



    /**
     * 开始解析下一个请求说明，当前请求已经不是活跃状态移除到普通的缓存中
     * @param resource
     */
    public void release(Request resource) {
//        LogUtils.getInstance().e("activeCaches",activeCaches+"");
        if(resource != null&&resource.getKey()!=null&&resource.getResult()!=null){
            activeCaches.remove(resource.getKey());
            mMemoryCache.put(resource.getKey(), resource.getResult());
        }

    }

    /**
     * 拦截器普通模式运行
     * @param request
     * @param <T>
     * @return
     */
    public <T>Observable run(final Request request, RequestHandler handler,boolean isRealData){
        runInit(request,handler);

        RealInterceptorChain chain = new RealInterceptorChain(mInterceptors,0,request,InterceptorMode.RUN);
        if(isRealData){
            return getRealData(chain.process());
        }else {
            return chain.process();
        }

    }

    /**
     * 操作缓存的时候,拦截器以其他方式运行
     * @param request
     * @param mode
     * @param <T>
     * @return
     */
    public <T>Observable operator(Request request,int mode){
        runInit(request,null);
        RealInterceptorChain chain = new RealInterceptorChain(mInterceptors,0,request,mode);
        return chain.process();
    }

    public Observable request(Request request, OkHttpClient client){
        OkHttpNetWorkHandler handler = new OkHttpNetWorkHandler(client);
        runInit(request,handler);
        RealInterceptorChain chain = new RealInterceptorChain(mInterceptors,0,request,InterceptorMode.RUN);
        return chain.process();
    }

    /**
     * 转化为真正的result
     * @param observable
     * @return
     */
    private Observable getRealData(Observable observable){
        return observable.map(new Func1<CacheResult<T>, T>() {
            @Override
            public T call(CacheResult<T> tCacheResult) {
                return tCacheResult.getData();
            }
        });
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
                    if(msg.obj instanceof HashMap){
                        HashMap<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>> map = (HashMap) msg.obj;
                        Iterator<Map.Entry<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>>> entry
                                = map.entrySet().iterator();
                        while (entry.hasNext()){
                            Map.Entry<Map<Key, WeakReference<CacheResult<?>>>,ReferenceQueue<CacheResult<?>>> item = entry.next();
                            MessageQueue queue = Looper.myQueue();
                            queue.addIdleHandler(new RefQueueIdleHandler(item.getKey(), item.getValue()));
//                            Log.e(TAG,"PUT");
                        }
                    }

                    break;
            }
            return false;
        }
    }



}
