package com.yjy.okrxcache_core;

import android.graphics.Bitmap;

import com.yjy.okrxcache_core.Utils.MemorySizeOf;
import com.yjy.okrxcache_core.Utils.Occupy;
import com.yjy.okrxcache_core.Utils.Utils;

import java.io.Serializable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   : the result which retrofit get back from network
 *     version: 1.0
 * </pre>
 */

public class CacheResult<T> implements Serializable{
    private final T mData;
//    private final CacheStrategy mSource;
    private long mCurrentTime;
    private long mLifeTime;
    private int mDataSize = 0;
    private Occupy occupy = new Occupy((byte) 0, (byte)0, (byte)4);;

    public CacheResult(T data,long currentTime,long lifeTime) {
        this.mData = data;

        this.mCurrentTime = currentTime;
        this.mLifeTime = lifeTime;
//        mDataSize = Utils.getDataSize(mData);

    }

    public int getSize(){
        return countSize(this);
    }

    public T getData() {
        return mData;
    }

    private int countSize(Object value) {
        if (value == null) {
            return 0;
        }

        //  更优良的内存大小算法
        int size;
        if (value instanceof Bitmap) {
            size = MemorySizeOf.sizeOf((Bitmap) value);
        } else {
            size = occupy.occupyof(value);
        }
        return size;
    }


//    public CacheStrategy getSource() {
//        return mSource;
//    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public long getLifeTime() {
        return mLifeTime;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "data=" + mData +
                ", lifeTime=" + mLifeTime +
                ", currentTime"+mCurrentTime+
                '}';
    }

}
