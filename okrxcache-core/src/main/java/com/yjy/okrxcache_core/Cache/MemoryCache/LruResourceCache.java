package com.yjy.okrxcache_core.Cache.MemoryCache;

/**
 * Created by software1 on 2018/1/31.
 */

import android.annotation.SuppressLint;

import com.yjy.okrxcache_core.Cache.Key.Key;
import com.yjy.okrxcache_core.Cache.LruCache;
import com.yjy.okrxcache_core.CacheResult;


/**
 * An LRU in memory cache for {@link }s.
 */
public class LruResourceCache extends LruCache<Key, CacheResult<?>> implements MemoryCache {
    private ResourceRemovedListener listener;

    /**
     * Constructor for LruResourceCache.
     *
     * @param size The maximum size in bytes the in memory cache can use.
     */
    public LruResourceCache(int size) {
        super(size);
    }

    @Override
    public void setResourceRemovedListener(ResourceRemovedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onItemEvicted(Key key, CacheResult<?> item) {
        if (listener != null) {
            listener.onResourceRemoved(item);
        }
    }

    @Override
    protected int getSize(CacheResult<?> item) {
        return item.getSize();
    }

    @SuppressLint("InlinedApi")
    @Override
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            // Nearing middle of list of cached background apps
            // Evict our entire bitmap cache
            clearMemory();
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            // Entering list of cached background apps
            // Evict oldest half of our bitmap cache
            trimToSize(getCurrentSize() / 2);
        }
    }
}
