package com.yjy.okrxcache.test;




import com.yjy.okexcache_base.AutoCache;
import com.yjy.okexcache_base.LifeCache;
import com.yjy.okrxcache.CommonDictResponse;
import com.yjy.okrxcache.HttpResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@AutoCache(duaration = 5,unit = TimeUnit.SECONDS,setFromNet = true,open = true)
public interface ApiService {

    @LifeCache(duaration = 30,unit = TimeUnit.SECONDS,setFromNet = false)
    @GET("api/zhuzher/projects/buildings/{building_code}/houses")
    Observable<Integer> getHouseListByBuilding(@Path("building_code") String projectCode);

    String URL_BASE = "https://api.github.com";
    String HEADER_API_VERSION = "Accept: application/vnd.github.v3+json";


    @Headers({HEADER_API_VERSION,"aaaaaa"})
    @GET("/users")
    Observable<Response<ResponseBody>> getUser(@Query("since") int lastIdQueried, @Query("per_page") int perPage);

    @Headers("Authorization-Control: no")
    @GET("https://flyingdutchman.4009515151.com/api/zhuzher/info")
    Observable<HttpResult<CommonDictResponse.Result>> getCommonDict();
}
