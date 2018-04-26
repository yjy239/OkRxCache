package com.yjy.okrxcache_core.rx.core;

import com.yjy.okrxcache_core.rx.core.Cache.CacheStrategy;

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
    private final T data;
    private final CacheStrategy source;

    public CacheResult(T data, CacheStrategy source) {
        this.data = data;
        this.source = source;

    }

    public T getData() {
        return data;
    }

    public CacheStrategy getSource() {
        return source;
    }


    @Override
    public String toString() {
        return "Reply{" +
                "data=" + data +
                ", source=" + source +
                '}';
    }
}
