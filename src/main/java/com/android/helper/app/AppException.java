package com.android.helper.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.common.utils.LogUtil;
import com.android.helper.utils.ActivityManager;
import com.android.helper.utils.FileUtil;
import com.google.gson.JsonParseException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import retrofit2.HttpException;

/**
 * 崩溃异常的收集
 */
public class AppException extends Exception implements UncaughtExceptionHandler {

    @SuppressLint("StaticFieldLeak")
    private static AppException INSTANCE;
    private final UncaughtExceptionHandler mDefaultHandler;
    private final Context mContext;
    private final HashMap<String, String> mMapParameter = new LinkedHashMap<>();
    private static String mFileTag = "";
    private String TAG = "AppException";
    private int mTargetVersion = 30;// 默认是30，高版本

    private AppException(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static AppException getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppException(context);
        }
        return INSTANCE;
    }

    @Override
    public void uncaughtException(@NotNull Thread thread, @NotNull Throwable ex) {
        LogUtil.e(TAG, "uncaughtException ----->");
        if (mDefaultHandler != null && !handleException(thread, ex)) {
            LogUtil.e(TAG, "uncaughtException -----> 自定义消耗！ ");
            // 如果没有处理就交给系统处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            LogUtil.e(TAG, "uncaughtException -----> 系统停止！ ");
            // Sleep一会后结束程序
            try {
                Thread.sleep(1500);
                ActivityManager.getInstance().AppExit(mContext);
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "AppException --- Error : " + e.getMessage());
            }
        }
    }

    private boolean saveErrorLog(Throwable ex) {
        LogUtil.e(TAG, "saveErrorLog ---> ");
        FileWriter fw;
        PrintWriter pw;
        try {
            // 获取路径
            String path = getErrorLogPath();
            LogUtil.e(TAG, "app --- path: " + path);
            if (TextUtils.isEmpty(path)) {
                return false;
            }
            fw = new FileWriter(path);
            pw = new PrintWriter(fw);
            pw.println(
                    "--------------------" + (DateFormat.getDateTimeInstance().format(new Date())) + "---------------------");

            Map<String, String> parameter = getParameter();
            for (Entry<String, String> entry : parameter.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                pw.println(key + " : " + value);
            }
            pw.println("Exception: " + ex.getMessage() + "\n");
            ex.printStackTrace(pw);
            pw.close();
            fw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "AppException --- App崩溃信息异常，请检查是否给予了应用读写权限！ --->error:" + e.getMessage());
        }
        return false;
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     *
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(Thread thread, Throwable ex) {
        LogUtil.e("全局捕获的异常："+ex.getMessage());
        LogUtil.e(TAG, "uncaughtException -----> handleException ！");
        if (thread == null || ex == null || mContext == null) {
            LogUtil.e(TAG, "uncaughtException -----> handleException  false = null ！ ");
            return false;
        } else {
            try {
                boolean saveErrorLog = saveErrorLog(ex);
                LogUtil.e(TAG, "uncaughtException ---> save error ---> 日志是否写成功：" + saveErrorLog);
                return saveErrorLog;
            } catch (Exception ignored) {
                LogUtil.e(TAG, "uncaughtException ----->handleException  false = 2 ！ ");
                return false;
            }
        }
    }

    /**
     * 获取App安装包信息
     */
    private PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            String packageName = mContext.getPackageName();
            info = mContext.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 处理错误的异常信息
     */
    public static String exception(Throwable e) {
        String msg = "";
        if (e != null) {
            String message = e.getMessage();
            if (e instanceof HttpException) {
                msg = "Http异常：" + message;
            } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
                msg = "数据解析异常：" + message;
            } else if (e instanceof ConnectException) {
                msg = "服务链接异常：" + message;
            } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
                msg = "SSL异常：" + message;
            } else if (e instanceof SocketTimeoutException) {
                msg = "读取超时：" + message;
            } else {
                msg = "未知异常：" + message;
            }
        }
        return msg;
    }

    /**
     * @return 返回一个应用目录下的File文件的路径，这里不适用sd卡的路径，在高版本手机上，很多权限被拒绝，这里尽量避免类似的情况
     */
    public String getErrorLogPath() {
        LogUtil.e(TAG, "getErrorLogPath:  ---> ");
        if (mContext != null) {
            String fileName = "";
            if (!TextUtils.isEmpty(mFileTag)) {
                fileName = mFileTag + "_error.txt";
            } else {
                fileName = "error.txt";
            }
            LogUtil.e(TAG, "getErrorLogPath:  ---> fileName ：" + fileName + "  TargetVersion: " + mTargetVersion);

            File parentFile;
            if (mTargetVersion > Build.VERSION_CODES.Q) {
                // 大于android 10 的版本，存入到不可见的沙盒目录中
                File filesDir = mContext.getFilesDir();
                parentFile = new File(filesDir + File.separator + "error" + File.separator);
            } else {
                // 小于android 10 ,存入到sd卡的文件目录下
                // /storage/emulated/0/Documents/com.jollyeng.www/error/error.txt
                String sdkPath = FileUtil.getInstance().getSdTypePublicPath(Environment.DIRECTORY_DOCUMENTS);
                parentFile = new File(
                        sdkPath + File.separator + mContext.getPackageName() + File.separator + "error" + File.separator);
            }

            boolean exists = parentFile.exists();
            LogUtil.e(TAG, "getErrorLogPath: parentFile ---> exists: " + exists);

            if (!exists) {
                boolean mkdirs = parentFile.mkdirs();
                LogUtil.e(TAG, "getErrorLogPath: parentFile ---> mkdirs: " + mkdirs);
            }

            File file = new File(parentFile, fileName);
            boolean existsFile = file.exists();
            LogUtil.e(TAG, "getErrorLogPath: childFile ---> exists : " + existsFile);

            if (!existsFile) {
                try {
                    boolean newFile = file.createNewFile();
                    LogUtil.e(TAG, "getErrorLogPath: childFile ---> newFile:  create: " + newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return file.getPath();
        }
        return "";
    }

    public void setFileTag(String fileName) {
        this.mFileTag = fileName;
    }

    public void setTargetVersion(int targetVersion) {
        this.mTargetVersion = targetVersion;
    }

    public Map<String, String> getParameter() {
        PackageInfo packageInfo = getPackageInfo();
        mMapParameter.put("App版本", packageInfo.versionName);
        mMapParameter.put("App版本号", packageInfo.versionCode + "");
        mMapParameter.put("系统版本", android.os.Build.VERSION.RELEASE);
        mMapParameter.put("系统品牌", android.os.Build.BRAND);
        mMapParameter.put("系统型号", android.os.Build.MODEL);
        return mMapParameter;
    }
}
