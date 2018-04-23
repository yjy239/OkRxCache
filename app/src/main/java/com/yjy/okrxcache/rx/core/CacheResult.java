package com.yjy.okrxcache.rx.core;

import rx.functions.Function;

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
    private final boolean isEncrypted;

    public CacheResult(T data, CacheStrategy source, boolean isEncrypted) {
        this.data = data;
        this.source = source;
        this.isEncrypted = isEncrypted;
    }

    public T getData() {
        return data;
    }

    public CacheStrategy getSource() {
        return source;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "data=" + data +
                ", source=" + source +
                ", isEncrypted=" + isEncrypted +
                '}';
    }
}
