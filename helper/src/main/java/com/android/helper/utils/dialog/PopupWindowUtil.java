package com.android.helper.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.android.helper.interfaces.listener.DialogChangeListener;
import com.android.helper.utils.ActivityUtil;
import com.android.helper.utils.TextViewUtil;

/**
 * PopupWidow 的工具类
 */
public class PopupWindowUtil {

    @SuppressLint("StaticFieldLeak")
    private static PopupWindowUtil windowUtil;
    private PopupWindow mPopupWindow;
    private Activity mActivity;
    private DialogChangeListener mDialogChangeListener;
    private View mLayout;

    private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
    private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;

    public static PopupWindowUtil getInstance(Activity activity) {
        if (windowUtil == null) {
            windowUtil = new PopupWindowUtil(activity);
        }
        return windowUtil;
    }

    private PopupWindowUtil(Activity activity) {
        mActivity = activity;

        // 释放掉原来的pop
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }

        mPopupWindow = new PopupWindow();

        //:pop背景（如果不设置就不会点击消失）
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //: 设置PopupWindow可触摸
        mPopupWindow.setOutsideTouchable(true);
        //:设置PopupWindow可获得焦点
        mPopupWindow.setFocusable(true);
        //: 设置点击pop以外的区域会消失
        mPopupWindow.setTouchable(true);

        //解决android 9.0水滴屏/刘海屏有黑边的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
        }

        mPopupWindow.setOnDismissListener(() -> {
            if (mDialogChangeListener != null) {
                mDialogChangeListener.onDismiss();
            }
        });
    }

    public PopupWindowUtil setContentView(int layout, View.OnClickListener listener) {
        if (mActivity != null) {
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
        if (layout != null) {
            mLayout = layout;
            mPopupWindow.setContentView(mLayout);
            if (listener != null) {
                listener.onClick(mLayout);
            }
        }
        return windowUtil;
    }

    public PopupWindowUtil setWidth(int width) {
        mWidth = width;
        if (mPopupWindow != null) {
            mPopupWindow.setWidth(mWidth);
        }
        return windowUtil;
    }

    public PopupWindowUtil setHeight(int height) {
        mHeight = height;
        if (mPopupWindow != null) {
            mPopupWindow.setHeight(mHeight);
        }
        return windowUtil;
    }

    public void showAtLocation(View view) {
        boolean destroy = ActivityUtil.isDestroy(mActivity);
        if ((!destroy) && (!mPopupWindow.isShowing())) {
            mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            if (mDialogChangeListener != null) {
                mDialogChangeListener.onShow(mLayout);
            }
        }
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (mPopupWindow != null) {
            boolean destroy = ActivityUtil.isDestroy(mActivity);
            if (destroy) {
                mPopupWindow.showAsDropDown(anchor, xoff, yoff);
            }
        }
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
    public void setPopupWindowChangeListener(DialogChangeListener dialogChangeListener) {
        this.mDialogChangeListener = dialogChangeListener;
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

}
