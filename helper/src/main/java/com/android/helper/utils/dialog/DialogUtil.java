package com.android.helper.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
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

    @SuppressLint("StaticFieldLeak")
    private static DialogUtil dialogUtil;
    private Dialog mDialog;
    private Activity mActivity;
    private int mWidth = WindowManager.LayoutParams.MATCH_PARENT; // 宽
    private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT; // 高
    private int mGravity = Gravity.CENTER;// 默认居中显示
    private int mAnimation = R.style.base_dialog_animation;// 动画
    private View mRootView; // 布局的veiw
    private int mDialogType = DialogType.DEFAULT_DIALOG;

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

    private DialogUtil() {
    }

    /**
     * @return 获取单利对象对象
     */
    public static synchronized DialogUtil getInstance() {
        if (dialogUtil == null) {
            dialogUtil = new DialogUtil();
        }
        return dialogUtil;
    }

    public DialogUtil setDefaultDialog(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return null;
        }
        mActivity = activity;
        mDialogType = DialogType.DEFAULT_DIALOG;
        return dialogUtil;
    }

    public DialogUtil setHintDialog(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return null;
        }
        mActivity = activity;
        mDialogType = DialogType.HINT_DIALOG;
        return dialogUtil;
    }

    public DialogUtil setContentView(Activity activity, @LayoutRes int resource) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return null;
        }
        this.mActivity = activity;

        // 避免重复出现弹窗
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        // 设置dialog的方式
        if (mDialogType == DialogType.DEFAULT_DIALOG) {
            mDialog = new Dialog(activity, R.style.base_dialog_default);
        } else if (mDialogType == DialogType.HINT_DIALOG) {
            mDialog = new Dialog(activity, R.style.base_dialog_hint);
        }

        View view = LayoutInflater.from(mActivity).inflate(resource, null, false);
        if (view != null) {
            mRootView = view;
            mDialog.setContentView(view);
            setWindowAttributes();
        }
        return dialogUtil;
    }

    public DialogUtil setContentView(Activity activity, View view) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return null;
        }
        this.mActivity = activity;

        // 避免重复出现弹窗
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        // 设置dialog的方式
        if (mDialogType == DialogType.DEFAULT_DIALOG) {
            mDialog = new Dialog(activity, R.style.base_dialog_default);
        } else if (mDialogType == DialogType.HINT_DIALOG) {
            mDialog = new Dialog(activity, R.style.base_dialog_hint);
        }

        if (view != null) {
            mRootView = view;
            mDialog.setContentView(view);
            setWindowAttributes();
        }
        return dialogUtil;
    }

    private void setWindowAttributes() {
        if (mDialog != null) {
            Window window = mDialog.getWindow();
            if (window != null) {
                window.setGravity(mGravity);
                WindowManager.LayoutParams attributes = window.getAttributes();
                if (attributes != null) {
                    attributes.width = mWidth;
                    attributes.height = mHeight;
                    if (mAnimation != 0) {
                        attributes.windowAnimations = mAnimation;
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

    /**
     * @param width 设置宽度
     * @return 设置宽度，需要在setContentView()方法之前设置，否则不生效
     */
    public DialogUtil setWidth(int width) {
        this.mWidth = width;
        return dialogUtil;
    }

    /**
     * @param mHeight 设置高度
     * @return 设置高度，需要在setContentView()方法之前设置，否则不生效
     */
    public DialogUtil setHeight(int mHeight) {
        this.mHeight = mHeight;
        return dialogUtil;
    }

    /**
     * @param gravity Gravity.CENTER ...
     * @return 设置位置，需要在setContentView()方法之前设置，否则不生效
     */
    public DialogUtil setGravity(int gravity) {
        this.mGravity = gravity;
        return dialogUtil;
    }

    /**
     * @param animation 设置动画
     * @return 设置动画，需要在setContentView()方法之前设置，否则不生效，一般使用{R.style.base_dialog_animation}
     */
    public DialogUtil setAnimation(int animation) {
        this.mAnimation = animation;
        return dialogUtil;
    }

    /**
     * @param cancel false时为点击周围空白处弹出层不自动消失
     * @return 弹窗点击周围空白处弹出层自动消失弹窗消失(false时为点击周围空白处弹出层不自动消失)
     */
    public DialogUtil setCanceledOnTouchOutside(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return dialogUtil;
    }

    /**
     * @param flag true：可以取消，false ：不可以取消
     * @return 设置点击返回键的时候，是否可以取消， true：可以取消，false ：不可以取消
     */
    public DialogUtil setCancelable(boolean flag) {
        if (mDialog != null) {
            mDialog.setCancelable(flag);
        }
        return dialogUtil;
    }

    public void show() {
        if ((mActivity != null) && (!mActivity.isFinishing()) && (!mActivity.isDestroyed()) && (mDialog != null) && (!mDialog.isShowing())) {
            mDialog.show();
        } else {
            try {
                LogUtil.e("dialog打开失败：activity:" + mActivity + " isFinishing:" + (mActivity.isFinishing()) + " dialog:" + mDialog + " isShowing:" + (mDialog.isShowing()));
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
            if (mRootView != null) {
                View view = mRootView.findViewById(id);
                if (view != null) {
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        textView.setText(text);
                    }
                }
            }
        }
        return dialogUtil;
    }

    /**
     * @param id  指定的id
     * @param <T> 指定的类型
     * @return 返回一个对象
     */
    public <T> T getView(@IdRes int id) {
        if (mRootView != null) {
            try {
                return (T) mRootView.findViewById(id);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param id 指定的id
     * @return 点击指定id的时候，关闭弹窗
     */
    public DialogUtil setClose(@IdRes int id) {
        if (mRootView != null) {
            View view = this.mRootView.findViewById(id);
            if (view != null) {
                view.setOnClickListener(v -> dismiss());
            }
        }
        return dialogUtil;
    }

    /**
     * @param id       指定view的id
     * @param listener 点击事件
     * @return 响应view的点击事件
     */
    public DialogUtil setOnClickListener(@IdRes int id, View.OnClickListener listener) {
        if (mRootView != null) {
            View view = mRootView.findViewById(id);
            if (view != null && listener != null) {
                view.setOnClickListener(listener);
            }
        }
        return dialogUtil;
    }

    /**
     * @param id       textView
     * @param content  标题
     * @param listener 点击事件
     * @return 按钮设置文字，并设置点击事件
     */
    public DialogUtil setOnClickListener(@IdRes int id, String content, View.OnClickListener listener) {
        if (mRootView != null) {
            View view = mRootView.findViewById(id);
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
        return dialogUtil;
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

}
