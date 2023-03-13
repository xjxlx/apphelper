package com.android.helper.utils;

import android.text.TextUtils;

import com.android.helper.app.BaseApplication;
import com.android.helper.common.CommonConstants;
import com.orhanobut.logger.Logger;

/**
 * @author XJX  日志工具类
 */
public class LogUtil {

    private static LogWriteUtil writeUtil;

    public static void e(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.e(value);
            }
        }
    }

    public static void e(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.t(tag).e(value);
            }
        }
    }

    public static void d(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.d(value);
            }
        }
    }

    public static void d(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.t(tag).d(value);
            }
        }
    }

    public static void i(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.i(value);
            }
        }
    }

    public static void i(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.t(tag).i(value);
            }
        }
    }

    public static void w(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.w(value);
            }
        }
    }

    public static void w(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (isDebug()) {
                Logger.t(tag).w(value);
            }
        }
    }

    /**
     * 专门用来监听邦车逻辑的log
     *
     * @param msg 具体的消息
     */
    public static void car(String msg) {
        if (isDebug()) {
            Logger.t("BindCar").e(msg);
        }
    }

    /**
     * 在debug模式下会把日志写入到sd卡中
     *
     * @param fileName 文件的名字,例如：电车数据 ，这里不用带格式名字，方法会自动拼写。
     * @param value    写入的内容
     */
    public static void writeDe(String fileName, String value) {
        if ((!TextUtils.isEmpty(fileName) && (!TextUtils.isEmpty(value)))) {
            if (isDebug()) {
                if (writeUtil == null) {
                    writeUtil = new LogWriteUtil();
                }
                writeUtil.write(fileName, value);
            }
        }
    }

    /**
     * 写入保活的信息日志
     */
    public static void writeLifeCycle(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (writeUtil == null) {
                writeUtil = new LogWriteUtil();
            }
            writeUtil.write(CommonConstants.FILE_LIFECYCLE_NAME, value);
            if (isDebug()) {
                Logger.e("应用保活：" + value);
            }
        }
    }

    /**
     * 设置充电中心的数据
     *
     * @param value 具体的数据
     */
    public static void writeChargingCenter(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (writeUtil == null) {
                writeUtil = new LogWriteUtil();
            }
            writeUtil.write(CommonConstants.FILE_CHARGING_CENTER_NAME, value);
            if (isDebug()) {
                Logger.e("充电中心：" + value);
            }
        }
    }

    /**
     * 写入保活的信息日志
     */
    public static void write(String fileName, String value) {
        if ((!TextUtils.isEmpty(value)) && (!TextUtils.isEmpty(fileName))) {
            if (writeUtil == null) {
                writeUtil = new LogWriteUtil();
            }
            writeUtil.write(fileName, value);
        }
    }

    /**
     * @return 检测当前时候是debug模式，默认不是
     */
    public static boolean isDebug() {
        boolean debug = false;
        BaseApplication application = BaseApplication.getInstance();
        if (application != null) {
            debug = application.isDebug();
        }
        return debug;
    }

}

