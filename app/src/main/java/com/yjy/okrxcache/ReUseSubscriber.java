package com.yjy.okrxcache;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/05/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */

//public class ReUseSubscriber<T>  extends SafeSubscriber<T> {
//    private final Subscriber<? super T> actual;
//
//    boolean done;
//
//    public ReUseSubscriber(Subscriber<? super T> actual) {
//        super(actual);
//        this.actual = actual;
//    }
//
//    /**
//     * Notifies the Subscriber that the {@code Observable} has finished sending push-based notifications.
//     * <p>
//     * The {@code Observable} will not call this method if it calls {@link #onError}.
//     */
//    @Override
//    public void onCompleted() {
////        if (!done) {
////            done = true;
//            try {
//                actual.onCompleted();
//            } catch (Throwable e) {
//                // we handle here instead of another method so we don't add stacks to the frame
//                // which can prevent it from being able to handle StackOverflow
//                Exceptions.throwIfFatal(e);
//                RxJavaHooks.onError(e);
//                throw new OnCompletedFailedException(e.getMessage(), e);
//            } finally { // NOPMD
//                try {
//                    // Similarly to onError if failure occurs in unsubscribe then Rx contract is broken
//                    // and we throw an UnsubscribeFailureException.
////                    unsubscribe();
//                } catch (Throwable e) {
//                    RxJavaHooks.onError(e);
//                    throw new UnsubscribeFailedException(e.getMessage(), e);
//                }
//            }
////        }
//    }
//
//    /**
//     * Notifies the Subscriber that the {@code Observable} has experienced an error condition.
//     * <p>
//     * If the {@code Observable} calls this method, it will not thereafter call {@link #onNext} or
//     * {@link #onCompleted}.
//     *
//     * @param e
//     *          the exception encountered by the Observable
//     */
//    @Override
//    public void onError(Throwable e) {
//        // we handle here instead of another method so we don't add stacks to the frame
//        // which can prevent it from being able to handle StackOverflow
//        Exceptions.throwIfFatal(e);
//        if (!done) {
//            done = true;
//            _onError(e);
//        }
//    }
//
//
//
//    /**
//     * Provides the Subscriber with a new item to observe.
//     * <p>
//     * The {@code Observable} may call this method 0 or more times.
//     * <p>
//     * The {@code Observable} will not call this method again after it calls either {@link #onCompleted} or
//     * {@link #onError}.
//     *
//     * @param t
//     *          the item emitted by the Observable
//     */
//    @Override
//    public void onNext(T t) {
//        try {
////            if (!done) {
//                actual.onNext(t);
////            }
//        } catch (Throwable e) {
//            // we handle here instead of another method so we don't add stacks to the frame
//            // which can prevent it from being able to handle StackOverflow
//            Exceptions.throwOrReport(e, this);
//        }
//    }
//
//
//
//
//
//    /**
//     * The logic for {@code onError} without the {@code isFinished} check so it can be called from within
//     * {@code onCompleted}.
//     *
//     * @see <a href="https://github.com/ReactiveX/RxJava/issues/630">the report of this bug</a>
//     */
//    @SuppressWarnings("deprecation")
//    protected void _onError(Throwable e) { // NOPMD
//        RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
//        try {
//            actual.onError(e);
//        } catch (OnErrorNotImplementedException e2) { // NOPMD
//            /*
//             * onError isn't implemented so throw
//             *
//             * https://github.com/ReactiveX/RxJava/issues/198
//             *
//             * Rx Design Guidelines 5.2
//             *
//             * "when calling the Subscribe method that only has an onNext argument, the OnError behavior
//             * will be to rethrow the exception on the thread that the message comes out from the observable
//             * sequence. The OnCompleted behavior in this case is to do nothing."
//             */
//            try {
////                unsubscribe();
//            } catch (Throwable unsubscribeException) {
//                RxJavaHooks.onError(unsubscribeException);
//                throw new OnErrorNotImplementedException("Observer.onError not implemented and error while unsubscribing.", new CompositeException(Arrays.asList(e, unsubscribeException))); // NOPMD
//            }
//            throw e2;
//        } catch (Throwable e2) {
//            /*
//             * throw since the Rx contract is broken if onError failed
//             *
//             * https://github.com/ReactiveX/RxJava/issues/198
//             */
//            RxJavaHooks.onError(e2);
//            try {
////                unsubscribe();
//            } catch (Throwable unsubscribeException) {
//                RxJavaHooks.onError(unsubscribeException);
//                throw new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError and during unsubscription.", new CompositeException(Arrays.asList(e, e2, unsubscribeException)));
//            }
//
//            throw new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError", new CompositeException(Arrays.asList(e, e2)));
//        }
//        // if we did not throw above we will unsubscribe here, if onError failed then unsubscribe happens in the catch
//        try {
////            unsubscribe();
//        } catch (Throwable unsubscribeException) {
//            RxJavaHooks.onError(unsubscribeException);
//            throw new OnErrorFailedException(unsubscribeException);
//        }
//    }
//
//    /**
//     * Returns the {@link Subscriber} underlying this {@code SafeSubscriber}.
//     *
//     * @return the {@link Subscriber} that was used to create this {@code SafeSubscriber}
//     */
//    public Subscriber<? super T> getActual() {
//        return actual;
//    }
//}
