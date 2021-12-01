package com.android.helper.base.title;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.helper.base.BaseActivity;
import com.android.helper.utils.TextViewUtil;
import com.android.helper.utils.ViewUtil;

/**
 * @author : 流星
 * @CreateDate: 2021/11/29-1:41
 * @Description: 带标题头的Activity
 */
public abstract class BaseTitleActivity extends BaseActivity {

    protected View.OnClickListener mBackClickListener = null;// 返回的点击事件
    protected View.OnClickListener mRightBackClickListener = null;// 返回的点击事件
    private TitleBar mTitleBar;
    private ViewGroup mLeftBackLayout;
    private ViewGroup mRightLayout;
    private TextView mRightText;
    protected ViewGroup mContentLayout;
    protected View mTitleRootLayout;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // 如果单页面没有设置独立的title信息，就使用公用的title信息
        if (mTitleBar == null) {
            TitleBuilder globalBuilder = TitleBar.getGlobalTitleBarBuilder();
            if (globalBuilder != null) {
                mTitleBar = globalBuilder.build(this);
            }
        }

        if (mTitleBar != null) {
            // 获取title的根布局
            mTitleRootLayout = mTitleBar.getTitleRootLayout();
            // 获取内容布局
            mContentLayout = mTitleBar.getContentLayout();

            // 添加实际的activity
            int titleLayout = getTitleLayout();
            if (titleLayout != 0) {
                // 把真实的布局添加到 mFlActivityContent 中去
                if (mContentLayout != null) {
                    LayoutInflater.from(this).inflate(titleLayout, mContentLayout, true);
                }

                // 设置布局
                if (mTitleRootLayout != null) {
                    setContentView(mTitleRootLayout);
                }
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        super.initView();

        if (mTitleBar != null) {
            // 返回的父类布局
            mLeftBackLayout = mTitleBar.getLeftBackLayout();
            TextView leftBackText = mTitleBar.getLeftBackText(); // 返回的文字
            // 设置可见性
            boolean leftBackTextShow = mTitleBar.getLeftBackTextShow();
            ViewUtil.setViewVisible(leftBackText, leftBackTextShow);

            mRightLayout = mTitleBar.getRightLayout(); // 右侧标题的父布局
            // 右侧布局可见性
            boolean rightLayoutShow = mTitleBar.getRightLayoutShow();
            ViewUtil.setViewVisible(mRightLayout, rightLayoutShow);

            // 右侧文字描述
            if (!rightLayoutShow) {
                // 右侧标题的textView
                mRightText = mTitleBar.getRightText();
            }

            // 设置标题
            String titleContent = setTitleContent();
            if (!TextUtils.isEmpty(titleContent)) {
                TextView title = mTitleBar.getTitle(); // 标题的内容
                TextViewUtil.setText(title, titleContent);
            }
        }
    }

    @Override
    public void initListener() {
        super.initListener();

        // 左侧返回键的点击事件
        if (mLeftBackLayout != null) {
            mLeftBackLayout.setOnClickListener(v -> {
                if (mBackClickListener != null) {
                    // 处理额外的点击事件
                    mBackClickListener.onClick(v);
                } else {
                    // 直接返回
                    finish();
                }
            });
        }

        // 右侧布局的点击事件
        if (mRightText != null) {
            mRightText.setOnClickListener(v -> {
                if (mRightBackClickListener != null) {
                    // 处理额外的点击事件
                    mRightBackClickListener.onClick(v);
                } else {
                    // 直接返回
                    finish();
                }
            });
        }

    }

    @Override
    protected int getBaseLayout() {
        return 0;
    }

    /**
     * @return 获取布局资源
     */
    protected abstract int getTitleLayout();

    /**
     * @return 设置标题内容
     */
    protected abstract String setTitleContent();

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 右侧的title
     */
    protected boolean setRightTitle(String rightTitle) {
        boolean success = false;
        if (TextUtils.isEmpty(rightTitle)) {
            return false;
        } else {
            if (mRightText != null && mRightLayout != null) {
                // 设置可见
                ViewUtil.setViewVisible(mRightLayout, true);
                ViewUtil.setViewVisible(mRightText, true);
                // 设置内容
                TextViewUtil.setText(mRightText, rightTitle);
                success = true;
            }
        }
        return success;
    }

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 标题
     * @param color      颜色
     * @param size       大小
     */
    protected void setRightTitle(String rightTitle, int color, int size) {
        boolean success = setRightTitle(rightTitle);
        if (success) {
            if (color != 0) {
                mRightText.setTextColor(color);
            }

            if (size > 0) {
                mRightText.setTextSize(size);
            }
        }
    }

    /**
     * 页面的返回
     *
     * @param backId    返回的id
     * @param listeners 返回的点击事件
     */
    public void setTitleBack(int backId, View.OnClickListener listeners) {
        if (backId != 0) {
            if (listeners != null) {
                findViewById(backId).setOnClickListener(listeners);
            } else {
                setTitleBack(backId);
            }
        }
    }

    /**
     * 页面的返回
     *
     * @param backId 返回的id
     */
    public void setTitleBack(int backId) {
        if (backId != 0) {
            findViewById(backId).setOnClickListener(v -> finish());
        }
    }

    /**
     * 设置标题
     *
     * @param titleId      标题控件的id
     * @param titleContent 标题的内容
     */
    public void setTitleContent(int titleId, String titleContent) {
        if (titleId != 0) {
            View view = findViewById(titleId);
            if (view instanceof TextView) {
                TextView titleView = (TextView) view;
                TextViewUtil.setText(titleView, titleContent);
            }
        }
    }

    /**
     * 设置返回按钮的点击事件
     *
     * @param listener 返回的点击事件
     */
    protected void setBackClickListener(View.OnClickListener listener) {
        this.mBackClickListener = listener;
    }

    /**
     * 设置右侧的点击事件
     *
     * @param listener 右侧的点击事件
     */
    protected void setRightTitleClickListener(View.OnClickListener listener) {
        this.mRightBackClickListener = listener;
    }

    /**
     * @param titleBar 单个页面指定的titleBar的信息
     */
    public void setTitleBar(TitleBar titleBar) {
        this.mTitleBar = titleBar;
    }

}
