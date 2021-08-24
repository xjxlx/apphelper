package com.android.helper.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 包类相关的工具类，用来获取本地包的信息
 */
public class PackageUtil {

    private PackageInfo packInfo = null;
    private Activity mActivity;

    private PackageUtil(Activity activity) {
        if (activity != null) {
            mActivity = activity;
            if (packInfo == null) {
                packInfo = getPackInfo();
            }
        }
    }

    public static PackageUtil getInstance(Activity activity) {
        return new PackageUtil(activity);
    }

    private PackageInfo getPackInfo() {
        if (mActivity != null) {
            PackageManager packageManager = mActivity.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            try {
                if (packageManager != null) {
                    packInfo = packageManager.getPackageInfo(mActivity.getPackageName(), 0);
                    if (packInfo != null) {
                        return packInfo;
                    } else {
                        LogUtil.e("获取PackInfo信息失败");
                        return null;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                LogUtil.e("获取PackInfo信息失败：" + e.getMessage());
                return null;
            }
        }
        return null;
    }

    public int getVersionCode() {
        if (packInfo != null) {
            LogUtil.e("获取版本号成功");
            return packInfo.versionCode;
        } else {
            LogUtil.e("获取版本号失败");
            return 0;
        }
    }
}
