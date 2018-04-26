package com.yjy.okrxcache_core.rx.core.Cache.Key;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Created by software1 on 2018/2/1.
 */

public class RequestKey implements Key {
    private static final String EMPTY_LOG_STRING = "";
    private final String id;
    private final Key signature;
    private String stringKey;
    private int hashCode;
    private Key originalKey;

    public RequestKey(String id, Key signature, int width, int height) {
        this.id = id;
        this.signature = signature;
    }

    public Key getOriginalKey() {
        if (originalKey == null) {
            originalKey = new OriginalKey(id, signature);
        }
        return originalKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequestKey requestKey = (RequestKey) o;

        if (!id.equals(requestKey.id)) {
            return false;
        } else if (!signature.equals(requestKey.signature)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = id.hashCode();
            hashCode = 31 * hashCode + signature.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (stringKey == null) {
            stringKey = new StringBuilder()
                    .append("RequestKey{")
                    .append(id)
                    .append('+')
                    .append(signature)
                    .append("+[")
                    .append("]+")
                    .append('\'')
                    .append('}')
                    .toString();
        }
        return stringKey;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        byte[] dimensions = ByteBuffer.allocate(8)
                .array();
        signature.updateDiskCacheKey(messageDigest);
        messageDigest.update(id.getBytes(STRING_CHARSET_NAME));
        messageDigest.update(dimensions);
    }
}

