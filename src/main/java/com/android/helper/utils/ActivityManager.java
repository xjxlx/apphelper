package com.android.helper.utils;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * Activity的管理类
 */
public class ActivityManager {

    private static final Stack<Activity> activityStack = new Stack<>();
    private static ActivityManager manager;

    private ActivityManager() {
    }

    public synchronized static ActivityManager getInstance() {
        if (manager == null) {
            manager = new ActivityManager();
        }
        return manager;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activity != null) {
            activityStack.add(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (!activityStack.isEmpty()) {
            return activityStack.lastElement();
        }
        return null;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishCurrentActivity() {
        if (!activityStack.isEmpty()) {
            Activity activity = activityStack.lastElement();
            if (activity != null) {
                finishSpecifiedActivity(activity);
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishSpecifiedActivity(Activity activity) {
        if (activity != null) {
            if (!activityStack.isEmpty()) {
                activityStack.remove(activity);
                activity.finish();
                activity = null;
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (!activityStack.isEmpty()) {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    finishSpecifiedActivity(activity);
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (!activityStack.isEmpty()) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        }
    }

    /**
     * 结束除当前Activity外其他所有Activity
     */
    public void finishAllOtherActivity() {
        if (!activityStack.isEmpty()) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i) && i != activityStack.size() - 1) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        }
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
