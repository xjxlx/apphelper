package com.android.helper.utils.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.TextViewUtil;

import java.util.ArrayList;
import java.util.List;

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
<<<<<<< HEAD
=======

    private FragmentActivity mActivity;         // 上下文对象
    private View mLayout;                       // 布局
    private View mCloseView;                    // 关闭布局的view
    private int mWidth;                         // 默认的宽高
    private int mHeight;                        // 高度
    private boolean mTouchable;                 // 触摸是否可以取消，默认可以
    private boolean mClippingEnabled;           // 是否可以超出屏幕显示，默认false:表示可以遮罩
    private int mGravity;                       // 默认居中显示
    private float mAlpha;                       // 透明度
    private OnViewCreatedListener mViewCreatedListener;

>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
    private PopupWindow.OnDismissListener mOnDismissListener;
    private OnShowListener mShowListener;

    private FragmentActivity mActivity; // 上下文对象
    private Fragment mFragment;
    private View mLayout; // 布局
    private int mGravity;// 默认居中显示
    private float mAlpha;// 透明度

    private PopupWindowUtil(Builder builder) {
        // 构建popupWindow
        if (builder != null) {

            mActivity = builder.mActivity;
            mLayout = builder.mLayout;
            mCloseView = builder.mCloseView;

            mWidth = builder.mWidth;
            mHeight = builder.mHeight;
            mTouchable = builder.mTouchable;
            mClippingEnabled = builder.mClippingEnabled;
            mGravity = builder.mGravity;
            mAlpha = builder.mAlpha;
            mViewCreatedListener = builder.mViewCreatedListener;

            initPopupWindow();
        }
    }

    /**
     * @author : 流星
     * @CreateDate: 2021/9/28
     * @Description: 构建popupWindow
     */
<<<<<<< HEAD
    private void initPopupWindow(Builder builder) {
        mActivity = builder.mActivity;
        mFragment = builder.mFragment;
        mLayout = builder.mLayout;

        ViewCreatedListener createdListener = builder.mCreatedListener;

        mGravity = builder.mGravity;
        mAlpha = builder.mAlpha;
        // 1:fragmentActivity 2:fragment
        int typeFromPage = builder.mTypeFromPage;

        // 局部使用的对象
        int width = builder.mWidth;
        int height = builder.mHeight;
        // 关闭布局的view
        List<View> closeView = builder.mCloseView;
        // 触摸是否可以取消，默认可以
        boolean touchable = builder.mTouchable;
        // 是否可以超出屏幕显示，默认false:表示可以遮罩
        boolean clippingEnabled = builder.mClippingEnabled;

=======
    private void initPopupWindow() {
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
        // 释放掉原来的pop
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }

<<<<<<< HEAD
        mPopupWindow = new PopupWindow(width, height);
=======
        mPopupWindow = new PopupWindow(mWidth, mHeight);
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
        // 设置布局
        if (mLayout != null) {
            ViewParent parent = mLayout.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parent;
                viewGroup.removeAllViews();
            }

<<<<<<< HEAD
=======
            mPopupWindow.setContentView(mLayout);

>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
            //解决android 9.0水滴屏/刘海屏有黑边的问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Window window = mActivity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(attributes);
            }

            // 添加管理
<<<<<<< HEAD
            if (typeFromPage == 1) {
                Lifecycle lifecycle = mActivity.getLifecycle();
                lifecycle.addObserver(this);
            } else if (typeFromPage == 2) {
                if (mFragment != null) {
                    Lifecycle lifecycle = mFragment.getLifecycle();
                    lifecycle.addObserver(this);
                }
            }
=======
            Lifecycle lifecycle = mActivity.getLifecycle();
            lifecycle.addObserver(this);
        }
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93

            if (createdListener != null) {
                createdListener.onViewCreated(mLayout);
            }

<<<<<<< HEAD
            // 点击外部是否关闭popupWindow
            if (touchable) {
                //: pop背景（如果不设置就不会点击消失）
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mPopupWindow.setFocusable(true);
            } else {
                mPopupWindow.setBackgroundDrawable(null);
                mPopupWindow.setFocusable(false);
            }
            //: 设置可以点击pop以外的区域
            mPopupWindow.setOutsideTouchable(touchable);

            //: 设置PopupWindow可触摸
            mPopupWindow.setTouchable(true);

            //: 设置超出屏幕显示
            mPopupWindow.setClippingEnabled(clippingEnabled);

            mPopupWindow.setContentView(mLayout);
        }

        // 关闭布局的view集合
        if (closeView != null) {
            for (int i = 0; i < closeView.size(); i++) {
                View view = closeView.get(i);
                view.setOnClickListener(v -> dismiss());
            }
=======
        //: 设置可以点击pop以外的区域
        mPopupWindow.setOutsideTouchable(mTouchable);
        //: 设置PopupWindow可获得焦点
        mPopupWindow.setFocusable(true);
        //: 设置PopupWindow可触摸
        mPopupWindow.setTouchable(true);
        //: 设置超出屏幕显示 ---> false表示允许窗口扩展到屏幕外
        mPopupWindow.setClippingEnabled(mClippingEnabled);

        // 关闭布局的view
        if (mCloseView != null) {
            mCloseView.setOnClickListener(v -> dismiss());
        }

        // 回调布局对象
        if (mViewCreatedListener != null && mLayout != null) {
            mViewCreatedListener.onViewCreated(mLayout);
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
        }

        // 关闭的监听
        mPopupWindow.setOnDismissListener(() -> {
            if (mAlpha > 0) {
                closeAlpha();
            }
            if (mOnDismissListener != null) {
                mOnDismissListener.onDismiss();
            }
        });
    }

    /**
     * @param view 用来获取token的view，随意view都可以
     * @return 相对于整个窗口的显示，默认显示在窗口的正中间
     */
    public PopupWindowUtil show(View view) {
        show(view, 0, 0);
        return this;
    }

    /**
     * @param view 用来获取token的view，随意view都可以
     * @return 相对于整个窗口的显示，并指定偏移
     */
<<<<<<< HEAD
    public PopupWindowUtil showAtLocation(View view, int xoff, int yoff) {
        if (view != null) {
=======
    public PopupWindowUtil show(View view, int xoff, int yoff) {
        if ((mActivity != null) && (view != null)) {
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
            if ((!mPopupWindow.isShowing())) {
                view.post(() -> {
                    if (mAlpha > 0) {
                        openAlpha();
                    }
                    mPopupWindow.showAtLocation(view, mGravity, xoff, yoff);
                    if (mShowListener != null) {
                        mShowListener.onShow(mLayout);
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
    public PopupWindowUtil showDown(View anchor, int xoff, int yoff) {
        if (anchor != null) {
            anchor.post(() -> {
                if (mPopupWindow != null) {
                    if (mAlpha > 0) {
                        openAlpha();
                    }
                    mPopupWindow.showAsDropDown(anchor, xoff, yoff);

                    if (mShowListener != null) {
                        mShowListener.onShow(mLayout);
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
    public PopupWindowUtil showDown(View anchor) {
        this.showDown(anchor, 0, 0);
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
<<<<<<< HEAD
        if (mLayout != null) {
=======
        if ((mLayout != null)) {
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
            View view = mLayout.findViewById(id);
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
        private Fragment mFragment;
        private View mLayout; // 布局
        private List<View> mCloseView; // 关闭布局的view
        private ViewCreatedListener mCreatedListener;

        private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;   // 默认的宽高
        private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        private boolean mTouchable = true; // 触摸是否可以取消，默认可以
        private boolean mClippingEnabled = false; // 是否可以超出屏幕显示，默认false:表示可以遮罩
        private int mGravity = Gravity.CENTER;// 默认居中显示
        private float mAlpha;// 透明度
<<<<<<< HEAD
        private int mTypeFromPage = 1;// 1:fragmentActivity 2:fragment
=======
        private OnViewCreatedListener mViewCreatedListener;
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93

        public Builder(FragmentActivity activity, View layout) {
            mActivity = activity;
            mLayout = layout;
            mTypeFromPage = 1;
        }

        public Builder(FragmentActivity activity, @LayoutRes int resource) {
            mActivity = activity;
            mTypeFromPage = 1;
            if (resource != 0) {
                View inflate = LayoutInflater.from(activity).inflate(resource, null, false);
                if (inflate != null) {
                    mLayout = inflate;
                }
            }
        }

        public Builder(Fragment fragment, View layout) {
            mFragment = fragment;
            mLayout = layout;
            mActivity = fragment.getActivity();
            mTypeFromPage = 2;
        }

        public Builder(Fragment fragment, @LayoutRes int resource) {
            mFragment = fragment;
            mActivity = fragment.getActivity();
            mTypeFromPage = 2;
            if (resource != 0) {
                View inflate = LayoutInflater.from(fragment.getContext()).inflate(resource, null, false);
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
         * @return 是否可以超出屏幕显示，false :可以，true:不可以，默认可以
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
         * @param id 指定的id
         * @return 点击指定id的时候，关闭弹窗
         */
        public Builder setCloseView(@IdRes int id) {
            if (mLayout != null) {
                View view = mLayout.findViewById(id);
                if (view != null) {
                    setClose(view);
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
                if (mCloseView == null) {
                    mCloseView = new ArrayList<>();
                }
                mCloseView.add(view);
            }
            return this;
        }

        public Builder setAlpha(@FloatRange(from = 0.0f, to = 1.0f) float alpha) {
            this.mAlpha = alpha;
            return this;
        }

<<<<<<< HEAD
        public Builder setViewCreated(ViewCreatedListener viewCreated) {
            this.mCreatedListener = viewCreated;
=======
        /**
         * @param viewCreatedListener 布局对象的回调
         * @return 布局对象的回调，用于获取字view
         */
        public Builder setViewCreatedListener(OnViewCreatedListener viewCreatedListener) {
            this.mViewCreatedListener = viewCreatedListener;
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
            return this;
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

    /**
     * 窗口打开和关闭的监听
     */
    public PopupWindowUtil setOnDismissListener(PopupWindow.OnDismissListener dismissListener) {
        this.mOnDismissListener = dismissListener;
        return this;
    }

    /**
     * 窗口打开的监听
     */
    public PopupWindowUtil setOnShowListener(OnShowListener showListener) {
        this.mShowListener = showListener;
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

    @Override
    public void onDestroy() {
        // 手动关闭弹窗，避免崩溃
        if (isShowing()) {
            dismiss();
        }
        if (mPopupWindow != null) {
            mPopupWindow = null;
        }
<<<<<<< HEAD

=======
>>>>>>> b25933ad8136c98283fd341648e1c8599d546c93
    }
}
