package com.android.helper.utils.dialog;

import android.annotation.SuppressLint;
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

import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.interfaces.listener.DialogChangeListener;
import com.android.helper.utils.TextViewUtil;

/**
 * PopupWidow 的工具类
 * <p>
 * 使用的时候，要在activity 或者 fragment 中调用 getLifecycle.addObserver() 把自身
 * 绑定页面的生命周期，在页面关闭的时候，就会自动关闭弹窗，避免出现找不到window的崩溃
 * 错误了
 */

public class PopupWindowUtil implements BaseLifecycleObserver {

    @SuppressLint("StaticFieldLeak")
    private PopupWindow mPopupWindow;
    private Builder mBuilder;

    private PopupWindowUtil(Builder builder) {
        this.mBuilder = builder;
        // 构建popupWindow
        if (builder != null) {
            initPopupWindow(builder);
        }
    }

    /**
     * @author : 流星
     * @CreateDate: 2021/9/28
     * @Description: 构建popupWindow
     */
    private void initPopupWindow(Builder builder) {

        // 释放掉原来的pop
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }

        mPopupWindow = new PopupWindow(builder.mWidth, builder.mHeight);

        if (builder.mActivity != null) {
            //解决android 9.0水滴屏/刘海屏有黑边的问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Window window = builder.mActivity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(attributes);
            }

            // 添加管理
            Lifecycle lifecycle = builder.mActivity.getLifecycle();
            lifecycle.addObserver(this);
        }

        // 设置布局
        if (builder.mLayout != null) {
            mPopupWindow.setContentView(builder.mLayout);
        }

        //: pop背景（如果不设置就不会点击消失）
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //: 设置可以点击pop以外的区域
        mPopupWindow.setOutsideTouchable(builder.mTouchable);

        //: 设置PopupWindow可获得焦点
        mPopupWindow.setFocusable(true);

        //: 设置PopupWindow可触摸
        mPopupWindow.setTouchable(true);

        //: 设置超出屏幕显示
        mPopupWindow.setClippingEnabled(builder.mClippingEnabled);

        // 关闭布局的view
        if (builder.mCloseView != null) {
            builder.mCloseView.setOnClickListener(v -> dismiss());
        }

        // 关闭的监听
        mPopupWindow.setOnDismissListener(() -> {
            if (builder.mAlpha > 0) {
                builder.closeAlpha();
            }
            if (builder.mDialogChangeListener != null) {
                builder.mDialogChangeListener.onDismiss();
            }
        });
    }

    /**
     * @param view 用来获取token的view，随意view都可以
     * @return 相对于整个窗口的显示，默认显示在窗口的正中间
     */
    public PopupWindowUtil showAtLocation(View view) {
        showAtLocation(view, 0, 0);
        return this;
    }

    /**
     * @param view 用来获取token的view，随意view都可以
     * @return 相对于整个窗口的显示，并指定偏移
     */
    public PopupWindowUtil showAtLocation(View view, int xoff, int yoff) {
        if ((mBuilder != null) && (mBuilder.mActivity != null) && (view != null)) {
            if ((!mPopupWindow.isShowing())) {
                view.post(() -> {
                    if (mBuilder.mAlpha > 0) {
                        mBuilder.openAlpha();
                    }
                    mPopupWindow.showAtLocation(view, mBuilder.mGravity, xoff, yoff);
                    if (mBuilder.mDialogChangeListener != null) {
                        mBuilder.mDialogChangeListener.onShow(mBuilder.mLayout);
                    }
                });
            }
        }
        return this;
    }

    /**
     * @author : 流星
     * @CreateDate: 2021/9/28
     * @Description: 从view的左下方弹出，并指定偏移，是相对于某个控件显示
     */
    public PopupWindowUtil showAsDropDown(View anchor, int xoff, int yoff) {
        if (anchor != null) {
            anchor.post(() -> {
                if (mPopupWindow != null) {
                    if (mBuilder.mAlpha > 0) {
                        mBuilder.openAlpha();
                    }
                    mPopupWindow.showAsDropDown(anchor, xoff, yoff);

                    if (mBuilder.mDialogChangeListener != null) {
                        mBuilder.mDialogChangeListener.onShow(mBuilder.mLayout);
                    }
                }
            });
        }
        return this;
    }

    /**
     * @author : 流星
     * @CreateDate: 2021/9/28
     * @Description: 从view的左下方弹出，是相对于某个控件显示
     */
    public PopupWindowUtil showAsDropDown(View anchor) {
        this.showAsDropDown(anchor, 0, 0);
        return this;
    }

    public boolean isShowing() {
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }
        return false;
    }

    /**
     * @param id      控件的id
     * @param content 现实的内容
     * @return 设置textView的内容
     */
    public PopupWindowUtil setText(@IdRes int id, String content) {
        if ((mBuilder != null) && (mBuilder.mLayout != null)) {
            View view = mBuilder.mLayout.findViewById(id);
            if (view instanceof TextView) {
                TextViewUtil.setText((TextView) view, content);
            }
        }
        return this;
    }

    /**
     * @param textView 控件的view
     * @param content  显示的内容
     * @return 设置textView的内容
     */
    public PopupWindowUtil setText(TextView textView, String content) {
        TextViewUtil.setText(textView, content);
        return this;
    }

    /**
     * @return 获取当前的popupWindow
     */
    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public static class Builder {
        private final FragmentActivity mActivity; // 上下文对象
        private View mLayout; // 布局
        private View mCloseView; // 关闭布局的view
        private DialogChangeListener mDialogChangeListener;

        private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;   // 默认的宽高
        private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        private boolean mTouchable = true; // 触摸是否可以取消，默认可以
        private boolean mClippingEnabled = false; // 是否可以超出屏幕显示，默认false:表示可以遮罩
        private int mGravity = Gravity.CENTER;// 默认居中显示
        private float mAlpha;// 透明度

        public Builder(FragmentActivity activity, View layout) {
            mActivity = activity;
            mLayout = layout;
        }

        public Builder(FragmentActivity activity, @LayoutRes int resource) {
            mActivity = activity;
            if (resource != 0) {
                View inflate = LayoutInflater.from(activity).inflate(resource, null, false);
                if (inflate != null) {
                    mLayout = inflate;
                }
            }
        }

        /**
         * @author : 流星
         * @CreateDate: 2021/9/28
         * @Description: 设置宽度，一般可以使用{@link ViewGroup.LayoutParams#MATCH_PARENT} or {@link  ViewGroup.LayoutParams#WRAP_CONTENT}
         */
        public Builder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        /**
         * @author : 流星
         * @CreateDate: 2021/9/28
         * @Description: 设置高度，一般可以使用{@link ViewGroup.LayoutParams#MATCH_PARENT} or {@link  ViewGroup.LayoutParams#WRAP_CONTENT}
         */
        public Builder setHeight(int height) {
            this.mHeight = height;
            return this;
        }

        /**
         * @param gravity Gravity.CENTER ...
         * @return 设置位置，需要在setContentView()方法之前设置，否则不生效
         */
        public Builder setGravity(int gravity) {
            this.mGravity = gravity;
            return this;
        }

        /**
         * @param touchable 是否消失
         * @return 点击popupWindow 外部的区域是否消失
         */
        public Builder setOutsideTouchable(boolean touchable) {
            this.mTouchable = touchable;
            return this;
        }

        /**
         * @return 是否可以超出屏幕显示，false :可以，true:不可以，默认不可以
         */
        public Builder setClippingEnabled(boolean clippingEnabled) {
            this.mClippingEnabled = clippingEnabled;
            return this;
        }

        /**
         * @param id   控件的id
         * @param text 现实的内容
         * @return 设置textView的内容
         */
        public Builder setText(@IdRes int id, String text) {
            if (mLayout != null) {
                View view = mLayout.findViewById(id);
                if (view instanceof TextView) {
                    TextViewUtil.setText((TextView) view, text);
                }
            }
            return this;
        }

        /**
         * @param textView 控件的id
         * @param text     设置的内容
         * @return 设置textView的内容
         */
        public Builder setText(TextView textView, String text) {
            TextViewUtil.setText(textView, text);
            return this;
        }

        /**
         * @return 设置view的点击事件
         */
        public Builder setViewClickListener(@IdRes int id, View.OnClickListener listener) {
            if (mLayout != null) {
                View view = mLayout.findViewById(id);
                if (view != null && listener != null) {
                    view.setOnClickListener(listener);
                }
            }
            return this;
        }

        /**
         * @return 设置view的点击事件
         */
        public Builder setViewClickListener(View view, View.OnClickListener listener) {
            if (view != null && listener != null) {
                view.setOnClickListener(listener);
            }
            return this;
        }

        /**
         * @return 设置textView的点击事件
         */
        public Builder setTextViewClickListener(TextView view, String content, View.OnClickListener listener) {
            if (view != null && listener != null) {
                // 设置内容
                TextViewUtil.setText(view, content);
                // 设置点击事件
                view.setOnClickListener(listener);
            }
            return this;
        }

        /**
         * @return 设置textView的点击事件
         */
        public Builder setTextViewClickListener(@IdRes int id, String content, View.OnClickListener listener) {
            if ((mLayout != null) && (id != 0) && (listener != null)) {
                View viewById = mLayout.findViewById(id);
                if (viewById instanceof TextView) {
                    TextView textView = (TextView) viewById;
                    // 设置内容
                    TextViewUtil.setText(textView, content);
                    // 设置点击事件
                    textView.setOnClickListener(listener);
                }
            }
            return this;
        }

        /**
         * 窗口打开和关闭的监听
         */
        public Builder setPopupWindowChangeListener(DialogChangeListener dialogChangeListener) {
            this.mDialogChangeListener = dialogChangeListener;
            return this;
        }

        /**
         * @param id 指定的id
         * @return 点击指定id的时候，关闭弹窗
         */
        public Builder setCloseView(@IdRes int id) {
            if (mLayout != null) {
                View view = mLayout.findViewById(id);
                if (view != null) {
                    mCloseView = view;
                }
            }
            return this;
        }

        /**
         * @param view 指定的view
         * @return 点击指定id的时候，关闭弹窗
         */
        public Builder setClose(View view) {
            if (view != null) {
                mCloseView = view;
            }
            return this;
        }

        public Builder setAlpha(@FloatRange(from = 0.0f, to = 1.0f) float alpha) {
            this.mAlpha = alpha;
            return this;
        }

        private void openAlpha() {
            if (mActivity != null) {
                Window window = mActivity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.alpha = mAlpha;
                window.setAttributes(attributes);
            }
        }

        private void closeAlpha() {
            if (mActivity != null) {
                Window window = mActivity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.alpha = 1.0f;
                window.setAttributes(attributes);
            }
        }

        public PopupWindowUtil Build() {
            return new PopupWindowUtil(this);
        }
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
        if (isShowing()) {
            dismiss();
        }
        if (mPopupWindow != null) {
            mPopupWindow = null;
        }

        if (mBuilder != null) {
            mBuilder = null;
        }
    }
}
