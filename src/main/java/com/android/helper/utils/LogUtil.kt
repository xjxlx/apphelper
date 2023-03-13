package com.android.helper.utils

import android.text.TextUtils
import com.android.helper.app.BaseApplication
import com.orhanobut.logger.Logger

/**
 * @author XJX 日志工具类
 */
object LogUtil {

    private val mWriteUtil: LogWriteUtil = LogWriteUtil()

    @JvmStatic
    fun e(tag: String = "", value: Any?) {
        if (isDebug) {
            value?.let {
                val contentValue = if (value is String) {
                    value
                } else {
                    value.toString()
                }

                if (!TextUtils.isEmpty(contentValue)) {
                    if (TextUtils.isEmpty(tag)) {
                        Logger.e(contentValue)
                    } else {
                        Logger
                            .t(tag)
                            .e(contentValue)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun e(value: Any?) {
        e(value = value, tag = "")
    }

    @JvmStatic
    fun d(tag: String = "", value: Any?) {
        if (isDebug) {
            value?.let {
                val contentValue = if (value is String) {
                    value
                } else {
                    value.toString()
                }

                if (!TextUtils.isEmpty(contentValue)) {
                    if (TextUtils.isEmpty(tag)) {
                        Logger.d(contentValue)
                    } else {
                        Logger
                            .t(tag)
                            .d(contentValue)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun d(value: Any?) {
        d(value = value, tag = "")
    }

    @JvmStatic
    fun i(tag: String = "", value: Any?) {
        if (isDebug) {
            value?.let {
                val contentValue = if (value is String) {
                    value
                } else {
                    value.toString()
                }

                if (!TextUtils.isEmpty(contentValue)) {
                    if (TextUtils.isEmpty(tag)) {
                        Logger.i(contentValue)
                    } else {
                        Logger
                            .t(tag)
                            .i(contentValue)
                    }
                }
            }
        }
    }

    fun i(value: Any?) {
        i(value = value, tag = "")
    }

    @JvmStatic
    fun w(tag: String = "", value: Any?) {
        if (isDebug) {
            value?.let {
                val contentValue = if (value is String) {
                    value
                } else {
                    value.toString()
                }

                if (!TextUtils.isEmpty(contentValue)) {
                    if (TextUtils.isEmpty(tag)) {
                        Logger.w(contentValue)
                    } else {
                        Logger
                            .t(tag)
                            .w(contentValue)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun w(value: Any?) {
        w(value = value, tag = "")
    }

    /**
     * write file content
     */
    @JvmStatic
    @JvmOverloads
    fun write(fileName: String? = "", value: String? = "") {
        if ((!TextUtils.isEmpty(value)) && (!TextUtils.isEmpty(fileName))) {
            mWriteUtil.write(fileName!!, value)
        }
    }

    /**
     * write  file content and write log
     */
    @JvmStatic
    fun writeAll(fileName: String? = "", tag: String? = "", value: String? = "") {
        if ((!TextUtils.isEmpty(value)) && (!TextUtils.isEmpty(fileName))) {
            value?.let {
                if (TextUtils.isEmpty(tag)) {
                    Logger.e(it)
                } else {
                    Logger
                        .t(tag)
                        .e(it)
                }
                mWriteUtil.write(fileName!!, it)
            }
        }
    }

    @JvmStatic
    fun writeAll(fileName: String? = "", value: String? = "") {
        writeAll(fileName = fileName, value = value, tag = "")
    }

    /**
     * @return if application type is debug,return true,else return false
     */
    private val isDebug: Boolean
        get() {
            val application = BaseApplication.getInstance()
            application?.let {
                return it.isDebug
            }
            return false
        }

}

