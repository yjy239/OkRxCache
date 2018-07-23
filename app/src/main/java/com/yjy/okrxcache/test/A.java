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

public class A {

    int data;
    int data2;

    public A(int data,int data2) {
        this.data = data;
        this.data2 = data2;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "A{" +
                "data=" + data +
                ", data2=" + data2 +
                '}';
    }
}
