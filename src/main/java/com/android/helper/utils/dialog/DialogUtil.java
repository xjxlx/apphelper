package com.android.helper.utils.dialog;

import android.app.Dialog;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.R;
import com.android.helper.common.CommonConstants;
import com.android.helper.common.EventMessage;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Dialog的工具类
 * 使用的时候，要在activity 或者 fragment 中调用 getLifecycle.addObserver() 把自身
 * 绑定页面的生命周期，在页面关闭的时候，就会自动关闭弹窗，避免出现找不到window的崩溃
 * 错误了
 */
public class DialogUtil implements BaseLifecycleObserver {

    private Dialog mDialog;
    private final Builder mBuilder;

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
        // 在页面不可见的时候，自动关闭dialog
        release();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DialogType.DEFAULT_DIALOG, DialogType.HINT_DIALOG})
    public @interface DialogType {
        int DEFAULT_DIALOG = 1;
        int HINT_DIALOG = 2;
    }

    private DialogUtil(Builder builder) {
        this.mBuilder = builder;
        // 构造dialog
        initDialog(builder);
    }

    /**
     * 构建dialog 对象
     *
     * @param builder builder的对象
     */
    private void initDialog(Builder builder) {
        if (builder != null) {
            if ((builder.mActivity != null) && (!builder.mActivity.isFinishing()) && (!builder.mActivity.isDestroyed())) {

                // 添加生命周期的控制
                Lifecycle lifecycle = builder.mActivity.getLifecycle();
                lifecycle.addObserver(this);

                // 避免重复出现弹窗
                if (mDialog != null) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }

                // 设置dialog的方式
                if (builder.mDialogType == DialogType.DEFAULT_DIALOG) {
                    mDialog = new Dialog(builder.mActivity, R.style.base_dialog_default);
                } else if (builder.mDialogType == DialogType.HINT_DIALOG) {
                    mDialog = new Dialog(builder.mActivity, R.style.base_dialog_hint);
                }

                // 设置布局
                if ((builder.mLayoutView != null) && (mDialog != null)) {

                    // 按下返回键是否可以取消dialog
                    mDialog.setCancelable(builder.mCancelable);

                    // dialog点击区域外的时候，是否可以取消dialog
                    mDialog.setCanceledOnTouchOutside(builder.mCanceledOnTouchOutside);

                    // 设置布局
                    mDialog.setContentView(builder.mLayoutView);

                    // 点击关闭dialog
                    builder.mCloseView.setOnClickListener(v -> dismiss());

                    // 设置属性
                    setWindowAttributes(builder);
                }
            }
        }
    }

    public void show() {
        if ((mBuilder.mActivity != null) && (!mBuilder.mActivity.isFinishing()) && (!mBuilder.mActivity.isDestroyed()) && (mDialog != null) && (!mDialog.isShowing())) {
            mDialog.show();
        } else {
            try {
                if (mBuilder.mActivity != null) {
                    LogUtil.e("dialog打开失败：activity:" + mBuilder.mActivity + " isFinishing:" + (mBuilder.mActivity.isFinishing()) + " dialog:" + mDialog + " isShowing:" + (mDialog.isShowing()));
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * @param text 内容
     * @return 设置title内容，title的id必须是：R.id.tv_title
     */
    public DialogUtil setText(@IdRes int id, String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mBuilder != null && mBuilder.mLayoutView != null) {
                View view = mBuilder.mLayoutView.findViewById(id);
                if (view != null) {
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        textView.setText(text);
                    }
                }
            }
        }
        return this;
    }

    /**
     * @param text 内容
     * @return 设置title内容，title的id必须是：R.id.tv_title
     */
    public DialogUtil setText(TextView textView, String text) {
        if (!TextUtils.isEmpty(text) && textView != null) {
            textView.setText(text);
        }
        return this;
    }

    /**
     * @param id  指定的id
     * @param <T> 指定的类型
     * @return 返回一个对象
     */
    public <T extends View> T getView(@IdRes int id) {
        if ((mBuilder != null) && (mBuilder.mLayoutView != null)) {
            View view = mBuilder.mLayoutView.findViewById(id);
            if (view != null) {
                return (T) view;
            }
        }
        return null;
    }

    public static class Builder {
        private int mAnimation = R.style.base_dialog_animation;// 动画
        private int mDialogType = DialogType.DEFAULT_DIALOG;

        private View mLayoutView;       // dialog的布局
        private FragmentActivity mActivity;     // dialog依赖的activity对象
        private View mCloseView; // 关闭dialog的对象
        private int mGravity = Gravity.CENTER;// 默认居中显示
        private boolean mCanceledOnTouchOutside = true; // 点击dialog外界是否可以取消dialog ，默认可以
        private boolean mCancelable = true;// 按下返回键的时候，是否可以取消dialog,默认可以
        private int mWidth = WindowManager.LayoutParams.MATCH_PARENT; // 宽
        private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT; // 高

        /**
         * @param activity    依赖的activity
         * @param contentView 布局的view
         * @author : 流星
         * @CreateDate: 2021/9/27
         * @Description: 构造Dialog的参数
         */
        public Builder(FragmentActivity activity, View contentView) {
            if (activity != null) {
                mActivity = activity;
            }
            if (contentView != null) {
                mLayoutView = contentView;
            }
        }

        /**
         * @param activity 依赖的activity
         * @param resource 布局的资源文件
         * @author : 流星
         * @CreateDate: 2021/9/27
         * @Description: 构造Dialog的参数
         */
        public Builder(FragmentActivity activity, @LayoutRes int resource) {
            if (activity != null) {
                mActivity = activity;
            }
            if (resource != 0) {
                View inflate = LayoutInflater.from(mActivity).inflate(resource, null, false);
                if (inflate != null) {
                    mLayoutView = inflate;
                }
            }
        }

        /**
         * @param id 指定的id
         * @return 点击指定id的时候，关闭弹窗
         */
        public Builder setClose(@IdRes int id) {
            if (mLayoutView != null) {
                View view = this.mLayoutView.findViewById(id);
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

        /**
         * @param gravity Gravity.CENTER ...
         * @return 设置位置，需要在setContentView()方法之前设置，否则不生效
         */
        public Builder setGravity(int gravity) {
            this.mGravity = gravity;
            return this;
        }

        /**
         * @param animation 设置动画
         * @return 设置动画，需要在setContentView()方法之前设置，否则不生效，一般使用{R.style.base_dialog_animation}
         */
        public Builder setAnimation(int animation) {
            this.mAnimation = animation;
            return this;
        }

        /**
         * @param cancel false时为点击周围空白处弹出层不自动消失
         * @return 弹窗点击周围空白处弹出层自动消失弹窗消失(false时为点击周围空白处弹出层不自动消失)
         */
        public Builder setCanceledOnTouchOutside(boolean cancel) {
            this.mCanceledOnTouchOutside = cancel;
            return this;
        }

        /**
         * @param flag true：可以取消，false ：不可以取消
         * @return 设置点击返回键的时候，是否可以取消， true：可以取消，false ：不可以取消
         */
        public Builder setCancelable(boolean flag) {
            mCancelable = flag;
            return this;
        }

        /**
         * @param width 设置宽度
         * @return 设置宽度，需要在setContentView()方法之前设置，否则不生效
         */
        public Builder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        /**
         * @param mHeight 设置高度
         * @return 设置高度，需要在setContentView()方法之前设置，否则不生效
         */
        public Builder setHeight(int mHeight) {
            this.mHeight = mHeight;
            return this;
        }

        /**
         * @param type: dialog的类型，默认是带遮罩层的弹窗，使用的时候，只接收两个参数{@link DialogType#DEFAULT_DIALOG} 或者{@link DialogType#HINT_DIALOG}
         */
        public Builder getDialogType(int type) {
            if ((type == DialogType.DEFAULT_DIALOG) || (type == DialogType.HINT_DIALOG)) {
                mDialogType = type;
            }
            return this;
        }

        /**
         * @param id       指定view的id
         * @param listener 点击事件
         * @return 响应view的点击事件
         */
        public Builder setOnClickListener(@IdRes int id, View.OnClickListener listener) {
            if (mLayoutView != null) {
                View view = mLayoutView.findViewById(id);
                if (view != null && listener != null) {
                    view.setOnClickListener(listener);
                }
            }
            return this;
        }

        /**
         * @param view     指定view
         * @param listener 点击事件
         * @return 响应view的点击事件
         */
        public Builder setOnClickListener(View view, View.OnClickListener listener) {
            if (listener != null && view != null) {
                view.setOnClickListener(listener);
            }
            return this;
        }

        /**
         * @param id       textView
         * @param content  标题
         * @param listener 点击事件
         * @return 按钮设置文字，并设置点击事件
         */
        public Builder setOnClickListener(@IdRes int id, String content, View.OnClickListener listener) {
            if (mLayoutView != null) {
                View view = mLayoutView.findViewById(id);
                if (!TextUtils.isEmpty(content)) {
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        textView.setText(content);
                    }
                }
                if (view != null && listener != null) {
                    view.setOnClickListener(listener);
                }
            }
            return this;
        }

        /**
         * @param text 内容
         * @return 设置title内容，title的id必须是：R.id.tv_title
         */
        public Builder setText(@IdRes int id, String text) {
            if (!TextUtils.isEmpty(text)) {
                if (mLayoutView != null) {
                    View view = mLayoutView.findViewById(id);
                    if (view != null) {
                        if (view instanceof TextView) {
                            TextView textView = (TextView) view;
                            textView.setText(text);
                        }
                    }
                }
            }
            return this;
        }

        /**
         * @param text 内容
         * @return 设置title内容，title的id必须是：R.id.tv_title
         */
        public Builder setText(TextView textView, String text) {
            if (!TextUtils.isEmpty(text) && textView != null) {
                textView.setText(text);
            }
            return this;
        }

        public DialogUtil Build() {
            return new DialogUtil(this);
        }
    }

    /**
     * 释放掉dialog，
     */
    public void release() {
        if ((mDialog != null) && (mDialog.isShowing())) {
            dismiss();
            mDialog = null;
        }
    }

    /**
     * 设置dialog的属性
     */
    private void setWindowAttributes(Builder builder) {
        if (mDialog != null && builder != null) {
            Window window = mDialog.getWindow();
            if (window != null) {
                window.setGravity(builder.mGravity);
                WindowManager.LayoutParams attributes = window.getAttributes();
                if (attributes != null) {
                    attributes.width = builder.mWidth;
                    attributes.height = builder.mHeight;
                    if (builder.mAnimation != 0) {
                        attributes.windowAnimations = builder.mAnimation;
                    }

                    //解决android 9.0水滴屏/刘海屏有黑边的问题
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                    }
                }
            }

            // dialog展示时候的监听
            mDialog.setOnShowListener(dialog -> EventBus.getDefault().post(new EventMessage(CommonConstants.CODE_DIALOG_SHOW)));

            // dialog 关闭时候的监听
            mDialog.setOnDismissListener(dialog -> EventBus.getDefault().post(new EventMessage(CommonConstants.CODE_DIALOG_DISMISS)));
        }
    }

}
