package com.android.helper.httpclient

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author : 流星
 * @CreateDate: 2023/2/14-12:26
 * @Description:
 */
interface AppInfoApi {

    /**
     * todo 上传错误日志,路径错误，需要修正
     */
    @POST("test-api/system/user/avatar/update")
    @Multipart
    fun uploadAppErrorLog(@Part file: MultipartBody.Part): Observable<String>

}