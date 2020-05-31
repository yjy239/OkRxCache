package com.yjy.okrxcache;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yjy.okrxcache.test.A;
import com.yjy.okrxcache.test.ApiService;
import com.yjy.okrxcache.test.B;
import com.yjy.okrxcache.test.FTest;
import com.yjy.okrxcache.test.ITest;
import com.yjy.okrxcache.test.InterfaceGsonConvert;
import com.yjy.okrxcache.test.STest;
import com.yjy.okrxcache.test.TestTypeAdapter;
import com.yjy.okrxcache_core.Cache.CacheStragry;
import com.yjy.okrxcache_core.CacheResult;
import com.yjy.okrxcache_core.OkRxCache;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



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
        Button get = findViewById(R.id.get);
        Button put = findViewById(R.id.put);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.URL_BASE)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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

        final FutureTest f = new FutureTest();
        final OkRxCache cache = OkRxCache.init(this);


        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ITest.class, new TestTypeAdapter())
                .create();

        A a = new A(1,2);
        FTest t = new FTest();
        t.setData(a);
        String s = gson.toJson(t);

        Log.e("FTest",s);

        ITest test =  gson.fromJson(s,FTest.class);

        Log.e("itest",test.toString());


        put.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ITest> tests = new ArrayList<>();
                FTest t1 = new FTest();
                t1.setData(new A(1,2));
                STest t2 = new STest();
                t2.setData(new B(2));
                tests.add(t1);
                tests.add(t2);

                OkRxCache.with()
                        .setStragry(CacheStragry.ONLYDISK)
                        .isDebug(true).setConvert(new InterfaceGsonConvert()).put("333",tests,0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Observer<Boolean>() {


                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                Log.e("put",aBoolean.toString());
                                f.setReady(true);
                            }
                        });

            }
        });



        final TypeToken type = TypeToken.getParameterized(ArrayList.class,ITest.class);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkRxCache.with()
                        .isDebug(true)
                        .setConvert(new InterfaceGsonConvert())
                        .setStragry(CacheStragry.ONLYDISK)
                        .get("333",type.getType())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Observer<CacheResult>() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("get",e.toString());
                            }

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(CacheResult cacheResult) {
                                if(cacheResult.getData() == null){
                                    Log.e("get","empty");
                                    return;
                                }
                                ArrayList<ITest> tests = (ArrayList<ITest>)cacheResult.getData();
                                for(int i=0;i<tests.size();i++){

                                    Log.e("get",tests.get(i).getData().getClass().getName()+"");
                                }

                            }
                        });


            }
        });



        Observable.fromCallable(f)
                .flatMap(new Function<Boolean, ObservableSource<CacheResult>>() {
                    @Override
                    public Observable<CacheResult> apply(Boolean aBoolean) {
                        return OkRxCache.with(MainActivity.this)
                                .get("333",type.getType());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<CacheResult>() {


                    @Override
                    public void onError(Throwable e) {
                        Log.e("get",e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CacheResult cacheResult) {
                        ArrayList<ITest> tests = (ArrayList<ITest>)cacheResult.getData();
                        for(int i=0;i<tests.size();i++){

                            Log.e("get",tests.get(i).getData()+"");
                        }

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
                        .subscribe(new Observer<CacheResult<HttpResult<CommonDictResponse.Result>>>() {

                            @Override
                            public void onError(Throwable e) {
                                Log.e("throw",e.toString());
                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onSubscribe(Disposable d) {

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
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onSubscribe(Disposable d) {

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
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onComplete() {

                            }


                            @Override
                            public void onSubscribe(Disposable d) {

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


//        final Observer subscriber = new Observer<HttpResult<CommonDictResponse.Result>>() {
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(HttpResult<CommonDictResponse.Result> resultHttpResult) {
//                Log.e("result",resultHttpResult.data.accountUrl);
//            }
//        };



        final ReUseDisposeObvserver reUseSubscriber = new ReUseDisposeObvserver<HttpResult<CommonDictResponse.Result>>(){

            @Override
            public void onNext(HttpResult<CommonDictResponse.Result> resultHttpResult) {
                Log.e("result",resultHttpResult.data.accountUrl);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };






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
