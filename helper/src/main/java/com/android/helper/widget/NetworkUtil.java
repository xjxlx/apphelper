package com.android.helper.widget;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.helper.app.BaseApplication;

/**
 * 网络状态的工具
 */
public class NetworkUtil {

    private static NetworkUtil networkUtil;
    private Application application;
    private static ConnectivityManager connectivityManager;

    private NetworkUtil() {
        if (connectivityManager == null) {
            if (application == null) {
                application = BaseApplication.getApplication();
            }
            if (application != null) {
                connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
        }
    }

    public static NetworkUtil getInstance() {
        if (networkUtil == null || connectivityManager == null) {
            networkUtil = new NetworkUtil();
        }
        return networkUtil;
    }

    /**
     * @return 当前网络是否连接中
     */
    public static boolean isNetworkConnected() {
        if (connectivityManager != null) {
            NetworkInfo wifiInfo = connectivityManager.getActiveNetworkInfo();
            if (wifiInfo != null) {
                return wifiInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * @return 判断WIFI网络是否连接
     */
    public boolean isWifiConnect() {
        if (connectivityManager != null) {
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo != null) {
                return wifiInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * @return 判断手机网络是否连接
     */
    public boolean isMobileConnect() {
        if (connectivityManager != null) {
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo != null) {
                return wifiInfo.isConnected();
            }
        }
        return false;
    }

}
