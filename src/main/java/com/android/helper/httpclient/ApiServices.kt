package com.android.helper.httpclient

import io.reactivex.Observable
import okhttp3.MultipartBody

/**
 * @author : 流星
 * @CreateDate: 2022/11/22-19:49
 * @Description:
 */
object ApiServices {

    fun uploadAppErrorLog(file: MultipartBody.Part): Observable<String> {
        return RetrofitHelper
            .create(AppInfoApi::class.java)
            .uploadAppErrorLog(file)
            .compose(RxUtil.getSchedulerObservable())
    }

}
