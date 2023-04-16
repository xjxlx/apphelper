package com.android.helper.httpclient.kotlin

import okhttp3.Interceptor

object RetrofitHelper {

    private val baseRetrofit: BaseRetrofit by lazy {
        BaseRetrofit()
    }

    @JvmStatic
    fun setConnectTime(time: Long) {
        baseRetrofit.connectTime = time
    }

    /**
     * add  connect interceptor
     */
    @JvmStatic
    fun addInterceptor(interceptor: Interceptor) {
        baseRetrofit.addInterceptor(interceptor)
    }

    /**
     * set base url path
     */
    @JvmStatic
    fun setBaseUrl(baseUrl: String) {
        baseRetrofit.setBaseUrl(baseUrl)
    }

    /**
     * create a service
     */
    @JvmStatic
    fun <T> create(service: Class<T>): T {
        return baseRetrofit.retrofit.create(service)
    }
}