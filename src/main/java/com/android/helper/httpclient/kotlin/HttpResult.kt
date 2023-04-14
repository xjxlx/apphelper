package com.android.helper.httpclient.kotlin

data class HttpResult<T>(
    var msg: String = "",
    var code: Int = 0,
    var `data`: T? = null
)
