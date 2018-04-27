package com.yjy.okrxcache_core.rx.core;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   : the result which retrofit get back from network
 *     version: 1.0
 * </pre>
 */

public class CacheResult<T> {
    private final T mData;
//    private final CacheStrategy mSource;
    private long mCurrentTime;
    private long mLifeTime;

    public CacheResult(T data,long currentTime,long lifeTime) {
        this.mData = data;

        this.mCurrentTime = currentTime;
        this.mLifeTime = lifeTime;

    }

    public T getData() {
        return mData;
    }

//    public CacheStrategy getSource() {
//        return mSource;
//    }

    public long getmCurrentTime() {
        return mCurrentTime;
    }

    public long getmLifeTime() {
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
