package com.yjy.okrxcache;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * 用于管理RxBus的事件和Rxjava相关代码的生命周期处理
 */
public class RxManager {

    private RxBus mRxBus = RxBus.getInstance();

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();// 管理订阅者者


    public RxManager(){

    }


    public void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    public <T> void addSubscription(Observable<T> observable, Subscriber<T> subscriber) {
        ConnectableObservable<T> conn = observable.compose(RxSchedule.<T>rxSchedulerHelper()).publish();

        addSubscription(conn.subscribe
                (subscriber));
    }

    public void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions())
            mCompositeSubscription.unsubscribe();// 取消订阅
    }

    public void post(Object event) {
        mRxBus.post(event);
    }

    public <T> void onEvent(Class<T> classT, Action1<T> action1) {
        addSubscription(mRxBus.toObserverable(classT).subscribe(action1));
    }
}
