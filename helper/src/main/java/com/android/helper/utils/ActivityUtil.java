package com.android.helper.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ActivityUtil {

    /**
     * 判断Activity是否Destroy
     *
     * @param activity 依赖的页面
     * @return 如果activity已经被销毁了，则返回为true，否则返回false
     */
    public static boolean isDestroy(Activity activity) {
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }

    /**
     * 判断Activity是否Destroy
     *
     * @param context 依赖的页面
     * @return 如果activity已经被销毁了，则返回为true，否则返回false
     */
    public static boolean isDestroy(Context context) {
        if (context == null) {
            return true;
        } else {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                return isDestroy(activity);
            } else {
                return true;
            }
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    public static void showActivity(Activity activity, @NonNull String packageName) {
        if (activity != null) {
            try {
                Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
                activity.startActivity(intent);
            } catch (Exception e) {
                LogUtil.e("跳转页面失败：" + e.getMessage());
            }
        }
    }

    /**
     * 跳转到指定应用的指定页面
     */
    public static void showActivity(Activity activity, @NonNull String packageName, @NonNull String activityDir) {
        if (activity != null) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, activityDir));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } catch (Exception e) {
                LogUtil.e("跳转页面失败：" + e.getMessage());
            }
        }
    }

    /**
     * 跳转到手机的安全中心管理页面上去
     */
    public static void toSecureManager(Activity activity) {
        boolean huawei = isHuawei();
        if (isHuawei()) {
            goHuaweiSetting(activity);
        } else if (isXiaomi()) {
            goXiaomiSetting(activity);
        } else if (isOPPO()) {
            goOPPOSetting(activity);
        } else if (isVivo()) {
            goVIVOSetting(activity);
        } else if (isMeizu()) {
            goMeizuSetting(activity);
        } else if (isSamsung()) {
            goSamsungSetting(activity);
        } else if (isLeTV()) {
            goLetvSetting(activity);
        } else if (isSmartisan()) {
            goSmartisanSetting(activity);
        }
    }

    public static boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    private static void goHuaweiSetting(Activity activity) {
        if (activity != null) {
            try {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                } catch (Exception e) {
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    } catch (Exception e2) {
                        LogUtil.e("跳转页面失败：" + e.getMessage());
                        try {
                            Intent intent = new Intent(activity.getPackageName());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
                            intent.setComponent(comp);
                            activity.startActivity(intent);
                        } catch (Exception e3) {
                            e.printStackTrace();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            try {
                                activity.startActivity(intent);
                            } catch (Exception e33) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e4) {

            }
        }
    }

    public static boolean isXiaomi() {
        return Build.BRAND != null && ((Build.BRAND.toLowerCase().equals("xiaomi")) || (Build.BRAND.toLowerCase().equals("redmi")));
    }

    private static void goXiaomiSetting(Activity activity) {
        showActivity(activity, "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity");
    }

    public static boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
    }

    private static void goOPPOSetting(Activity activity) {
        try {
            showActivity(activity, "com.coloros.phonemanager");
        } catch (Exception e1) {
            try {
                showActivity(activity, "com.oppo.safe");
            } catch (Exception e2) {
                try {
                    showActivity(activity, "com.coloros.oppoguardelf");
                } catch (Exception e3) {
                    showActivity(activity, "com.coloros.safecenter");
                }
            }
        }
    }

    public static boolean isVivo() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
    }

    private static void goVIVOSetting(Activity activity) {
        showActivity(activity, "com.iqoo.secure");
    }

    public static boolean isMeizu() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("meizu");
    }

    private static void goMeizuSetting(Activity activity) {
        showActivity(activity, "com.meizu.safe");
    }

    public static boolean isSamsung() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("samsung");
    }

    private static void goSamsungSetting(Activity activity) {
        try {
            showActivity(activity, "com.samsung.android.sm_cn");
        } catch (Exception e) {
            showActivity(activity, "com.samsung.android.sm");
        }
    }

    public static boolean isLeTV() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("letv");
    }

    private static void goLetvSetting(Activity activity) {
        showActivity(activity, "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity");
    }

    public static boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("smartisan");
    }

    private static void goSmartisanSetting(Activity activity) {
        showActivity(activity, "com.smartisanos.security");
    }
}
