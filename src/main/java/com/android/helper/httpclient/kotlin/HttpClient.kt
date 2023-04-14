package com.android.helper.httpclient.kotlin

import com.android.helper.utils.LogUtil
import com.android.helper.httpclient.kotlin.listener.HttpCallBackListener
import com.android.helper.httpclient.kotlin.listener.HttpResultCallBackListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * @author : 流星
 * @CreateDate: 2023/3/15-14:22
 * @Description:
 */
object HttpClient {

    @JvmStatic
    suspend fun <T> httpResult(block: suspend () -> HttpResult<T>, onStart: () -> Unit = {}, onComplete: () -> Unit = {}, listener: HttpResultCallBackListener<HttpResult<T>>) {
        flow {
            LogUtil.e("http:", "block ---> launch ...")
            emit(block())
        }.flowOn(Dispatchers.IO)
            .onStart {
                LogUtil.e("http:", "onStart")
                onStart()
            }
            .catch {
                LogUtil.e("http:", "catch:" + it.message)
                it.printStackTrace()
                listener.onError(it)
            }
            .onCompletion {
                LogUtil.e("http:", "onCompletion: --->")
                onComplete()
            }
            .collect {
                val code = it.code
                if (code == 200) {
                    listener.onSuccess(it)
                } else {
                    listener.onEmpty(it.msg)
                }
            }
    }

    @JvmStatic
    suspend fun <T> http(block: suspend () -> T, onStart: () -> Unit = {}, onComplete: () -> Unit = {}, listener: HttpCallBackListener<T>) {
        flow {
            LogUtil.e("http:", "block ---> launch ...")
            emit(block())
        }.onStart {
            LogUtil.e("http:", "onStart")
            onStart()
        }
            .catch {
                LogUtil.e("http:", "catch:" + it.message)
                it.printStackTrace()
                listener.onError(it)
            }
            .onCompletion {
                LogUtil.e("http:", "onCompletion: --->")
                onComplete()
            }
            .collect {
                listener.onSuccess(it)
            }
    }

    @JvmStatic
    suspend inline fun <reified T, P, R> http(block: T.(P) -> HttpResult<R>, p: P): HttpResult<R> {
        val apiService = RetrofitHelper.create(T::class.java)
        return apiService.block(p)
    }

}
