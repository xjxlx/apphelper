package com.android.helper.utils.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.common.utils.LogUtil;
import com.android.helper.R;
import com.android.helper.common.CommonConstants;
import com.android.helper.common.EventMessage;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;

/**
 * Dialog的工具类 使用的时候，要在activity 或者 fragment 中调用 getLifecycle.addObserver() 把自身
 * 绑定页面的生命周期，在页面关闭的时候，就会自动关闭弹窗，避免出现找不到window的崩溃 错误了
 */
public class DialogUtil implements BaseLifecycleObserver {

    private Builder mBuilder;
    private int mAnimation;// 动画
    private int mDialogType;

    private View mLayoutView; // dialog的布局
    private FragmentActivity mActivity; // dialog依赖的activity对象
    private Fragment mFragment; // dialog依赖的activity对象
    private HashSet<View> mListCloseView; // 关闭dialog的对象
    private int mGravity;// 默认居中显示
    private boolean mCanceledOnTouchOutside; // 点击dialog外界是否可以取消dialog ，默认可以
    private boolean mCancelable;// 按下返回键的时候，是否可以取消dialog,默认可以
    private int mWidth; // 宽
    private int mHeight; // 高
    private DialogInterface.OnShowListener mShowListener;
    private DialogInterface.OnDismissListener mDismissListener;
    private boolean stopDialog;// 在stop的时候，关闭dialog
    private int mOffsetX; // 偏移的X轴
    private int mOffsetY; // 偏移的Y轴
    private Dialog mDialog;
    private boolean isAutoDismiss;// 在点击按钮的时候，是否自定关闭dialog

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DialogType.DEFAULT_DIALOG, DialogType.HINT_DIALOG})
    public @interface DialogType {
        int DEFAULT_DIALOG = 1;
        int HINT_DIALOG = 2;
    }

    private DialogUtil(Builder builder) {
        if (builder != null) {
            this.mBuilder = builder;
            /*
             * 1：来源于activity，2：来源于fragment
             */
            int typeFrom = mBuilder.mTypeFrom;
            mAnimation = mBuilder.mAnimation;
            mDialogType = mBuilder.mDialogType;
            mLayoutView = mBuilder.mLayoutView;
            mListCloseView = mBuilder.mListCloseView;
            mGravity = mBuilder.mGravity;
            mCanceledOnTouchOutside = mBuilder.mCanceledOnTouchOutside;
            mCancelable = mBuilder.mCancelable;
            mWidth = mBuilder.mWidth;
            mHeight = mBuilder.mHeight;
            stopDialog = mBuilder.stopDialog;
            mOffsetX = mBuilder.mOffsetX;
            mOffsetY = mBuilder.mOffsetY;
            isAutoDismiss = mBuilder.isAutoDismiss;
            mShowListener = mBuilder.mShowListener;
            mDismissListener = mBuilder.mDismissListener;

            // 添加生命周期的控制
            if (typeFrom == 1) {
                mActivity = mBuilder.mActivity;
                if (mActivity != null) {
                    Lifecycle lifecycle = mActivity.getLifecycle();
                    lifecycle.addObserver(this);
                }
            } else if (builder.mTypeFrom == 2) {
                mFragment = builder.mFragment;
                mActivity = mFragment.getActivity();
                if (mFragment != null) {
                    Lifecycle lifecycle = mFragment.getLifecycle();
                    lifecycle.addObserver(this);
                }
            }
            initDialog();
        }
    }

    public DialogUtil show() {
        if (mActivity != null && mDialog != null) {
            if ((!mActivity.isFinishing()) && (!mActivity.isDestroyed()) && (mDialog != null) && (!mDialog.isShowing())) {
                mDialog.show();
            } else {
                LogUtil.e("dialog打开失败：activity:" + mActivity + " isFinishing:" + (mActivity.isFinishing()) + " dialog:" + mDialog + " isShowing:" + (mDialog.isShowing()));
            }
        }
        return this;
    }

    /**
     * 取消当前的dialog
     */
    public void cancel() {
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    /**
     * 关闭弹窗
     */
    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * @param id  指定的id
     * @param <T> 指定的类型
     * @return 返回一个对象
     */
    public <T extends View> T getView(@IdRes int id) {
        if (mLayoutView != null) {
            View view = mLayoutView.findViewById(id);
            if (view != null) {
                return (T) view;
            }
        }
        return null;
    }

    /**
     * @return 获取当前的dialog
     */
    public Dialog getDialog() {
        return mDialog;
    }

    public static class Builder {
        private int mAnimation = R.style.base_dialog_animation;// 动画
        private int mDialogType = DialogType.DEFAULT_DIALOG;
        private View mLayoutView; // dialog的布局
        private FragmentActivity mActivity; // dialog依赖的activity对象
        private Fragment mFragment; // dialog依赖的activity对象
        private HashSet<View> mListCloseView; // 关闭dialog的对象
        private int mGravity = Gravity.CENTER;// 默认居中显示
        private boolean mCanceledOnTouchOutside = true; // 点击dialog外界是否可以取消dialog ，默认可以
        private boolean mCancelable = true;// 按下返回键的时候，是否可以取消dialog,默认可以
        private int mWidth = WindowManager.LayoutParams.MATCH_PARENT; // 宽
        private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT; // 高
        private boolean stopDialog;// 在stop的时候，关闭dialog
        private int mOffsetX; // 偏移的X轴
        private int mOffsetY; // 偏移的Y轴
        private boolean isAutoDismiss = true;// 在点击按钮的时候，是否自定关闭dialog
        private DialogInterface.OnShowListener mShowListener;
        private DialogInterface.OnDismissListener mDismissListener;

        /**
         * 1：来源于activity，2：来源于fragment
         */
        private final int mTypeFrom;

        /**
         * @param activity    依赖的activity
         * @param contentView 布局的view
         * @author : 流星
         * @CreateDate: 2021/9/27
         * @Description: 构造Dialog的参数
         */
        public Builder(FragmentActivity activity, View contentView) {
            mTypeFrom = 1;
            if (activity != null) {
                mActivity = activity;
            }
            if (contentView != null) {
                mLayoutView = contentView;
            }
        }

        public Builder(Fragment fragment, View contentView) {
            mTypeFrom = 2;
            if (fragment != null) {
                mFragment = fragment;
                mActivity = fragment.getActivity();
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
            mTypeFrom = 1;
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

        public Builder(Fragment fragment, @LayoutRes int resource) {
            mTypeFrom = 2;
            if (fragment != null) {
                mFragment = fragment;
                FragmentActivity activity = fragment.getActivity();

                if ((resource != 0) && (activity != null)) {
                    View inflate = LayoutInflater.from(activity).inflate(resource, null, false);
                    if (inflate != null) {
                        mLayoutView = inflate;
                    }
                }
            }
        }

        /**
         * @param offsetX 偏移X轴
         * @param offsetY 偏移Y轴
         */
        public Builder setOffset(int offsetX, int offsetY) {
            this.mOffsetX = offsetX;
            this.mOffsetY = offsetY;
            return this;
        }

        /**
         * @param id 指定的id
         * @return 点击指定id的时候，关闭弹窗
         */
        public Builder setClose(@IdRes int id) {
            if (mLayoutView != null) {
                View view = this.mLayoutView.findViewById(id);
                if (view != null) {
                    if (mListCloseView == null) {
                        mListCloseView = new HashSet<>();
                    }
                    mListCloseView.add(view);
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
                if (mListCloseView == null) {
                    mListCloseView = new HashSet<>();
                }
                mListCloseView.add(view);
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
         * @return true:在点击按钮的时候，自动取消，false:不自动取消，默认自动取消
         */
        public Builder setAutoDismiss(boolean autoDismiss) {
            this.isAutoDismiss = autoDismiss;
            return this;
        }

        /**
         * @param width 设置宽度
         *              ,建议使用{@link android.view.ViewGroup.LayoutParams#MATCH_PARENT} or
         *              {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
         * @return 设置宽度
         */
        public Builder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        /**
         * @param mHeight 设置高度,建议使用{@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}
         *                or
         *                {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
         * @return 设置高度，需要在setContentView()方法之前设置，否则不生效
         */
        public Builder setHeight(int mHeight) {
            this.mHeight = mHeight;
            return this;
        }

        /**
         * @param type: dialog的类型，默认是带遮罩层的弹窗，使用的时候，只接收两个参数{@link DialogType#DEFAULT_DIALOG}
         *              或者{@link DialogType#HINT_DIALOG}
         */
        public Builder getDialogType(int type) {
            if ((type == DialogType.DEFAULT_DIALOG) || (type == DialogType.HINT_DIALOG)) {
                mDialogType = type;
            }
            return this;
        }

        /**
         * @return 在onStop的时候，是否要关闭掉弹窗，默认为false
         */
        public Builder setDismissDialogForStop(boolean stop) {
            stopDialog = stop;
            return this;
        }

        /**
         * @return 设置打开的监听
         */
        public Builder setOnShowListener(DialogInterface.OnShowListener showListener) {
            mShowListener = showListener;
            return this;
        }

        /**
         * @return 设置关闭的监听
         */
        public Builder setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
            mDismissListener = dismissListener;
            return this;
        }

        public DialogUtil Build() {
            return new DialogUtil(this);
        }
    }

    /**
     * 构建dialog 对象
     */
    private void initDialog() {
        if ((mActivity != null) && (!mActivity.isFinishing()) && (!mActivity.isDestroyed())) {
            // 避免重复出现弹窗
            if (mDialog != null) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }

            // 设置dialog的方式
            if (mDialogType == DialogType.DEFAULT_DIALOG) {
                mDialog = new Dialog(mActivity, R.style.base_dialog_default);
            } else if (mDialogType == DialogType.HINT_DIALOG) {
                mDialog = new Dialog(mActivity, R.style.base_dialog_hint);
            }

            // 设置布局
            if ((mLayoutView != null) && (mDialog != null)) {
                // 移除view的父类，避免布局重复添加时候的崩溃异常
                ViewParent parent = mLayoutView.getParent();
                if (parent instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) parent;
                    group.removeAllViews();
                }

                // 设置布局
                mDialog.setContentView(mLayoutView);

                // 设置属性，如果没有猜错，这个设置的属性，必须要在设置布局之前才可以，否则可能会卡死，出现异常
                setWindowAttributes();

                // 点击关闭dialog
                if (mListCloseView != null && mListCloseView.size() > 0) {
                    for (View next : mListCloseView) {
                        next.setOnClickListener(v -> dismiss());
                    }
                }

                // dialog展示时候的监听
                mDialog.setOnShowListener(dialog -> {
                    if (mShowListener != null) {
                        mShowListener.onShow(dialog);
                    }
                    EventBus.getDefault().post(new EventMessage(CommonConstants.CODE_DIALOG_SHOW));
                });

                // dialog 关闭时候的监听
                mDialog.setOnDismissListener(dialog -> {
                    if (mDismissListener != null) {
                        mDismissListener.onDismiss(dialog);
                    }
                    EventBus.getDefault().post(new EventMessage(CommonConstants.CODE_DIALOG_DISMISS));
                });
            }
        }
    }

    /**
     * 设置dialog的属性
     */
    private void setWindowAttributes() {
        if (mDialog != null) {

            // 按下返回键是否可以取消dialog
            mDialog.setCancelable(mCancelable);

            // dialog点击区域外的时候，是否可以取消dialog
            mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

            Window window = mDialog.getWindow();
            if (window != null) {
                window.setGravity(mGravity);
                WindowManager.LayoutParams attributes = window.getAttributes();
                if (attributes != null) {
                    attributes.width = mWidth;
                    attributes.height = mHeight;
                    attributes.x = mOffsetX;
                    attributes.y = mOffsetY;

                    if (mAnimation != 0) {
                        attributes.windowAnimations = mAnimation;
                    }

                    // 解决android 9.0水滴屏/刘海屏有黑边的问题
                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // attributes.layoutInDisplayCutoutMode =
                    // WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                    // }
                }

                // 设置属性
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * @param id       指定view的id
     * @param listener 点击事件
     * @return 响应view的点击事件
     */
    public DialogUtil setOnClickListener(@IdRes int id, DialogClickListener listener) {
        if (mLayoutView != null) {
            View view = mLayoutView.findViewById(id);
            setOnClickListener(view, listener);
        }
        return this;
    }

    /**
     * @param view     指定view
     * @param listener 点击事件
     * @return 响应view的点击事件
     */
    public DialogUtil setOnClickListener(View view, DialogClickListener listener) {
        if (listener != null && view != null) {
            view.setOnClickListener(v -> {
                listener.onClick(v, this);
                if (isAutoDismiss) {
                    dismiss();
                }
            });
        }
        return this;
    }

    /**
     * @param id       textView
     * @param content  标题
     * @param listener 点击事件
     * @return 按钮设置文字，并设置点击事件
     */
    public DialogUtil setOnClickListener(@IdRes int id, String content, DialogClickListener listener) {
        if (mLayoutView != null) {
            View view = mLayoutView.findViewById(id);
            if (!TextUtils.isEmpty(content)) {
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setText(content);
                }
            }
            setOnClickListener(view, listener);
        }
        return this;
    }

    /**
     * @param text 内容
     * @return 设置title内容，title的id必须是：R.id.tv_title
     */
    public DialogUtil setText(@IdRes int id, String text) {
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
     * @param id              控件的资源id
     * @param visibilityValue true:可见 false:不可见
     * @return 设置view可见与不可见
     */
    public DialogUtil setVisibility(@IdRes int id, boolean visibilityValue) {
        if (mLayoutView != null) {
            View view = mLayoutView.findViewById(id);
            if (view != null) {
                int visibility = view.getVisibility();
                if (visibilityValue) {
                    if (visibility != View.VISIBLE) {
                        view.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (visibility != View.GONE) {
                        view.setVisibility(View.GONE);
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
     * @return 设置加载view成功的监听
     */
    public DialogUtil setOnViewCreatedListener(OnViewCreatedListener listener) {
        // view布局设置之后的回调
        if (listener != null) {
            listener.onViewCreated(mLayoutView);
        }
        return this;
    }

    /**
     * 释放掉dialog，
     */
    public void release() {
        dismiss();

        if (mLayoutView != null) {
            mLayoutView = null;
        }
        if (mListCloseView != null) {
            mListCloseView = null;
        }
        if (mShowListener != null) {
            mShowListener = null;
        }
        if (mDismissListener != null) {
            mDismissListener = null;
        }

        if (mDialog != null) {
            mDialog = null;
        }

        if (mFragment != null) {
            mFragment = null;
        }
        if (mActivity != null) {
            mActivity = null;
        }

        if (mBuilder != null) {
            mBuilder = null;
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
        LogUtil.e("dialog--->stop:");
        if (stopDialog) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        // 在页面不可见的时候，自动关闭dialog
        release();
    }

}
