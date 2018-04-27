package com.yjy.okrxcache;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yjy.okrxcache.test.ApiService;
import com.yjy.okrxcache_core.rx.core.Cache.CacheStragry;
import com.yjy.okrxcache_core.rx.core.CacheResult;
import com.yjy.okrxcache_core.rx.core.OkRxCache;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkRxCache cache = new OkRxCache.Builder()
                .with(this)
                .setStragry(CacheStragry.ONLYNETWORK)
                .using(ApiService.class)
                .build();

        cache.put("1111",new CacheResult<>(1,System.currentTimeMillis(),11))
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.e("aaa",aBoolean.toString());
                    }
                });

        cache.get("1111")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<CacheResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CacheResult cacheResult) {
                        Log.e("result",cacheResult.getData().toString());
                    }
                });



//        ApiService api = new ApiService() {
//            @Override
//            public Observable<Integer> getHouseListByBuilding(String projectCode) {
//                return null;
//            }
//        };
//        ProcessHandler handler = new ProcessHandler(api);
//        ProcessHandler2 handler2 = new ProcessHandler2(api);
//        ApiService a = (ApiService) Proxy.newProxyInstance(ApiService.class.getClassLoader(),new Class<?>[]{ApiService.class},handler);
//        ApiService a2 = (ApiService) Proxy.newProxyInstance(a.getClass().getClassLoader(),new Class<?>[]{ApiService.class},handler2);
//        a2.getHouseListByBuilding("111");

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(333);
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e("num",""+integer);
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService restApi = retrofit.create(ApiService.class);

        ApiService proxy = cache.create(restApi);

//        proxy.getUser(1,1)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Subscriber<Response<ResponseBody>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("error",e.toString());
//                    }
//
//                    @Override
//                    public void onNext(Response<ResponseBody> users) {
//                        Log.e("num","header : "+users.headers());
//                    }
//                });

        Observable o1 = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                Log.e("first","next");

                subscriber.onNext(1);
            }
        });
        Observable o2 = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                Log.e("second","next");

                subscriber.onNext(2);
            }
        });

        Observable.merge(o1,o2)
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e("test",o.toString());
                    }
                });




        proxy.getUsers(2,1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        Log.e("User",users.toString());
                    }
                });

        proxy.getUsers(2,1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        Log.e("User",users.toString());
                    }
                });

//        proxy.getHouseListByBuilding("12")
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Subscriber<Integer>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("error",e.toString());
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//
//                    }
//                });


//       cache.excute(proxy.getUsers(1,1))
//               .subscribeOn(Schedulers.io())
//               .observeOn(Schedulers.io())
//               .subscribe(new Subscriber<ArrayList>() {
//           @Override
//           public void onCompleted() {
//
//           }
//
//           @Override
//           public void onError(Throwable e) {
//               Log.e("error",e.toString());
//           }
//
//           @Override
//           public void onNext(ArrayList o) {
//               Log.e("num",""+o.toString());
//           }
//       });


    }

    public Observable<String> valueObservable() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override public Observable<String> call() {
                return Observable.just("11");
            }
        });
    }

}
