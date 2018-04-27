package com.yjy.okrxcache_core.rx.core.Engine.RxInterceptor;

import com.yjy.okrxcache_core.rx.core.CacheResult;
import com.yjy.okrxcache_core.rx.core.Engine.Request;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class RealInterceptorChain<T> implements Interceptor.Chain {

    private List<Interceptor> mInterceptors = new ArrayList<>();
    private Request mRequest;
    private int mIndex;
    private Observable mObservale;
    private int mMode = 0;

    public RealInterceptorChain(List<Interceptor> interceptors,int index, Request request,int mode){
        this.mInterceptors = interceptors;
        this.mRequest = request;
        this.mIndex = index;
        this.mMode = mode;
    }


    @Override
    public Request request() {
        return mRequest;
    }

    /**
     * 拦截器启动运行
     * @return
     */
    @Override
    public Observable process() {
        if(mRequest.isInterceptor()||mInterceptors.size()<=mIndex){
            return mRequest.getObservable();
        }
        RealInterceptorChain next = new RealInterceptorChain(mInterceptors,mIndex+1,mRequest,mMode);
        Interceptor interceptor = mInterceptors.get(mIndex);
        interceptor.setMode(mMode);
        try {
            mObservale = interceptor.intercept(next);
        }catch (Exception e){
            mObservale = Observable.empty();
            e.printStackTrace();
        }

        return mObservale;
    }
}
