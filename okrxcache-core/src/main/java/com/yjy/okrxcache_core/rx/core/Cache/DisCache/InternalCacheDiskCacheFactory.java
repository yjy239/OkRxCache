package com.yjy.okrxcache_core.rx.core.Cache.DisCache;

/**
 * Created by software1 on 2018/1/31.
 */

import android.content.Context;

import java.io.File;

/**
 * Creates an {@link DiskLruCache} based disk cache in the internal disk cache
 * directory.
 */
public final class InternalCacheDiskCacheFactory extends DiskLruCacheFactory {

//    public InternalCacheDiskCacheFactory(Context context) {
//        this(DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
//    }
//
//    public InternalCacheDiskCacheFactory(Context context, int diskCacheSize) {
//        this( DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, diskCacheSize);
//    }

    public InternalCacheDiskCacheFactory(Context context,String fileDir,int diskSize){
        this(context,fileDir,DiskCache.Factory.DEFAULT_DISK_CACHE_DIR,diskSize == 0? DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE:diskSize);
    }



    public InternalCacheDiskCacheFactory(final Context context,final String fileDir, final String diskCacheName, int diskCacheSize) {


        super(new CacheDirectoryGetter() {
            @Override
            public File getCacheDirectory() {
                File cacheDirectory = context.getCacheDir();

                if(fileDir != null){
                    cacheDirectory = new File(fileDir);
                }
                if (cacheDirectory == null) {
                    return null;
                }
                if (diskCacheName != null) {
                    return new File(cacheDirectory, diskCacheName);
                }

                return cacheDirectory;
            }
        }, diskCacheSize);
    }
}
