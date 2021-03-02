package android.helper.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import android.helper.app.CommonConstant;

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
    public static int getStatusBarHeight(Context context) {
        //获取status_bar_height资源的ID
        int statusBarHeight = 0;
        
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (statusBarHeight > 0) {
            // 把状态栏的高度存入到sp中
            SpUtil.putInt(CommonConstant.KEY_STATUS_BAR_HEIGHT, statusBarHeight);
            LogUtil.e("获取状态栏的高度为：" + statusBarHeight);
        } else {
            LogUtil.e("拿不到状态栏的高度，走的是默认25dp的方法");
            float v1 = ConvertUtil.toDp(25);
            // 四舍五入取整数
            int round = Math.round(v1);
            SpUtil.putInt(CommonConstant.KEY_STATUS_BAR_HEIGHT, round);
        }
        return statusBarHeight;
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
    
}
