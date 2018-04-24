package com.yjy.rxcache_compiler;

import com.squareup.javapoet.AnnotationSpec;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/24
 *     desc   :编译时注解的注解结构体
 *     version: 1.0
 * </pre>
 */

public class RxCacheAnnonation {

    private AnnotationSpec mName;

    private Object mValue;

    public AnnotationSpec getSpec() {
        return mName;
    }

    public void setSpec(AnnotationSpec mName) {
        this.mName = mName;
    }

    public Object getmValue() {
        return mValue;
    }

    public void setmValue(Object mValue) {
        this.mValue = mValue;
    }


}
