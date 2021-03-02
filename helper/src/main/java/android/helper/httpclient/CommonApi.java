package android.helper.httpclient;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CommonApi {

    //上传信息
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("http://web.jollyeng.com/")
    @Multipart
    Call<String> uploadFile(@Part List<MultipartBody.Part> requestBodyMap);
}
