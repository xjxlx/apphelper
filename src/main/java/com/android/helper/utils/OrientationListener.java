package com.android.helper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.view.OrientationEventListener;

import androidx.fragment.app.FragmentActivity;

import com.android.common.utils.LogUtil;
import com.android.helper.interfaces.TagListener;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;

/**
 * 屏幕旋转的工具类
 */
public class OrientationListener extends OrientationEventListener implements TagListener, BaseLifecycleObserver {

    private final Object mObj;
    private Activity mContext;

    /**
     * @param context 上下文，必须是activity或者fragment的对象
     * @param obj     当前类的对象
     */
    public OrientationListener(Context context, Object obj) {
        super(context);
        this.mObj = obj;
        if (context instanceof FragmentActivity) {
            mContext = (FragmentActivity) context;
        }
    }

    @Override
    public void onOrientationChanged(int orientation) {
        LogUtil.e(getTag(), "orientation" + orientation);
        if (mContext != null) {
            int screenOrientation = mContext.getResources().getConfiguration().orientation;
            if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    LogUtil.e(getTag(), "设置竖屏");
                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } else if (orientation > 225 && orientation < 315) { //设置横屏
                LogUtil.e(getTag(), "设置横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
                LogUtil.e(getTag(), "反向横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            } else if (orientation > 135 && orientation < 225) {
                LogUtil.e(getTag(), "反向竖屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
            }
        }

    }

    @Override
    public String getTag() {
        return ClassUtil.getClassName(mObj);
    }

    @Override
    public void onCreate() {
        if (mContext != null) {
            // 判断当前设备是否开启了自动旋转的功能
            int enableValue = Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            // 1:开启了自动旋转的功能  0：没有开启自动旋转的功能
            boolean autoRotateOn = (enableValue == 1);
            LogUtil.e(getTag(), "是否开启了自动旋转的功能:" + autoRotateOn);
            //检查系统是否开启自动旋转
            if (autoRotateOn) {
                // 当设备方向改变的时候，监听方向的传感器数据的调用
                enable();
            }
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroy() {
        // 在activity销毁的时候，取消监听
        disable();
        LogUtil.e(getTag(), "取消了屏幕旋转的监听！");
    }
}
