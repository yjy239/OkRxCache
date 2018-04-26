package com.yjy.okrxcache_core.rx.core.Cache.Key;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Created by software1 on 2018/2/1.
 */

public class RequestKey implements Key {
    private final String signature;

    public RequestKey(String signature) {
        if (signature == null) {
            throw new NullPointerException("Signature cannot be null!");
        }
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequestKey that = (RequestKey) o;

        return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        messageDigest.update(signature.getBytes(STRING_CHARSET_NAME));
    }

    @Override
    public String toString() {
        return "StringSignature{"
                + "signature='" + signature + '\''
                + '}';
    }
}

