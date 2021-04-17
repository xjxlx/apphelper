package com.android.helper.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtil {
    
    /**
     * 判断服务是否正在运行
     *
     * @param cls     服务类的全路径名称 例如： com.jaychan.demo.service.PushService  是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @param context 上下文对象
     * @return true ：运行中  false:  没有运行中
     */
    public static boolean isServiceRunning(Context context, Class cls) {    //活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100); //获取运行的服务,参数表示最多返回的数量
        
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(cls.getName())) {
                return true; //判断服务是否运行
            }
        }
        return false;
    }
    
    
    /**
     * 判断服务是否正在运行
     *
     * @param cls     服务类的全路径名称 例如： com.jaychan.demo.service.PushService  是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @param context 上下文对象
     * @return true ：运行中  false:  没有运行中
     */
    public static boolean isServiceRunning(Context context, String cls) {    //活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100); //获取运行的服务,参数表示最多返回的数量
        
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(cls)) {
                return true; //判断服务是否运行
            }
        }
        return false;
    }
    
    
    
}
