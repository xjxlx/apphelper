package com.android.helper.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.android.helper.interfaces.listener.DialogChangeListener;
import com.android.helper.utils.ActivityUtil;
import com.android.helper.utils.TextViewUtil;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;

/**
 * PopupWidow 的工具类
 * <p>
 * 使用的时候，要在activity 或者 fragment 中调用 getLifecycle.addObserver() 把自身
 * 绑定页面的生命周期，在页面关闭的时候，就会自动关闭弹窗，避免出现找不到window的崩溃
 * 错误了
 */

public class PopupWindowUtil implements BaseLifecycleObserver {

    @SuppressLint("StaticFieldLeak")
    private static PopupWindowUtil windowUtil;
    private PopupWindow mPopupWindow;
    private final Activity mActivity;
    private DialogChangeListener mDialogChangeListener;
    private View mLayout;

    private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;   // 默认的宽高
    private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private boolean mTouchable = true; // 触摸是否可以取消，默认可以
    private boolean mClippingEnabled = true; // 是否可以超出屏幕显示，模式不可以

    public static PopupWindowUtil getInstance(Activity activity) {
        if (windowUtil == null) {
            windowUtil = new PopupWindowUtil(activity);
        }
        return windowUtil;
    }

    private PopupWindowUtil(Activity activity) {
        mActivity = activity;
    }

    private void initPopupWindow() {

        // 释放掉原来的pop
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }

        mPopupWindow = new PopupWindow(mWidth, mHeight);

        //: pop背景（如果不设置就不会点击消失）
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //: 设置可以点击pop以外的区域
        mPopupWindow.setOutsideTouchable(mTouchable);

        //: 设置PopupWindow可获得焦点
        mPopupWindow.setFocusable(mTouchable);

        //: 设置PopupWindow可触摸
        mPopupWindow.setTouchable(mTouchable);

        //: 设置超出屏幕显示
        mPopupWindow.setClippingEnabled(mClippingEnabled);

        //解决android 9.0水滴屏/刘海屏有黑边的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Window window = mActivity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(attributes);
        }

        mPopupWindow.setOnDismissListener(() -> {
            if (mDialogChangeListener != null) {
                mDialogChangeListener.onDismiss();
            }
        });

    }

    public PopupWindowUtil setContentView(int layout, View.OnClickListener listener) {
        if (mActivity != null) {
            initPopupWindow();
            mLayout = LayoutInflater.from(mActivity).inflate(layout, null);
            if (mLayout != null) {
                mPopupWindow.setContentView(mLayout);
                if (listener != null) {
                    listener.onClick(mLayout);
                }
            }
        }
        return windowUtil;
    }

    public PopupWindowUtil setContentView(View layout, View.OnClickListener listener) {
        if (mActivity != null) {
            if (layout != null) {
                initPopupWindow();
                mLayout = layout;
                mPopupWindow.setContentView(mLayout);
                if (listener != null) {
                    listener.onClick(mLayout);
                }
            }
        }
        return windowUtil;
    }

    public PopupWindowUtil setWidth(int width) {
        mWidth = width;
        if (mPopupWindow != null) {
            mPopupWindow.setWidth(width);
        }
        return windowUtil;
    }

    public PopupWindowUtil setHeight(int height) {
        mHeight = height;
        if (mPopupWindow != null) {
            mPopupWindow.setHeight(height);
        }
        return windowUtil;
    }

    public PopupWindowUtil show(Activity activity, View view) {
        if (view != null) {
            view.post(() -> {
                boolean destroy = ActivityUtil.isDestroy(activity);
                if ((!destroy) && (!mPopupWindow.isShowing())) {
                    mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    if (mDialogChangeListener != null) {
                        mDialogChangeListener.onShow(mLayout);
                    }
                }
            });
        }
        return windowUtil;
    }

    public boolean isShowing() {
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }
        return false;
    }

    public PopupWindowUtil show(Activity activity, View anchor, int xoff, int yoff) {
        if (anchor != null) {
            anchor.post(() -> {
                if (mPopupWindow != null) {
                    boolean destroy = ActivityUtil.isDestroy(activity);
                    if (destroy) {
                        mPopupWindow.showAsDropDown(anchor, xoff, yoff);
                        if (mDialogChangeListener != null) {
                            mDialogChangeListener.onShow(mLayout);
                        }
                    }
                }
            });
        }
        return windowUtil;
    }

    /**
     * @param id   控件的id
     * @param text 现实的内容
     * @return 设置textView的内容
     */
    public PopupWindowUtil setText(@IdRes int id, String text) {
        if (mLayout != null) {
            View view = mLayout.findViewById(id);
            if (view instanceof TextView) {
                TextViewUtil.setText((TextView) view, text);
            }
        }
        return this;
    }

    /**
     * @param touchable 是否消失
     * @return 点击popupWindow 外部的区域是否消失
     */
    public PopupWindowUtil setOutsideTouchable(boolean touchable) {
        this.mTouchable = touchable;
        return this;
    }

    /**
     * @return 是否可以超出屏幕显示，false :可以，true:不可以，默认不可以
     */
    public PopupWindowUtil setClippingEnabled(boolean clippingEnabled) {
        this.mClippingEnabled = clippingEnabled;
        return this;
    }

    /**
     * @return 获取当前的popupWindow
     */
    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    /**
     * @return 设置view的点击事件
     */
    public PopupWindowUtil setViewClickListener(int id, View.OnClickListener listener) {
        if (mLayout != null) {
            View view = mLayout.findViewById(id);
            if (view != null && listener != null) {
                view.setOnClickListener(listener);
            }
        }
        return this;
    }

    /**
     * 窗口打开和关闭的监听
     */
    public PopupWindowUtil setPopupWindowChangeListener(DialogChangeListener dialogChangeListener) {
        this.mDialogChangeListener = dialogChangeListener;
        return this;
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onCreate() {

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
        // 手动关闭弹窗，避免崩溃
        dismiss();
    }
}
