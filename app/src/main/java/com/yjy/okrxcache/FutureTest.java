package com.yjy.okrxcache;

import java.util.concurrent.Callable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class FutureTest implements Callable<Boolean> {

    private boolean ready = false;

    @Override
    public synchronized Boolean call() throws Exception {
        if(!ready){
            wait();
        }
        return ready;
    }

    public synchronized void setReady(boolean ready){
        this.ready = ready;
        notifyAll();
    }
}
