package com.yjy.rxcache_compiler;

import com.squareup.javapoet.MethodSpec;
import com.sun.tools.javac.code.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/24
 *     desc   :编译时注解的方法结构体
 *     version: 1.0
 * </pre>
 */

public class RxCacheMethod {

    private List<RxCacheAnnonation> mAnnonations = new ArrayList<>();

    private List<RxCacheParamer> mParamers = new ArrayList<>();

    private Symbol.MethodSymbol mMethod;

    public Symbol.MethodSymbol getmMethod() {
        return mMethod;
    }

    public void setmMethod(Symbol.MethodSymbol mMethod) {
        this.mMethod = mMethod;
    }

    public List<RxCacheAnnonation> getmAnnonations() {
        return mAnnonations;
    }

    public void setmAnnonations(List<RxCacheAnnonation> mAnnonations) {
        this.mAnnonations = mAnnonations;
    }

    public List<RxCacheParamer> getmParamers() {
        return mParamers;
    }

    public void setmParamers(List<RxCacheParamer> mParamers) {
        this.mParamers = mParamers;
    }
}
