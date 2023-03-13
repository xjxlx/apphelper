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
    @JvmOverloads
    fun e(value: Any? = "", tag: String = "") {
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
    @JvmOverloads
    fun d(value: Any?, tag: String = "") {
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
    @JvmOverloads
    fun i(value: Any?, tag: String = "") {
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

    @JvmStatic
    @JvmOverloads
    fun w(value: Any?, tag: String = "") {
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

    /**
     * write file content
     */
    @JvmStatic
    @JvmOverloads
    fun write(value: String?, fileName: String?) {
        if ((!TextUtils.isEmpty(value)) && (!TextUtils.isEmpty(fileName))) {
            mWriteUtil.write(fileName!!, value)
        }
    }

    /**
     * write  file content and write log
     */
    @JvmStatic
    @JvmOverloads
    fun writeAll(fileName: String? = "", value: String? = "", tag: String? = "") {
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
class sss{
    fun sss(){
        LogUtil.writeAll(fileName = "")
    }
}
