package com.android.helper.httpclient.kotlin

object RetrofitHelper {

    private val baseRetrofit: BaseRetrofit by lazy {
        BaseRetrofit()
    }

    fun setConnectTime(time: Long) {
        baseRetrofit.connectTime = time
    }

    /**
     * create a service
     */
    fun <T> create(service: Class<T>): T {
        return baseRetrofit.retrofit.create(service)
    }
}