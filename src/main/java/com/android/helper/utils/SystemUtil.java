package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class SystemUtil {

    /**
     * 跳转activity的请求码
     */
    public static final int CODE_REQUEST_ACTIVITY_BATTERY = NotificationUtil.CODE_REQUEST_ACTIVITY_NOTIFICATION + 1;
    public static Application mApp;
    public static SystemUtil systemUtil;

    public static SystemUtil getInstance(Application application) {
        if (application != null) {
            mApp = application;
        }
        if (systemUtil == null) {
            systemUtil = new SystemUtil();
        }
        return systemUtil;
    }

    /**
     * @return 检测是否在后台运行的白名单当中，也就是电池的优化权限
     */
    public boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        if (mApp != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager powerManager = (PowerManager) mApp.getSystemService(Context.POWER_SERVICE);
                if (powerManager != null) {
                    isIgnoring = powerManager.isIgnoringBatteryOptimizations(mApp.getPackageName());
                }
            }
        }
        return isIgnoring;
    }

    /**
     * 申请白名单
     */
    @SuppressLint("BatteryLife")
    public void requestIgnoreBatteryOptimizations() {
        if (mApp != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("package:" + mApp.getPackageName()));
                    mApp.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 申请白名单
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations(Activity activity) {
        if (activity != null) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
                activity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BATTERY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    public void showActivity(@NonNull String packageName) {
        if (mApp != null) {
            Intent intent = mApp.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApp.startActivity(intent);
        }
    }

    /**
     * 跳转到指定应用的指定页面
     */
    public void showActivity(@NonNull String packageName, @NonNull String activityDir) {
        if (mApp != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, activityDir));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApp.startActivity(intent);
        }
    }

    public boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.equalsIgnoreCase("huawei") || Build.BRAND.equalsIgnoreCase("honor");
        }
    }

    public void goHuaweiSetting() {
        try {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch (Exception e) {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
        }
    }

    public boolean isXiaomi() {
        return Build.BRAND != null && ((Build.BRAND.equalsIgnoreCase("xiaomi")) || (Build.BRAND.equalsIgnoreCase("redmi")));
    }

    public void goXiaomiSetting() {
        showActivity("com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity");
    }

    public boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.equalsIgnoreCase("oppo");
    }

    public void goOPPOSetting() {
        try {
            showActivity("com.coloros.phonemanager");
        } catch (Exception e1) {
            try {
                showActivity("com.oppo.safe");
            } catch (Exception e2) {
                try {
                    showActivity("com.coloros.oppoguardelf");
                } catch (Exception e3) {
                    showActivity("com.coloros.safecenter");
                }
            }
        }
    }

    public boolean isVIVO() {
        return Build.BRAND != null && Build.BRAND.equalsIgnoreCase("vivo");
    }

    public void goVIVOSetting() {
        showActivity("com.iqoo.secure");
    }

    public boolean isMeizu() {
        return Build.BRAND != null && Build.BRAND.equalsIgnoreCase("meizu");
    }

    public void goMeizuSetting() {
        showActivity("com.meizu.safe");
    }

    public boolean isSamsung() {
        return Build.BRAND != null && Build.BRAND.equalsIgnoreCase("samsung");
    }

    public void goSamsungSetting() {
        try {
            showActivity("com.samsung.android.sm_cn");
        } catch (Exception e) {
            showActivity("com.samsung.android.sm");
        }
    }

    public boolean isLeTV() {
        return Build.BRAND != null && Build.BRAND.equalsIgnoreCase("letv");
    }

    public void goLetvSetting() {
        showActivity("com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity");
    }

    public boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.equalsIgnoreCase("smartisan");
    }

    public void goSmartisanSetting() {
        showActivity("com.smartisanos.security");
    }
}
