package com.yjy.okrxcache;


import org.reactivestreams.Subscriber;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


/**
 */

public class RxSchedule {

    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io())
                        //.unsubscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> defaultSchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> tObservable) {
                return tObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn
                        (Schedulers.io());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> all_io() {
        return new ObservableTransformer<T, T>() {
            @Override
            public Observable<T> apply(Observable<T> tObservable) {
                return tObservable.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            }
        };
    }





    /**
     * 生成Observable
     *
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createData(final T t) {
        return Observable.create(new ObservableOnSubscribe<T>() {

            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }

        });
    }

}
