package com.android.helper.httpclient.error

import com.android.common.utils.LogUtil
import com.android.common.utils.SpUtil
import com.android.helper.httpclient.BaseException
import com.android.helper.httpclient.BaseHttpDisposableObserver
import com.android.helper.httpclient.RetrofitHelper
import com.android.helper.httpclient.RxUtil
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
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

    fun uploadAppErrorLog(file: File) {
        if (!file.exists()) {
            return
        }
        if (file.length() <= 0) {
            return
        }

        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val fileBody: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val serviceBody = RequestBody.create("text/plain;charset=UTF-8".toMediaTypeOrNull(), "App.App2021.UploadLog")
        val nameBody = RequestBody.create("text/plain;charset=UTF-8".toMediaTypeOrNull(), SpUtil.getString(""))

        ApiServices
            .uploadAppErrorLog(fileBody, serviceBody, nameBody)
            .subscribe(object : BaseHttpDisposableObserver<String>() {
                override fun onSuccess(t: String?) {
                    t?.let {
                        LogUtil.e("错误日志上传成功： $it")
                        val jsonObject = JSONObject(it)
                        val hasRet = jsonObject.has("ret")
                        if (hasRet) {
                            val ret = jsonObject.getInt("ret")
                            if (ret == 200) {
                                LogUtil.e("错误日志上传成功：成功： $it")
                                val delete = file.delete()
                                LogUtil.e("错误日志：删除成功：$delete")
                            } else {
                                val hasMsg = jsonObject.has("msg")
                                if (hasMsg) {
                                    val msgValue = jsonObject.getString("msg")
                                    LogUtil.e("错误日志上传成功:失败：$msgValue")
                                }
                            }
                        }
                    }
                }

                override fun onFailure(e: BaseException?) {
                    LogUtil.e("错误日志上传失败：： " + e?.message)
                }
            })
    }
}

object ApiServices {
    fun uploadAppErrorLog(file: MultipartBody.Part, service: RequestBody, name: RequestBody): Observable<String> {
        return RetrofitHelper
            .create(AppInfoApi::class.java)
            .uploadAppErrorLog(file, service, name)
            .compose(RxUtil.getSchedulerObservable())
    }
}

interface AppInfoApi {
    @Multipart
    @POST("/")
    // 图文混排上传
    fun uploadAppErrorLog(@Part file: MultipartBody.Part, @Part("service") service: RequestBody, @Part("name") name: RequestBody): Observable<String>
}