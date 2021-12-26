package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.StateSet;

import com.android.helper.app.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AppUtil {

    private PackageInfo packageInfo;
    private final String TAG = "AppUtil";
    private final String enCode = StandardCharsets.UTF_8.name();
    private static AppUtil INSTANCE;

    private AppUtil() {

    }

    public static AppUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (AppUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppUtil();
                }
            }
        }
        return INSTANCE;
    }

    public PackageInfo getPackageInfo() {
        try {
            if (packageInfo == null) {
                if (BaseApplication.getInstance().getApplication() != null) {
                    Application application = BaseApplication.getInstance().getApplication();
                    packageInfo = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "获取报名信息失败:" + e.getMessage());
        }
        return packageInfo;
    }

    public String getVersionName() {
        try {
            PackageInfo packageInfo = getPackageInfo();
            if (packageInfo != null) {
                return packageInfo.versionName;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getVersionCode() {
        try {
            PackageInfo packageInfo = getPackageInfo();
            if (packageInfo != null) {
                return packageInfo.versionCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return 获取项目的目标版本
     */
    public int getTargetSdkVersion() {
        int mTargetSdkVersion = Build.VERSION.SDK_INT;
        try {
            if (BaseApplication.getInstance().getApplication() != null) {
                ApplicationInfo applicationInfo = BaseApplication.getInstance().getApplication().getApplicationInfo();
                if (applicationInfo != null) {
                    mTargetSdkVersion = applicationInfo.targetSdkVersion;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTargetSdkVersion;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * @return 获取app的版本信息
     */
    public String getAppInfo() {
        String result = "";
        JSONObject object = new JSONObject();
        try {
            object.put("versionName", getVersionName());
            object.put("versionCode", getVersionCode());
            object.put("mobileBrand", getDeviceBrand());
            object.put("mobileModel", getSystemModel());
            object.put("mobileSystemVersion", getSystemVersion());
            result = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param context     上下文
     * @param packageName 报名
     * @return 如果app活着就返回true，否则就返回false
     */
    public boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            String processName = processInfo.processName;
            Log.e("processName：", processName);
        }
        return "";
    }

    /**
     * 获取已经安装的应用包列表，在某些手机上，会获取不到，因为某些手机的权限比较高，自动屏蔽了获取信息的功能，如果手动打开了就可以，否则就不可以
     * <p>
     * 在android 11 上面，如果想要去获取到安装的应用信息，需要去增加一个权限
     * <ol>
     *     这个是查询所有的权限
     *   <uses-permission
     *      * android:name="android.permission.QUERY_ALL_PACKAGES"
     *      * tools:ignore="QueryAllPackagesPermission" />
     * </ol>
     *
     * <ol>
     *     这个是查询单个的权限
     *     <queries>
     *      <package android:name="com.autonavi.minimap" />
     *      </queries>
     * </ol>
     */
    @SuppressLint("QueryPermissionsNeeded")
    public static List<PackageInfo> getInstalledAppList() {
        List<PackageInfo> packages = new ArrayList<>();

        Application application = BaseApplication.getInstance().getApplication();
        if (application != null) {
            PackageManager pm = application.getPackageManager();
            // 目前只查看已经安装的activity，不考虑后台的Service的应用
            //   int flag = PackageManager.GET_ACTIVITIES;
            int flag = PackageManager.GET_UNINSTALLED_PACKAGES;
            if (pm != null) {
                packages = pm.getInstalledPackages(flag);
                //  if (packages.size() > 0) {
                //      for (int i = 0; i < packages.size(); i++) {
                //          LogUtil.e("package:" + packages.get(i).packageName);
                //      }
                //   }
            }
        }
        return packages;
    }

    /**
     * <p>
     * android 11 上面，如果想要去获取到安装的应用信息，需要去增加一个权限
     * <ol>
     *     这个是查询所有的权限
     *   <uses-permission
     *      * android:name="android.permission.QUERY_ALL_PACKAGES"
     *      * tools:ignore="QueryAllPackagesPermission" />
     * </ol>
     *
     * <ol>
     *     这个是查询单个的权限
     *     <queries>
     *      <package android:name="com.autonavi.minimap" />
     *      </queries>
     * </ol>
     *
     * @param packageName 应用的包名
     * @return 根据应用的包名，反向去获取指定应用的信息，如果获取成功了，则说明安装了指定的app,否则就是没有安装该应用
     */
    public static boolean checkInstalledApp(Context context, String packageName) {
        boolean hasApp = false;
        if (TextUtils.isEmpty(packageName) || (context == null)) {
            return false;
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            hasApp = packageManager.getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            // 抛出找不到的异常，说明该程序已经被卸载
        }
        return hasApp;
    }

}
