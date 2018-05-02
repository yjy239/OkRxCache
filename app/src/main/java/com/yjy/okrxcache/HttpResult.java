package com.yjy.okrxcache;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Class Note:
 * wrapper for return result，include {@link #code}， {@link #message}
 * and {@link #data}
 * <p>
 * 对请求结果返回对象的封装，包含{@link #code}， {@link #message}
 * 和{@link #data}
 */
public class HttpResult<T> {

    private static final int CODE_SUCCESS = 0;

    @SerializedName(value = "code")
    int code;
    @SerializedName(value = "message", alternate = {"msg", "error"})
    String message;
    @SerializedName(value = "data", alternate = {"result"})
    T data;
    @SerializedName(value = "errorCode")
    Integer errorCode;
    @SerializedName(value = "extra")
    JsonObject extra;

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }


    public int getCode() {
        //二手市场errorCode
        if (errorCode != null && errorCode != 0)
            return errorCode;
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public JsonObject getExtra() {
        return extra;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("code=").append(code).append(" message=").append(message);
        if (data != null) {
            sb.append("data=").append(data.toString());
        }
        return sb.toString();
    }
}
