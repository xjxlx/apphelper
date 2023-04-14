package com.android.helper.httpclient.kotlin.listener

/**
 * @author : 流星
 * @CreateDate: 2023/3/15-17:31
 * @Description:
 */
interface HttpCallBackListener<T> {
    fun onSuccess(t: T)
    fun onError(throwable: Throwable)
}