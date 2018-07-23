package com.yjy.okrxcache.test;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/07/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface ITest<T> {

    T getData();

    void setData(T data);

    Class<?> getClazz();


}
