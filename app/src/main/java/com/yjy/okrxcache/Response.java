package com.yjy.okrxcache;

/**
 * Created by liugl01 on 2015/7/28.
 */
public class Response {
    public int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                '}';
    }
}
