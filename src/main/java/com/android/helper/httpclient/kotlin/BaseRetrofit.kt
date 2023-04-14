package com.android.helper.httpclient.kotlin

import android.annotation.SuppressLint
import android.text.TextUtils
import com.android.helper.utils.LogUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * @author : 流星
 * @CreateDate: 2023/3/14-18:18
 * @Description:
 */
class BaseRetrofit {

    var connectTime = 30L
    private var mBaseUrl: String = ""
    private val mListInterceptor = arrayListOf<Interceptor>()
    val retrofit: Retrofit by lazy {
        val builder = OkHttpClient.Builder()
            .apply {
                // set connect time
                readTimeout(connectTime, TimeUnit.SECONDS) // setting the read timeout period
                writeTimeout(connectTime, TimeUnit.SECONDS) // set the waite timeout period
                connectTimeout(connectTime, TimeUnit.SECONDS) // set the connect timeout period

                // add  interceptor
                if (mListInterceptor.size > 0) {
                    mListInterceptor.forEach {
                        addInterceptor(it)
                    }
                }
            }

        // set  ssl certificates
        setSSLFactory(builder)

        Retrofit.Builder()
            .apply {
                client(builder.build())
                if (TextUtils.isEmpty(mBaseUrl)) {
                    LogUtil.e("Retrofit Base url is null !")
                }
                baseUrl(mBaseUrl)
                addConverterFactory(GsonConverterFactory.create())
                addConverterFactory(ScalarsConverterFactory.create())
            }
            .build()
    }

    /**
     * add  connect interceptor
     */
    fun addInterceptor(interceptor: Interceptor) {
        mListInterceptor.add(interceptor)
    }

    /**
     * set base url path
     */
    fun setBaseUrl(baseUrl: String) {
        this.mBaseUrl = baseUrl
    }

    /**
     * create a service
     */
    inline fun <reified T> create(@NotNull service: T): T {
        return retrofit.create(service!!::class.java)
    }

    @SuppressLint("TrustAllX509TrustManager,BadHostnameVerifier,CustomX509TrustManager")
    private fun setSSLFactory(builder: OkHttpClient.Builder?) {
        builder?.let {
            var mSslSocketFactory: SSLSocketFactory? = null

            val mX509TrustManager: X509TrustManager = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            }
            val mHostnameVerifier = HostnameVerifier { _: String?, _: SSLSession? -> true }

            try {
                val sslContext = SSLContext.getInstance("SSL")
                if (sslContext != null) {
                    sslContext.init(null, arrayOf<TrustManager?>(mX509TrustManager), SecureRandom())
                    mSslSocketFactory = sslContext.socketFactory
                }

                // trust all https certificates
                if (mSslSocketFactory != null) {
                    builder.sslSocketFactory(mSslSocketFactory, mX509TrustManager)
                }

                // trust all host
                builder.hostnameVerifier(mHostnameVerifier)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
