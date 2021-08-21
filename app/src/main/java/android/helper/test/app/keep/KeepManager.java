package android.helper.test.app.keep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.helper.utils.LogUtil;

import java.lang.ref.WeakReference;

/**
 * 一个像素activity的管理器
 */
public class KeepManager {

    private android.helper.test.app.keep.keepReceiver keepReceiver;
    private WeakReference<Activity> weakReference;
    private static KeepManager keepManager;

    public static KeepManager getInstance() {
        if (keepManager == null) {
            keepManager = new KeepManager();
        }
        return keepManager;
    }

    public void registerKeep(Context context) {
        if (context != null) {
            try {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_SCREEN_ON);
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
                keepReceiver = new keepReceiver();
                context.registerReceiver(keepReceiver, intentFilter);
            } catch (Exception e) {
                LogUtil.e("注册一个像素的activity异常！" + e.getMessage());
            }
        }
    }

    /**
     * 解除一个像素的页面注册
     */
    public void unregisterKeep(Context context) {
        try {
            if ((context != null) && (keepReceiver != null)) {
                context.unregisterReceiver(keepReceiver);
            }
        } catch (Exception e) {
            LogUtil.e("注销一个像素的activity异常！" + e.getMessage());
        }
    }

    /**
     * 打开一个像素的activity的页面
     */
    public void startKeep(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, KeepActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent);
        }
    }

    /**
     * 结束一个像素的activity的页面
     */
    public void finishKeep() {
        if (weakReference != null) {
            Activity activity = weakReference.get();
            if (activity != null) {
                activity.finish();
            }
            weakReference = null;
        }
    }

    /**
     * 添加一个activity到堆栈中去
     */
    public void setKeep(KeepActivity keep) {
        if (keep != null) {
            weakReference = new WeakReference<>(keep);
        }
    }

}
