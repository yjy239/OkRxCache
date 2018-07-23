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

public class FTest implements ITest<A> {

    private A data;
    private int test = 1;

    @Override
    public A getData() {
        return data;
    }

    @Override
    public void setData(A data) {
        this.data = data;
    }

    @Override
    public Class<?> getClazz() {
        return FTest.class;
    }

    @Override
    public String toString() {
        return "FTest{" +
                "data=" + data +
                ", test=" + test +
                '}';
    }
}
