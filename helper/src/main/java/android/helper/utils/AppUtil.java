package android.helper.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AppUtil {
    
    private PackageInfo packageInfo;
    private final String TAG = "AppUtil";
    private final Context mContext;
    private final String enCode = StandardCharsets.UTF_8.name();
    
    public AppUtil(Context mContext) {
        this.mContext = mContext;
    }
    
    public PackageInfo getPackageInfo() {
        try {
            if (packageInfo == null) {
                if (mContext != null) {
                    packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
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
    
}
