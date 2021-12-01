package com.android.helper.base.title;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.helper.R;

/**
 * @author : 流星
 * @CreateDate: 2021/12/1-23:56
 * @Description: 标题的对象封装
 */
public class TitleBar {
    private Context mContext;

    private int mTitleLayoutId; // title的根布局资源
    private int mLeftBackLayoutId; //左侧返回键父布局id
    private int mLeftBackTextId; // 左侧返回键文字的id
    private boolean mShowBackText; // 返回的文字是否可见
    private int mTitleId; // 中间标题的id
    private int mRightLayoutId; // 右侧标题的父布局
    private int mRightTextId; // 右侧标题中的文字id
    private boolean mShowRightText; // 右侧文字是否可见
    private boolean mShowRightLayout; // 右侧布局是否可见
    private int mContentLayoutId; // 布局下面真实使用到的布局，这个对象可用可不用

    /**
     * title的根布局资源
     */
    private View mTitleLayout;

    public TitleBar(TitleBuilder builder) {
        if (builder != null) {
            this.mContext = builder.mContext;
            this.mTitleLayoutId = builder.mTitleLayoutId;
            this.mLeftBackLayoutId = builder.mLeftBackLayoutId;
            this.mLeftBackTextId = builder.mLeftBackTextId;
            this.mShowBackText = builder.mShowBackText;
            this.mTitleId = builder.mTitleId;
            this.mRightLayoutId = builder.mRightLayoutId;
            this.mShowRightLayout = builder.mShowRightLayout;
            this.mRightTextId = builder.mRightTextId;
            this.mShowRightText = builder.mShowRightText;
            this.mContentLayoutId = builder.mContentLayoutId;
        }
    }

    /**
     * @return 获取title的根布局
     */
    @SuppressLint("InflateParams")
    public View getTitleRootLayout() {
        if (mContext != null) {
            if (mTitleLayoutId != 0) {
                mTitleLayout = LayoutInflater.from(mContext).inflate(mTitleLayoutId, null, false);
            }
        }
        return mTitleLayout;
    }

    /**
     * @return 获取左侧返回键的根布局
     */
    public ViewGroup getLeftBackLayout() {
        ViewGroup mLeftBackLayout = null;
        if (mTitleLayout != null) {
            if (mLeftBackLayoutId != 0) {
                View view = mTitleLayout.findViewById(mLeftBackLayoutId);
                if (view instanceof ViewGroup) {
                    mLeftBackLayout = (ViewGroup) view;
                }
            }
        }
        return mLeftBackLayout;
    }

    /**
     * @return 获取左侧返回布局中的textView
     */
    public TextView getLeftBackText() {
        TextView mLeftBackText = null;
        if (mTitleLayout != null) {
            if (mLeftBackTextId != 0) {
                View view = mTitleLayout.findViewById(mLeftBackTextId);
                if (view instanceof TextView) {
                    mLeftBackText = (TextView) view;
                }
            }
        }
        return mLeftBackText;
    }

    /**
     * @return 返回的文字描述是否可见
     */
    public boolean getLeftBackTextShow() {
        return mShowBackText;
    }

    /**
     * @return 获取中间的标题
     */
    public TextView getTitle() {
        TextView mTitle = null;
        if (mTitleLayout != null) {
            if (mTitleId != 0) {
                View view = mTitleLayout.findViewById(mTitleId);
                if (view instanceof TextView) {
                    mTitle = (TextView) view;
                }
            }
        }
        return mTitle;
    }

    /**
     * 右侧标题的父布局
     */
    public ViewGroup getRightLayout() {
        ViewGroup mRightLayout = null;
        if (mTitleLayout != null) {
            if (mRightLayoutId != 0) {
                View view = mTitleLayout.findViewById(mRightLayoutId);
                if (view instanceof ViewGroup) {
                    mRightLayout = (ViewGroup) view;
                }
            }
        }
        return mRightLayout;
    }

    /**
     * @return 右侧的布局是否可见
     */
    public boolean getRightLayoutShow() {
        return mShowRightLayout;
    }

    /**
     * 右侧标题中的文字id
     */
    public TextView getRightText() {
        TextView mRightText = null;
        if (mTitleLayout != null) {
            if (mRightTextId != 0) {
                View view = mTitleLayout.findViewById(mRightTextId);
                if (view instanceof TextView) {
                    mRightText = (TextView) view;
                }
            }
        }
        return mRightText;
    }

    /**
     * @return 右侧的文字描述是否可见
     */
    public boolean getRightTextShow() {
        return mShowRightText;
    }

    /**
     * title 底部真实用到的布局
     */
    public ViewGroup getContentLayout() {
        ViewGroup mContentLayout = null;
        if (mTitleLayout != null) {
            if (mContentLayoutId != 0) {
                View view = mTitleLayout.findViewById(mContentLayoutId);
                if (view instanceof ViewGroup) {
                    mContentLayout = (ViewGroup) view;
                }
            }
        }
        return mContentLayout;
    }

    @SuppressLint("StaticFieldLeak")
    private static TitleBuilder mBuilder;

    /**
     * @param builder 设置全局的信息
     */
    public static void setGlobalTitleBar(TitleBuilder builder) {
        mBuilder = builder;
    }

    public static TitleBuilder getGlobalTitleBarBuilder() {
        return mBuilder;
    }

}
