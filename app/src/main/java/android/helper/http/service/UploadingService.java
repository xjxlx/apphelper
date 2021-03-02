package android.helper.http.service;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UploadingService {
    
    //我的信息
    @GET("v1/login")
    Flowable<Response<String>> myInfo(@Query("mobile") String mobile,
                                      @Query("captcha") String captcha,
                                      @Query("inviteCode") String inviteCode,
                                      @Query("anonymousId") String anonymousId);
    
    @Multipart
    @POST("http://web.jollyeng.com/")
    Flowable<String> uploadFiles(@Part MultipartBody multipartBody);
    
}
