package com.yjy.okrxcache;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;


/**
 * 用于管理RxBus的事件和Rxjava相关代码的生命周期处理
 */
public class RxManager {
//
//    private RxBus mRxBus = RxBus.getInstance();

    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();// 管理订阅者者


    public RxManager(){

    }


    public void addSubscription(Disposable subscription) {
        mCompositeSubscription.add(subscription);
    }

    public <T> void addSubscription(Observable<T> observable, DisposableObserver<T> subscriber) {
        ConnectableObservable<T> conn = observable.compose(RxSchedule.<T>rxSchedulerHelper()).publish();

        addSubscription(conn.subscribeWith
                (subscriber));
    }

    public void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.isDisposed()){
            mCompositeSubscription.dispose();// 取消订阅
        }

    }

//    public void post(Object event) {
//        mRxBus.post(event);
//    }

}
