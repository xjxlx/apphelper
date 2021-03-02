package android.helper.utils;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import android.helper.app.BaseApplication;

/**
 * @author XJX
 */
public class LogUtil {

    private static LogWriteUtil writeUtil;

    public static void e(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
                Logger.e(value);
            }
        }
    }

    public static void e(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
                Logger.t(tag).e(value);
            }
        }
    }

    public static void d(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
                Logger.d(value);
            }
        }
    }

    public static void d(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
                Logger.t(tag).d(value);
            }
        }
    }

    public static void i(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
                Logger.i(value);
            }
        }
    }

    public static void i(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
                Logger.t(tag).i(value);
            }
        }
    }

    public static void w(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
                Logger.w(value);
            }
        }
    }

    public static void w(String tag, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (BaseApplication.isDebug()) {
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
        if (BaseApplication.isDebug()) {
            Logger.t("BindCar").e(msg);
        }
    }

    /**
     * 在debug模式下会把日志写入到sd卡中
     *
     * @param fileName 文件的名字
     * @param value    写入的内容
     */
    public static void writeDe(String fileName, String value) {
        if ((!TextUtils.isEmpty(fileName) && (!TextUtils.isEmpty(value)))) {
            if (BaseApplication.isDebug()) {
                if (writeUtil == null) {
                    writeUtil = new LogWriteUtil();
                }
                writeUtil.write(fileName, value);
            }
        }
    }


}

