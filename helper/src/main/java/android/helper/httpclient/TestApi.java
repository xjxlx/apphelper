package android.helper.httpclient;

import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface TestApi {
    
    //我的信息
    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("v1/login")
    Flowable<Response<String>> myInfo(@Query("mobile") String mobile,
                                     @Query("captcha") String captcha,
                                     @Query("inviteCode") String inviteCode,
                                     @Query("anonymousId") String anonymousId);
}
