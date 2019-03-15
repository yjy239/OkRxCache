package com.yjy.okrxcache;



/**
 * 用RxJava实现的EventBus
 */
//public class RxBus {
//
//    private final Subject<Object, Object> bus;
//
//    private RxBus() {
//        bus = new SerializedSubject<>(PublishSubject.create());
//    }
//
//
//
//    // Make this class a thread safe singleton
//    private static class SingletonHolder {
//        private static final RxBus INSTANCE = new RxBus();
//    }
//
//    public static synchronized RxBus getInstance() {
//        return SingletonHolder.INSTANCE;
//    }
//
//
//    /**
//     * 发送消息
//     *
//     * @param object
//     */
//    public void post(Object object) {
//
//        bus.onNext(object);
//
//    }
//
//    /**
//     * 接收消息
//     *
//     * @param eventType
//     * @param <T>
//     * @return
//     */
//    public <T> Observable<T> toObserverable(Class<T> eventType) {
//        return bus.ofType(eventType);
//    }
//}
