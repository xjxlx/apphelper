package android.helper.utils;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * Created by erge 2019-11-08 11:29
 */
public class ActivityManager {

    private Stack<Activity> activityStack = new Stack<>();
    private static ActivityManager instance;

    private ActivityManager() {

    }

    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishCurrentActivity() {
        Activity activity = activityStack.lastElement();
        finishSpecifiedActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishSpecifiedActivity(Activity activity) {
        if (activity != null) {
            boolean remove = activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishSpecifiedActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束除当前Activity外其他所有Activity
     */
    public void finishAllOtherActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {

            if (null != activityStack.get(i) && i != activityStack.size() - 1) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出登录时finish掉Activity
     */
    public void logoutFinishActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {

            if (null != activityStack.get(i) && i != activityStack.size() - 1) {
                Activity activity = activityStack.get(i);
//                if (!activity.getClass().getName().equals("com.fxh.auto.ui.activity.common.MainActivity")) {
//                    activityStack.get(i).finish();
//                }
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
