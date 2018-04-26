package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;


import android.util.Log;

import com.yjy.okrxcache_core.rx.core.Cache.ByteArrayPool;
import com.yjy.okrxcache_core.rx.core.Cache.DisCache.DiskCache;
import com.yjy.okrxcache_core.rx.core.Engine.Request;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;

/**
 * Created by yjy on 2018/4/23.
 */

public class DiskInterceptor implements Interceptor {

    private final String TAG = "DiskCache";

    @Override
    public Observable intercept(Interceptor.Chain chain) {
        Log.e("DiskInterceptor","DiskInterceptor");
        chain.request();


        return chain.process();
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
