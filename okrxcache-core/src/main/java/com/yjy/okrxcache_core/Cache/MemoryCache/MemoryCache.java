package com.yjy.okrxcache_core.Cache.MemoryCache;


import com.yjy.okrxcache_core.Cache.Key.Key;
import com.yjy.okrxcache_core.CacheResult;

/**
 * Created by software1 on 2018/1/30.
 */

public interface MemoryCache {
    /**
     * An interface that will be called whenever a bitmap is removed from the cache.
     */
    interface ResourceRemovedListener {
        void onResourceRemoved(CacheResult<?> removed);
    }

    /**
     * Returns the sum of the sizes of all the contents of the cache in bytes.
     */
    int getCurrentSize();

    /**
     * Returns the current maximum size in bytes of the cache.
     */
    int getMaxSize();

    /**
     * Adjust the maximum size of the cache by multiplying the original size of the cache by the given multiplier.
     *
     * <p>
     *     If the size multiplier causes the size of the cache to be decreased, items will be evicted until the cache
     *     is smaller than the new size.
     * </p>
     *
     * @param multiplier A size multiplier >= 0.
     */
    void setSizeMultiplier(float multiplier);

    /**
     * Removes the value for the given key and returns it if present or null otherwise.
     *
     * @param key The key.
     */
    CacheResult<?> remove(Key key);

    /**
     * Add cache to the cache with the given key.
     *
     * @param key The key to retrieve the bitmap.
     * @param resource The {@link } to store.
     * @return The old value of key (null if key is not in map).
     */
    CacheResult<?> put(Key key, CacheResult<?> resource);

    /**
     * Set the listener to be called when a cache is removed from the cache.
     *
     * @param listener The listener.
     */
    void setResourceRemovedListener(ResourceRemovedListener listener);

    /**
     * Evict all items from the memory cache.
     */
    void clearMemory();

    /**
     * Trim the memory cache to the appropriate level. Typically called on the callback onTrimMemory.
     *
     * @param level This integer represents a trim level as specified in {@link android.content.ComponentCallbacks2}.
     */
    void trimMemory(int level);
}
