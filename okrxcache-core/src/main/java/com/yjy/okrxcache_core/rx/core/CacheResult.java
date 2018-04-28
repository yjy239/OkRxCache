package com.yjy.okrxcache_core.rx.core;

import com.yjy.okrxcache_core.rx.core.Utils.Utils;

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

    public CacheResult(T data,long currentTime,long lifeTime) {
        this.mData = data;

        this.mCurrentTime = currentTime;
        this.mLifeTime = lifeTime;
//        mDataSize = Utils.getDataSize(mData);

    }

    public int getSize(){
        if(mData == null){
            return 0;
        }
        return mDataSize;
    }

    public T getData() {
        return mData;
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
                ", mLifeTime=" + mLifeTime +
                '}';
    }

}
