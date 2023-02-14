package com.android.helper.httpclient

import com.android.helper.utils.LogUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * @author : 流星
 * @CreateDate: 2023/2/14-12:29
 * @Description:
 */
class AppPushErrorService {

    private fun uploadAppErrorLog(file: File) {
        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)

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