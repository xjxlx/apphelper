package com.android.helper.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.helper.utils.PreferenceHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import retrofit2.HttpException;

/**
 * author ATao
 * version 1.0
 * created 2015/9/29
 */
public class AppException extends Exception implements UncaughtExceptionHandler {

    // 日志文件的路径
    public final static String DEFAULT_SAVE_LOG_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + BaseApplication.getLogTag()
            + File.separator + "log" + File.separator;

    /**
     * 定义异常类型
     */
    public final static byte TYPE_NETWORK = 0x01;
    public final static byte TYPE_SOCKET = 0x02;
    public final static byte TYPE_HTTP_CODE = 0x03;
    public final static byte TYPE_HTTP_ERROR = 0x04;
    public final static byte TYPE_XML = 0x05;
    public final static byte TYPE_IO = 0x06;
    public final static byte TYPE_RUN = 0x07;
    public final static byte TYPE_JSON = 0x08;

    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    private byte type;
    private int code;

    public byte getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    private AppException() {
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    private AppException(byte type, int code, Exception excp) {
        super(excp);
        this.type = type;
        this.code = code;
//        makeToast(AppManager.getAppManager().getContext());
    }
//
//    public void makeToast(Context ctx) {
//        switch (this.getType()) {
//            case TYPE_HTTP_CODE:
//                String err = ctx.getString(R.string.http_status_code_error, this.getCode());
//                ToastUtil.showToast(ctx, err);
//                break;
//            case TYPE_HTTP_ERROR:
//                Toast.makeText(ctx, R.string.http_exception_error, Toast.LENGTH_SHORT).show();
//                break;
//            case TYPE_SOCKET:
//                Toast.makeText(ctx, R.string.socket_exception_error, Toast.LENGTH_SHORT).show();
//                break;
//            case TYPE_NETWORK:
//                Toast.makeText(ctx, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
//                break;
//            case TYPE_XML:
//                Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
//                break;
//            case TYPE_JSON:
//                Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
//                break;
//            case TYPE_IO:
//                Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT).show();
//                break;
//            case TYPE_RUN:
//                Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }

    public static AppException http(int code) {
        return new AppException(TYPE_HTTP_CODE, code, null);
    }

    public static AppException http(Exception e) {
        return new AppException(TYPE_HTTP_ERROR, 0, e);
    }

    public static AppException socket(Exception e) {
        return new AppException(TYPE_SOCKET, 0, e);
    }

    public static AppException io(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof IOException) {
            return new AppException(TYPE_IO, 0, e);
        }
        return run(e);
    }

    public static AppException xml(Exception e) {
        return new AppException(TYPE_XML, 0, e);
    }

    public static AppException json(Exception e) {
        return new AppException(TYPE_JSON, 0, e);
    }

    public static AppException network(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof HttpException) {
            return http(e);
        } else if (e instanceof SocketException) {
            return socket(e);
        }
        return http(e);
    }

    public static AppException run(Exception e) {
        return new AppException(TYPE_RUN, 0, e);
    }

    public static AppException getAppExceptionHandler() {
        return new AppException();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            if (BaseApplication.isDebug()) {
                // Sleep一会后结束程序
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.e("AppException", "Error : ", e);
                }
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        }
    }

    public boolean saveErrorLog(Throwable ex) {
        boolean isSave = false;
        String errorLog = "error.txt";
        String savePath;
        String logFilePath = "";
        FileWriter fw = null;
        PrintWriter pw = null;
        Context context = BaseApplication.getContext();
        try {
            //判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(storageState)) {
                savePath = DEFAULT_SAVE_LOG_FILE_PATH;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                logFilePath = savePath + errorLog;
            }
            //没有挂载SD卡，无法写文件
            if (TextUtils.isEmpty(logFilePath)) {
                return false;
            }

            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            if (System.currentTimeMillis() - logFile.lastModified() > 5000) {
                isSave = true;
            }
            PreferenceHelper.write(context, "error", "hasError", true);
            PackageInfo pinfo = getPackageInfo();
            fw = new FileWriter(logFile, false);
            pw = new PrintWriter(fw);
            pw.println("--------------------" + (new Date().toLocaleString()) + "---------------------");
            pw.println("Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
            pw.println("Android: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.MODEL + ")\n");
            pw.println("Exception: " + ex.getMessage() + "\n");
            ex.printStackTrace(pw);
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AppException", "App崩溃信息异常，请检查是否给与了应用读写权限！ --->error:" + e.getMessage());
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ignored) {
                }
            }
        }
        return isSave;
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     *
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        boolean success = true;
        try {
            success = saveErrorLog(ex);
        } catch (Exception e) {
        } finally {
            if (!success) {
                return false;
            } else {
//                final Context context = AppManager.getAppManager()
//                        .currentActivity();
//                // 显示异常信息&发送报告
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//                        // 拿到未捕获的异常，
//                        String crashReport = getCrashReport(context, ex);
//                        sendAppCrashReport(context, crashReport);
//                        Looper.loop();
//                    }
//                }.start();
            }
        }
        return true;
    }

    /**
     * 获取APP崩溃异常报告
     *
     * @param ex
     * @return
     */
    private String getCrashReport(Context context, Throwable ex) {
        PackageInfo pinfo = getPackageInfo();
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
        exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.MODEL + ")\n");
        exceptionStr.append("Exception: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return exceptionStr.toString();
    }

    /**
     * 获取App安装包信息
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = BaseApplication.getContext().getPackageManager().getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }
}
