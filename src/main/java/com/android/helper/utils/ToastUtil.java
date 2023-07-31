package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.utils.LogUtil;
import com.android.helper.R;
import com.android.helper.app.BaseApplication;

@SuppressLint("StaticFieldLeak")
public class ToastUtil {

    private static final String TAG = "ToastUtil";
    private static Toast toast;
    private static int yOffset;
    private static Application context;
    private static View view;

    /**
     * 私有化构造
     */
    private ToastUtil() {
    }

    /**
     * 强大的吐司，能够连续弹的吐司,默认弹出在屏幕底部5分之一的位置
     *
     * @param text 内容
     */
    public static void show(String text) {
        if (context == null) {
            context = BaseApplication
                    .getInstance()
                    .getApplication();
        }

        if (yOffset <= 0) {
            int screenHeight = ScreenUtil.getScreenHeight(context);
            yOffset = screenHeight / 5;
        }
        show(text, Toast.LENGTH_SHORT, Gravity.BOTTOM, 0, yOffset);
    }

    /**
     * @param text     内容
     * @param duration 时间
     * @param gravity  位置
     * @param xOffset  默认居中
     * @param yOffset  偏移，如果为-1的话，就使用默认屏幕5分之一的高度
     */
    @SuppressLint("InflateParams")
    public static void show(String text, int duration, int gravity, int xOffset, int yOffset) {

        if (context == null) {
            context = BaseApplication
                    .getInstance()
                    .getApplication();
        }

        if (context == null) {
            LogUtil.e(TAG, "context为空！");
            return;
        }

        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (toast != null) {
            toast.cancel();
        }

        toast = new Toast(context);
        if (view == null) {
            view = LayoutInflater
                    .from(context)
                    .inflate(R.layout.widget_toast, null);
        }
        TextView textView = view.findViewById(R.id.message);
        // 4:设置布局的内容
        textView.setText(text);
        if (isPadding){
            textView.setPadding(mLeft, mTop, mRight, mBottom);
        }

        if (mTextSize!=0){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        }

        if (mIsBOLD) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }

        // 5:设置Toast的参数
        toast.setGravity(gravity, xOffset, yOffset);
        toast.setView(view);

        toast.setDuration(duration);
        toast.show();
    }

    private static int mLeft = 0;
    private static int mTop = 0;
    private static int mRight = 0;
    private static int mBottom = 0;
    private static int mTextSize = 0;
    private static boolean mIsBOLD = false;
    private static boolean isPadding =false;

    public static void setPadding(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        isPadding = true;
    }

    public static void setTextSize(int size) {
        mTextSize = size;
    }

    public static void isBOLD(boolean isBOLD) {
        mIsBOLD = isBOLD;
    }

}
