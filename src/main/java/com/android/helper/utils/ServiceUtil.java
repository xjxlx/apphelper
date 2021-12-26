package com.android.helper.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.helper.app.BaseApplication;

import java.util.List;

public class ServiceUtil {

    private static JobScheduler scheduler;

    /**
     * 开始服务 ，如果使用了startForegroundService（）这个方法，那么就必须要在service中开通 startForeground(1, notification)方法，可以尝试使用
     * NotificationUtil 这个类去创建对象
     *
     * @param context 上下文
     * @param intent  跳转的意图
     */
    public static void startService(Context context, Intent intent) {
        if (context != null && intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
                LogUtil.e("开启了前台的服务！");
            } else {
                context.startService(intent);
                LogUtil.e("开启了后台的服务！");
            }
        }
    }

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

    /**
     * @param context 上下文
     * @param cls     jobService的对象
     * @return 如果当前有指定的jobService在运行，就返回true，否则返回false
     */
    public synchronized static boolean isJobServiceRunning(Context context, Class<?> cls) {
        if (scheduler == null) {
            scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }

        boolean hasBeenScheduled = false;
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            ComponentName service = jobInfo.getService();
            if (service != null) {
                String jobServiceName = service.getClassName();
                String className = cls.getName();
                LogUtil.e("job:" + jobServiceName + "  cls:" + className + "  " + TextUtils.equals(jobServiceName, className));
                if (TextUtils.equals(jobServiceName, className)) {
                    hasBeenScheduled = true;
                    break;
                }
            }
        }
        return hasBeenScheduled;
    }

    /**
     * @param context 上下文
     * @param clsName jobService的名字，例如： com.jaychan.demo.service.PushService
     * @return 如果当前有指定的jobService在运行，就返回true，否则返回false
     */
    public synchronized static boolean isJobServiceRunning(Context context, String clsName) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            ComponentName service = jobInfo.getService();
            if (service != null) {
                String jobServiceName = service.getClassName();
                if (TextUtils.equals(jobServiceName, clsName)) {
                    hasBeenScheduled = true;
                    break;
                }
            }
        }
        return hasBeenScheduled;
    }

    /**
     * @return 判断 Notification access 是否开启
     */
    public static boolean notificationEnabled() {
        Application application = BaseApplication.getInstance().getApplication();
        if (application != null) {
            String pkgName = application.getPackageName();
            final String flat = Settings.Secure.getString(application.getContentResolver(), "enabled_notification_listeners");
            if (!TextUtils.isEmpty(flat)) {
                final String[] names = flat.split(":");
                for (String name : names) {
                    final ComponentName cn = ComponentName.unflattenFromString(name);
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
