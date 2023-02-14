package com.android.helper.httpclient.error

import com.android.helper.httpclient.BaseException
import com.android.helper.httpclient.BaseHttpDisposableObserver
import com.android.helper.httpclient.RetrofitHelper
import com.android.helper.httpclient.RxUtil
import com.android.helper.utils.LogUtil
import com.android.helper.utils.SpUtil
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

/**
 * @author : 流星
 * @CreateDate: 2023/2/14-12:29
 * @Description:
 */
class AppPushErrorService {
    
    private fun uploadAppErrorLog(file: File) {
        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        val multipartBody = MultipartBody
            .Builder()
            .addFormDataPart("file", file.name, requestFile)
            .addFormDataPart("service", "App.App2021.UploadLog")
            .addFormDataPart("name", SpUtil.getString("wx_unionid"))
            .setType(MultipartBody.FORM)
            .build()

        val body: MultipartBody.Part = MultipartBody.Part.createFormData("avatarFile", file.name, multipartBody)

        ApiServices
            .uploadAppErrorLog(body)
            .subscribe(object : BaseHttpDisposableObserver<String>() {
                override fun onStart() {
                    super.onStart()
                }

                override fun onSuccess(t: String?) {
                    LogUtil.e("错误日志上传成功： $t")

                }

                override fun onFailure(e: BaseException?) {
                    LogUtil.e("错误日志上传失败：： " + e?.message)
                }
            })
    }

}

object ApiServices {
    fun uploadAppErrorLog(file: MultipartBody.Part): Observable<String> {
        return RetrofitHelper
            .create(AppInfoApi::class.java)
            .uploadAppErrorLog(file)
            .compose(RxUtil.getSchedulerObservable())
    }
}

interface AppInfoApi {

    /**
     * todo 上传错误日志,路径错误，需要修正
     */
    @POST("")
    @Multipart
    fun uploadAppErrorLog(@Part parts: MultipartBody.Part): Observable<String>

}