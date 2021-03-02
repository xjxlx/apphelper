package android.helper.http.service;

import android.helper.bean.HomeBean;

import java.util.HashMap;

import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

public interface CommonApiService {

    //我的信息
    @Headers("module_type:1")
    @GET("yd/lotteryActivity/getLotteryActivityList")
    Flowable<Response<HomeBean>> getHomeData(@QueryMap HashMap<String, Object> map);

}
