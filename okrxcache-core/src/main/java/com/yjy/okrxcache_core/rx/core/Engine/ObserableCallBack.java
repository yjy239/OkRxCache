package com.yjy.okrxcache_core.rx.core.Engine;

import rx.Observable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface ObserableCallBack {
    Observable getFromNet(Observable observable);

    Observable getFromDiskCache(Observable observable);

    Observable getFromMemoryCache(Observable observable);
}
