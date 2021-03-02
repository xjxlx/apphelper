package android.helper.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class QywxService extends NotificationListenerService {
    
    public static final String TAG = "Notification";
    
    public QywxService() {
    }
    
    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_REDELIVER_INTENT, startId);
    }
    
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.e(TAG, "onListenerConnected --- 建立监听成功！");
    }
    
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.e(TAG, "onListenerDisconnected --- 监听断开！");
    }
    
    // 通知被移除时回调
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.e(TAG, "onNotificationRemoved");
    }
    
    // 增加一条通知时回调
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        
        // 获取包名
        String packageName = sbn.getPackageName();
        Log.e(TAG, "获取到消息的包名为：" + packageName);
        // 获取对象
        Notification notification = sbn.getNotification();
        if (notification != null) {
            // 当 API > 18 时，使用 extras 获取通知的详细信息
            Bundle extras = notification.extras;
            if (extras != null) {
                // 获取通知标题
                String title = extras.getString(Notification.EXTRA_TITLE, "");
                // 获取通知内容
                String content = extras.getString(Notification.EXTRA_TEXT, "");
                
                Log.e(TAG, "获取到消息的title为：" + title + "   获取的内容为：" + content);
                
                if (!TextUtils.isEmpty(content)) {
                    if ((content.contains("打开企业微信")) || ((content.contains("切换本身应用")))) {
                        Log.e(TAG, "接收到发送的指令： 打开企业微信");
                        boolean runningForeground = isRunningForeground(getBaseContext());
                        Log.e(TAG, "接收到发送的指令： 本应用是否已经处于前台：" + runningForeground);
                        
                        boolean isExist = appIsExist(getBaseContext(), "com.xjx.qywx");
                        
                        if (!runningForeground) {
                            boolean topApp = setTopApp(getBaseContext());
                            Log.e(TAG, "接收到发送的指令： 把本应用设置到于前台:  " + topApp);
                            if (topApp) {
                                if (content.contains("打开企业微信")) {
                                    if (isExist) {
                                        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage("com.tencent.wework");
                                        if (intent != null) {
                                            intent.putExtra("type", "110");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            getBaseContext().startActivity(intent);
                                            Log.e(TAG, "接收到发送的指令： 去打开企业微信 !");
                                        }
                                    }
                                }
                            }
                        } else {
                            if (content.contains("打开企业微信")) {
                                if (isExist) {
                                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage("com.tencent.wework");
                                    if (intent != null) {
                                        intent.putExtra("type", "110");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getBaseContext().startActivity(intent);
                                        Log.e(TAG, "接收到发送的指令： 去打开企业微信 !");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 判断本地是否已经安装好了指定的应用程序包
     *
     * @param packageNameTarget ：待判断的 App 包名，如 微博 com.sina.weibo
     * @return 已安装时返回 true,不存在时返回 false
     */
    public boolean appIsExist(Context context, String packageNameTarget) {
        if (!"".equals(packageNameTarget.trim())) {
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
            for (PackageInfo packageInfo : packageInfoList) {
                String packageNameSource = packageInfo.packageName;
                if (packageNameSource.equals(packageNameTarget)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public boolean setTopApp(Context context) {
        if (!isRunningForeground(context)) {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            
            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 判断本应用是否已经位于最前端
     *
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        /**枚举进程*/
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}