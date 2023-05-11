package com.android.helper.httpclient.kotlin

import com.android.helper.httpclient.kotlin.listener.HttpCallBackListener
import com.android.helper.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * @author : 流星
 * @CreateDate: 2023/3/15-14:22
 * @Description:
 */
object HttpClient {

    @JvmStatic
    suspend inline fun <reified T, Result> http(crossinline block: suspend T.() -> Result) = callbackFlow {
        try {
            val bean = RetrofitHelper.create(T::class.java)
                .block()
            // send request data
            trySend(bean)
        } catch (exception: Throwable) {
            exception.printStackTrace()
            close(exception)
        }
        // close callback
        awaitClose()
    }.flowOn(Dispatchers.IO)

    @JvmStatic
    suspend inline fun <reified T, Parameter, Result> http(crossinline block: suspend T.(Parameter) -> Result, p: Parameter) =
        callbackFlow {
            try {
                // LogUtil.e("http thread callbackFlow :" + Thread.currentThread().name)
                val bean = RetrofitHelper.create(T::class.java)
                    .block(p)
                // send request data
                trySend(bean)
            } catch (exception: Throwable) {
                exception.printStackTrace()
                close(exception)
            }
            // close callback
            awaitClose()
        }.flowOn(Dispatchers.IO)

    @JvmStatic
    suspend inline fun <reified T, Parameter, Result> http(crossinline block: suspend T.(Parameter) -> Result, p: Parameter, callback: HttpCallBackListener<Result>) {
        http<T, Parameter, Result>({ block(it) }, p).onStart {
            LogUtil.e("http thread started :" + Thread.currentThread().name)
            callback.onStart()
        }
            .catch {
                it.printStackTrace()
                callback.onFailure(it)
            }
            .onCompletion {
                callback.onCompletion()
            }
            .collect {
                callback.onSuccess(it)
            }
    }

    @JvmStatic
    suspend inline fun <reified T, Result> http(crossinline block: suspend T.() -> Result, callback: HttpCallBackListener<Result>) {
        http<T, Result> { block() }.onStart {
            LogUtil.e("http thread started :" + Thread.currentThread().name)
            callback.onStart()
        }
            .catch {
                it.printStackTrace()
                callback.onFailure(it)
            }
            .onCompletion {
                callback.onCompletion()
            }
            .collect {
                callback.onSuccess(it)
            }
    }
}