package com.android.helper.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.android.common.utils.LogUtil;
import com.android.helper.app.BaseApplication;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络状态的工具
 */
public class NetworkUtil {

    private static NetworkUtil networkUtil;
    private static ConnectivityManager connectivityManager;
    private Application application;

    private NetworkUtil() {
        if (connectivityManager == null) {
            if (application == null) {
                application = BaseApplication.getInstance().getApplication();
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
     * @param context 上下文对象
     * @return 获取ip地址的字符串，如果是WIFI网络下，获取到的是192.168.0.x 的地址，如果是手机网络的情况下，获取到的是10.x.x.x的数据
     */
    public static String getIPAddress(Context context) {
        Context applicationContext = null;
        if (context != null) {
            if (!(context instanceof Application)) {
                applicationContext = context.getApplicationContext();
            }
            if (applicationContext != null) {
                NetworkInfo info = ((ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                        try {
                            //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                            while (networkInterfaces.hasMoreElements()) {
                                NetworkInterface element = networkInterfaces.nextElement();
                                Enumeration<InetAddress> inetAddresses = element.getInetAddresses();
                                if (inetAddresses != null) {
                                    while (inetAddresses.hasMoreElements()) {
                                        InetAddress address = inetAddresses.nextElement();
                                        if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                                            return address.getHostAddress();
                                        }
                                    }
                                }
                            }
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                        WifiManager wifiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        return intIP2StringIP(wifiInfo.getIpAddress());
                    }
                } else {
                    //当前无网络连接,请在设置中打开网络
                    LogUtil.e("网络没有链接，无法获取Ip地址！");
                }
            }
        }
        return null;
    }

    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * @return 当前网络是否连接中
     */
    public boolean isNetworkConnected() {
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
