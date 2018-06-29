package com.yjy.okrxcache;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;
import com.yjy.okrxcache.test.ApiService;
import com.yjy.okrxcache.test.ReUseConnectableObservable;
import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.OkRxCache;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;
import rx.plugins.RxJavaHooks;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button)findViewById(R.id.request);
        Button remove = (Button)findViewById(R.id.remove);
        Button clear = (Button)findViewById(R.id.clear);
        Button web = (Button)findViewById(R.id.web);
        Button retry = (Button)findViewById(R.id.retry);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//
        final ApiService restApi = retrofit.create(ApiService.class);

        final ApiService proxy = OkRxCache.with(this)
                .setStragry(CacheStragry.ALL)
                .using(ApiService.class)
                .create(restApi);

        final ApiService orgin = OkRxCache.with(this)
                .setStragry(CacheStragry.ALL)
                .using(ApiService.class)
                .createOrgin(restApi);

        ArrayList<Integer> list = new ArrayList();
        list.add(0);
        list.add(1);

        OkRxCache cache = OkRxCache.init(this);

        cache.with()
                .isDebug(true).put("222",list,111)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e("put",aBoolean.toString());
                    }
                });

        TypeToken type = TypeToken.getParameterized(ArrayList.class,Integer.class);


        OkRxCache.with(this)
                .get("222",type.getType())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<CacheResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("get",e.toString());
                    }

                    @Override
                    public void onNext(CacheResult cacheResult) {
                        Log.e("get",cacheResult.getData()+"");
                    }
                });

//        final Observable.Transformer transformer = OkRxCache.with(this)
//                .transformToCache("111111",111);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypeToken token = TypeToken.getParameterized(HttpResult.class,CommonDictResponse.Result.class);

                restApi.getCommonDict()
                        .compose(OkRxCache.with(getApplicationContext())
                                .setStragry(CacheStragry.FIRSTCACHE)
                                .isDebug(true)
                                .<HttpResult<CommonDictResponse.Result>>transformToCache("111111",111,token.getType()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Subscriber<CacheResult<HttpResult<CommonDictResponse.Result>>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("throw",e.toString());
                            }

                            @Override
                            public void onNext(CacheResult<HttpResult<CommonDictResponse.Result>> httpResultCacheResult) {
                                Log.e("request",httpResultCacheResult.getData().toString()+" "+httpResultCacheResult.getFromCache());
                            }
                        });
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkRxCache.with(MainActivity.this).remove("222")
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                Log.e("remove",aBoolean.toString());
                            }
                        });
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkRxCache.with(MainActivity.this).clear()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                Log.e("clear",aBoolean.toString());
                            }
                        });
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,WebActivity.class);
                startActivity(i);
            }
        });

        //test for retry

        final Observable<HttpResult<CommonDictResponse.Result>> net = restApi.getCommonDict().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());


        final Subscriber subscriber = new Subscriber<HttpResult<CommonDictResponse.Result>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(HttpResult<CommonDictResponse.Result> resultHttpResult) {
                Log.e("result",resultHttpResult.data.accountUrl);
            }
        };

        final ReUseSubscriber reUseSubscriber = new ReUseSubscriber(subscriber);







        final RxManager manager = new RxManager();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

            }
        }).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });



        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                net.subscribe(reUseSubscriber);

//                manager.addSubscription(restApi.getCommonDict(), new Subscriber<HttpResult<CommonDictResponse.Result>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(HttpResult<CommonDictResponse.Result> resultHttpResult) {
//                        Log.e("manager",resultHttpResult.data.accountUrl);
//                    }
//                });

            }
        });



//        restApi.getCommonDict().subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Observer<HttpResult<CommonDictResponse.Result>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(HttpResult<CommonDictResponse.Result> resultHttpResult) {
//                        Log.e("result",resultHttpResult.data.accountUrl);
//                    }
//                });







    }



}
