package com.yjy.rxcache_compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/24
 *     desc   :编译时注解的注解参数结构体
 *     version: 1.0
 * </pre>
 */

public class RxCacheParamer {
    //参数注解
    private List<RxCacheAnnonation> mAnnonations = new ArrayList<>();

    //参数名字
    private String mParamerName;

    public List<RxCacheAnnonation> getmAnnonations() {
        return mAnnonations;
    }

    public void setmAnnonations(List<RxCacheAnnonation> mAnnonations) {
        this.mAnnonations = mAnnonations;
    }

    public String getmParamerName() {
        return mParamerName;
    }

    public void setmParamerName(String mParamerName) {
        this.mParamerName = mParamerName;
    }



}
