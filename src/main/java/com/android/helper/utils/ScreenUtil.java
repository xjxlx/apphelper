package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.android.helper.common.CommonConstants;
import com.luck.picture.lib.tools.SPUtils;

import java.lang.reflect.Method;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;

public class ScreenUtil {

    private PowerManager pm;
    private KeyguardManager keyguardManager;

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕高度：包括底部虚拟导航栏的高度
     *
     * @return
     */
    public static int getScreenHeight2(Context context) {
        int dpi = 0;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;

    }

    /**
     * 判断底部导航栏是否展现
     *
     * @param context
     * @return
     */
    public static boolean isNavigationBarShow(Context context) {
        boolean isShow = false;
        View decorView = ((Activity) context).getWindow().getDecorView().getRootView();
        if (decorView instanceof ViewGroup) {
            View childAt = ((ViewGroup) decorView).getChildAt(0);
            if (childAt != null) {
                final int bottom = childAt.getBottom();
                isShow = bottom < getScreenHeight(context);
            }
        }
        return isShow;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取状态栏的高度
     */
    @SuppressLint("PrivateApi")
    public static int initStatusBarHeight(Context activity) {
        int statusBarHeight = 0;
        // 第一种方法，获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        if (statusBarHeight > 0) {
            // 把状态栏高度存入到sp中
            SpUtil.putInt(CommonConstants.KEY_STATUS_BAR_HEIGHT, statusBarHeight);
            LogUtil.e("获取状态栏的高度为：【1】--->" + statusBarHeight);
        } else {
            // 第二种方法
            if (activity instanceof Activity) {
                Activity activity1 = (Activity) activity;
                Rect localRect = new Rect();
                activity1.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
                statusBarHeight = localRect.top;
            }

            if (statusBarHeight > 0) {
                SpUtil.putInt(CommonConstants.KEY_STATUS_BAR_HEIGHT, statusBarHeight);
                LogUtil.e("获取状态栏的高度为：【2】--->" + statusBarHeight);
            } else {
                // 第三种方法
                Class<?> localClass;
                try {
                    localClass = Class.forName("com.android.internal.R$dimen");
                    Object localObject = localClass.newInstance();
                    int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                    statusBarHeight = activity.getResources().getDimensionPixelSize(i5);

                    // 存入本地sp中状态栏高度
                    if (statusBarHeight > 0) {
                        SPUtils.getInstance().put(CommonConstants.KEY_STATUS_BAR_HEIGHT, statusBarHeight);
                        LogUtil.e("获取状态栏的高度为：【3】--->" + statusBarHeight);
                    } else {
                        // 第四种方法
                        LogUtil.e("拿不到状态栏的高度，走的是默认25dp的方法");
                        float v1 = ConvertUtil.toDp(25);
                        // 四舍五入取整数
                        int round = Math.round(v1);
                        SpUtil.putInt(CommonConstants.KEY_STATUS_BAR_HEIGHT, round);
                        LogUtil.e("获取状态栏的高度为：【4】--->" + statusBarHeight);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return statusBarHeight;
    }

    public static int getStatusBarHeight() {
        return SpUtil.getInt(CommonConstants.KEY_STATUS_BAR_HEIGHT);
    }

    /**
     * 点亮屏幕并解锁
     *
     * @param activity 需要的activity的对象
     */
    public void unScreenKey(Activity activity) {
        if (activity == null) {
            throw new NullPointerException("activity为空，解锁失败");
        }

        // 添加Window的flag
        Window win = activity.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (pm == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pm = (PowerManager) activity.getSystemService(POWER_SERVICE);
            }
        }

        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakelock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP, "xx");
        wakelock.acquire(10 * 60 * 1000L /*10 minutes*/);
        wakelock.release();

        if (keyguardManager == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyguardManager = (KeyguardManager) activity.getApplicationContext().getSystemService(KEYGUARD_SERVICE);
            }
        }

        // 请求打开没有解锁的屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(activity, new KeyguardManager.KeyguardDismissCallback() {
                @Override
                public void onDismissError() {
                    super.onDismissError();
                    LogUtil.e("解锁错误！");
                }

                @Override
                public void onDismissSucceeded() {
                    super.onDismissSucceeded();
                    LogUtil.e("解锁成功！");
                }

                @Override
                public void onDismissCancelled() {
                    LogUtil.e("解锁取消！");
                    super.onDismissCancelled();
                }
            });
        }
    }

    public void toString(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)

        LogUtil.e("screenWidth:" + screenWidth + "  screenHeight:" + screenHeight);
    }

    /**
     * 适配全面屏，针对全面屏有黑边的问题
     *
     * @param activity 适配的页面
     */
    public void adapterFullScreen(Activity activity) {
        //解决android 9.0水滴屏/刘海屏有黑边的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
        }
    }
}
