package com.android.helper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import java.io.File
import java.net.NetworkInterface
import java.util.*

/**
 * @author : 流星
 * @CreateDate: 2023/2/18-23:16
 * @Description:
 */
class DeviceUtil private constructor() {

    @JvmField
    val fileName = "deviceId.txt"
    val TAG = "DeviceUtil"

    companion object {

        @Volatile
        private var INSTANCE: DeviceUtil? = null

        @JvmStatic
        val instance: DeviceUtil?
            get() {
                if (INSTANCE == null) {
                    synchronized(DeviceUtil::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = DeviceUtil()
                        }
                    }
                }
                return INSTANCE
            }
    }

    /**
     * 必须添加权限
     * android 11 以下，使用权限
     *      <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE"/>
     * android 11 以上，额外另加一个权限
     *      <uses-permission android:name="android.permission.READ_PHONE_NUMBERS" />
     */
    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    private fun getDeviceId(context: Context?): String {
        var deviceId: String = ""
        context?.let {
            val systemService = it.getSystemService(Context.TELEPHONY_SERVICE)
            if (systemService is TelephonyManager) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android 10 以上，只能通过系统权限获取

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //1.1:如果有权限，直接显示
                    // 8.0 以下可以用deviceId，8.0以上要使用 imeiId
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 只适用于手机，依赖于sim卡
                        deviceId = systemService.imei // 设备的SN 序列号
                        if (TextUtils.isEmpty(deviceId)) {
                            deviceId = Build.getSerial()
                        }
                    } else {
                        deviceId = systemService.deviceId
                    }
                }

                // 设备的SN 序列号
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = Build.SERIAL
                }
            }
        }
        return deviceId
    }

    /**
     * 获取mac地址
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     * @return 通过反射的方式去获取Mac网卡的地址值
     */
    private fun getMacAddress(context: Context?): String {
        var macAddress = ""
        context?.let {
            val systemService = it.getSystemService(Context.WIFI_SERVICE)
            if (systemService is WifiManager) {
                try {
                    val buf = StringBuilder()
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        val info = systemService.connectionInfo
                        info?.let { info ->
                            macAddress = info.macAddress
                        }
                        return macAddress
                    } else {
                        val interfaces = NetworkInterface.getNetworkInterfaces()
                        while (interfaces.hasMoreElements()) {
                            val netInterface: NetworkInterface = interfaces.nextElement()
                            if ("wlan0" == netInterface.name || "eth0" == netInterface.name) {
                                val address = netInterface.hardwareAddress
                                if (address == null || address.isEmpty()) {
                                    return macAddress
                                }
                                for (b in address) {
                                    buf.append(String.format("%02X:", b))
                                }
                                if (buf.isNotEmpty()) {
                                    buf.deleteCharAt(buf.length - 1)
                                }
                                return buf.toString().lowercase(Locale.getDefault())
                            }
                        }

                    }
                } catch (ex: java.lang.Exception) {
                    LogUtil.e("getMacAddress:" + ex.message)
                }
            }
        }
        return macAddress
    }

    private fun getDeviceId2(uniqueId: String) { // 设备的唯一标识。由设备的多个信息拼接合成
        val fingerprint = Build.FINGERPRINT // 系统品牌
        val BRAND = Build.BRAND // 型号
        val MODEL = Build.MODEL // 系统制造商
        val MANUFACTURER = Build.MANUFACTURER // 设备参数
        val DEVICE = Build.DEVICE // 手机制造商
        val PRODUCT = Build.PRODUCT // sdk 版本
        val SDK = Build.VERSION.SDK // 系统 版本
        val RELEASE = Build.VERSION.RELEASE

        LogUtil.e("fingerprint:$fingerprint\r\n 系统品牌:$BRAND \r\n 型号: $MODEL \n 系统制造商:$MANUFACTURER \n 设备参数:$DEVICE \n 手机制造商:$PRODUCT\n sdk 版本:$SDK\n 系统 版本:$RELEASE")

        val deviceId = uniqueId + "_" + Build.BRAND + "_" + Build.MODEL + "_" + Build.MANUFACTURER + "_" + Build.DEVICE + "_" + Build.PRODUCT
        LogUtil.e("deviceId --------->$deviceId")
    }

    fun getSdPath(): String {
        return FileUtil.getInstance().getSdTypePublicPath(Environment.DIRECTORY_DOCUMENTS)
    }

    fun getFilePath(context: Context?): String {
        context?.let {
            val filesDir = it.filesDir
            if (filesDir != null) {
                val path = filesDir.path
                if (!TextUtils.isEmpty(path)) {
                    return path
                }
            }
        }
        return ""
    }

    /**
     * 获取 deviceId
     */
    fun getDeviceIdForFile(path: String): String {
        var content = ""
        val file = File(path + File.separator, fileName)
        if (file.exists()) {
            content = FileUtil
                .getInstance()
                .getContentForFile(file)
        }
        return content
    }

    /**
     * 写入deviceId 到文件中
     */
    fun writeDeviceId(context: Context?, androidId: String) {
        context?.let {
            if (!TextUtils.isEmpty(androidId)) {
                val fileUtil = FileUtil.getInstance()
                // 1:使用标准的文档地址
                val documents = fileUtil.getSdTypePublicPath(Environment.DIRECTORY_DOCUMENTS)
                writeContentToFile(documents, androidId)

                // 2: 使用内部沙盒文件的file目录下的地址
                val filesDir = it.filesDir
                if (filesDir != null) {
                    val path = filesDir.path
                    if (!TextUtils.isEmpty(path)) {
                        writeContentToFile(path, androidId)
                    }
                }
            }
        }
    }

    private fun writeContentToFile(path: String, androidId: String) {
        LogUtil.e("path::$path")

        // 判断文档地址是否存在，不存在就创建
        val parentFile = File(path)
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        // 如果存在了，就去创建子文件
        if (parentFile.exists()) {
            val childFile = File(path + File.separator, fileName)
            if (!childFile.exists()) {
                childFile.createNewFile()
            }

            if (childFile.exists()) { // 写入数据
                val success = FileUtil
                    .getInstance()
                    .writeContentToFile(childFile, androidId)
                LogUtil.e(TAG, "Device 文件写入成功：$success")
            } else {
                LogUtil.e(TAG, "Device 子类文件创建失败！")
            }
        } else {
            LogUtil.e(TAG, "Device 父类文件创建失败！")
        }
    }

    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context?): String {
        var deviceId = ""
        context?.let {
            // 获取android的id值
            deviceId = Settings.Secure.getString(it.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            if (TextUtils.isEmpty(deviceId)) {
                // 设备的唯一标识。由设备的多个信息拼接合成
                deviceId = Build.FINGERPRINT
            }
            if (!TextUtils.isEmpty(deviceId)) {
                return MD5Utils.getMD5String(deviceId)
            }
        }
        return deviceId
    }

}