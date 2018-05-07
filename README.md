# OkRxCache
模仿Okhttp,Retrofit动态代理，RxCache写的基于Rxjava的缓存框架

### How to use it

In the project root gradle
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
In your module gradle
```
dependencies {
	        implementation 'com.github.yjy239.OkRxCache:okrxcache-core:0.90'
          implementation 'com.github.yjy239.OkRxCache:okrxcache-base:0.90'
          annotationProcessor 'com.github.yjy239.OkRxCache:okrxcache-compiler:0.90'
	}
    
```

### Api

okrxcache has 5 api to controll the network request.

#### Create

this api can cache the request automatically.According to what CacheStragy we use,okrxcache can know when return the cache or when reuqest network.it also can be controlled by Response Header.

Sorry,in rxokcache 0.90 it couldn't not controlled by network.it will be supprot in 1.0.

if we want to use this api,Firstly,we must to create a retrofit
```
@AutoCache(duaration = 5,unit = TimeUnit.SECONDS,setFromNet = true,open = true)
public interface ApiService {

    @LifeCache(duaration = 30,unit = TimeUnit.SECONDS,setFromNet = false)
    @Headers("xxxxxx)
    @GET("xxxxxx")
    Observable<HttpResult<CommonDictResponse.Result>> getCommonDict();
}
```
There are two annotations in okrxcache.@AutoCache controll cache life of the whole interface;
@LifeCache controll one network request.

the paramter of Annotation:
the life of cache = duaration * unit;

setFromNetWork mean okrxcache conrolled by network Response Header

In AutoCache,open means if all of the method will be controller by this Annotation without which has LifeCache Annotation

Firstly,
```
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ApiService restApi = retrofit.create(ApiService.class);
```

Secondly,
```
final ApiService proxy = OkRxCache.with(this)
                .setStragry(CacheStragry.ALL)
                .using(ApiService.class)
                .create(restApi);
                
```
Thirdly,
```
        proxy.getCommonDict()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Subscriber<HttpResult<CommonDictResponse.Result>>() {
                            @Override
                            public void onCompleted() {
                                
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(HttpResult<CommonDictResponse.Result> resultHttpResult) {

                            }
                        });
```


#### createOrgin

this api can cache the request automatically without cachelife. it can only cache request and get the cache whether it was outdate.

Firstly,
```
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ApiService restApi = retrofit.create(ApiService.class);
```

Secondly,
```
final ApiService proxy = OkRxCache.with(this)
                .setStragry(CacheStragry.ALL)
                .using(ApiService.class)
                .createOrgin(restApi);
                
```
Thirdly,
```
        proxy.getCommonDict()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Subscriber<HttpResult<CommonDictResponse.Result>>() {
                            @Override
                            public void onCompleted() {
                                
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(HttpResult<CommonDictResponse.Result> resultHttpResult) {

                            }
                        });
```

#### How to controll one network request

if we want to use this api,Firstly,we must to create a retrofit
```
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//
        final ApiService restApi = retrofit.create(ApiService.class);
       
```

```
restApi.getCommonDict()
                        .compose(OkRxCache.with(getApplicationContext())
                                .<HttpResult<CommonDictResponse.Result>>transformToCache("111111",111))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Subscriber<CacheResult<HttpResult<CommonDictResponse.Result>>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(CacheResult<HttpResult<CommonDictResponse.Result>> httpResultCacheResult) {
                                Log.e("request",httpResultCacheResult.toString());
                            }
                        });
```

#### controll the cache directly

##### put
```
OkRxCache.with(this).put("222",list,111)
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
```

##### get

It must be find by the key and Type

```
        TypeToken type = TypeToken.getParameterized(ArrayList.class,Integer.class);

        OkRxCache.with(this).get("222",type.getType())
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
```

##### remove

find the key of cache ,remove it
```
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
```

##### clear
```
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
```

Thanks for Gilde,Picasso,RxCache,OkHttp,Gson. All of mind comes from threse project.
This is my first time to write a library,Thanks for reading.
