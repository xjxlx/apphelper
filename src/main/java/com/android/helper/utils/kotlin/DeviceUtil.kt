package com.android.helper.utils.kotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.android.common.utils.LogUtil
import com.android.common.utils.Md5Util

/**
 * @author : 流星
 * @CreateDate: 2023/1/4-17:58
 * @Description:
 */
object DeviceUtil {
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    fun getFingerprint(): String {
        // 设备的唯一标识。由设备的多个信息拼接合成
        val fingerprint = Build.FINGERPRINT
        LogUtil.e("设备的唯一识别码为：$fingerprint")
        return fingerprint
    }

    fun getDeviceId(context: Context): String {
        val androidId = getAndroidId(context)

        LogUtil.e("设备的android——id为：$androidId")

        // 设备的唯一标识。由设备的多个信息拼接合成
        val fingerprint = Build.FINGERPRINT
        LogUtil.e("设备的唯一识别码为：$fingerprint")
        val value = androidId + fingerprint
        val md5: String = Md5Util.md5(value)
        LogUtil.e("设备的唯一识别码的Md5为：$md5")
        return md5
    }
}
