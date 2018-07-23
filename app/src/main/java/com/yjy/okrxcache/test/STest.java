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

public class STest implements ITest<B> {


    private B data;

    @Override
    public B getData() {
        return data;
    }

    @Override
    public void setData(B data) {
        this.data = data;
    }

    @Override
    public Class<?> getClazz() {
        return STest.class;
    }
}
