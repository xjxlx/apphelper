package com.android.helper.utils.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    private static PopupWindowUtil windowUtil;
    private PopupWindow mPopupWindow;
    private Activity activity;
    private DialogChangeListener mDialogChangeListener;
    private View mLayout;

    private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
    private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;

    public static PopupWindowUtil getInstance() {
        if (windowUtil == null) {
            windowUtil = new PopupWindowUtil();
        }
        return windowUtil;
    }

    private PopupWindowUtil() {
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

        mPopupWindow.setOnDismissListener(() -> {
            if (mDialogChangeListener != null) {
                mDialogChangeListener.onDismiss();
            }
        });
    }

    public PopupWindowUtil setContentView(int layout) {
        mLayout = LayoutInflater.from(activity).inflate(layout, null);
        if (mLayout != null) {
            mPopupWindow.setContentView(mLayout);
        }
        return windowUtil;
    }

    public PopupWindowUtil setContentView(View layout) {
        if (layout != null) {
            mLayout = layout;
            mPopupWindow.setContentView(mLayout);
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
        if ((!activity.isFinishing()) && (!mPopupWindow.isShowing())) {
            mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            if (mDialogChangeListener != null) {
                mDialogChangeListener.onShow(mLayout);
            }
        }
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (mPopupWindow != null) {
            boolean destroy = ActivityUtil.isDestroy(activity);
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

}
