package com.android.helper.httpclient.kotlin.listener

/**
 * @author : 流星
 * @CreateDate: 2023/3/15-11:54
 * @Description: httpResult
 */
interface HttpResultCallBackListener<T> {
    fun onSuccess(t: T)
    fun onEmpty(msg: String)
    fun onError(throwable: Throwable)
}