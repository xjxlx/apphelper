package com.android.helper.base.title;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author : 流星
 * @CreateDate: 2021/12/1-23:56
 * @Description: 标题的对象封装
 */
public class TitleBar {
    private Context mContext;

    private int mTitleLayoutId; // title的根布局资源
    private int mTitleBarLayoutId;// 顶部title的资源id
    private int mLeftBackLayoutId; //左侧返回键父布局id
    private int mTitleId; // 中间标题的id
    private int mRightLayoutId; // 右侧标题的父布局
    private int mRightTextId; // 右侧标题中的文字id
    private int mContentLayoutId; // 布局下面真实使用到的布局，这个对象可用可不用
    private int mPlaceHolderLayoutId;// title布局下面，和真实使用到的activity布局，同一层级的view，用来展示错误数据的布局

    /**
     * title的根布局资源
     */
    private View mTitleLayout;

    public TitleBar(TitleBuilder builder) {
        if (builder != null) {
            this.mContext = builder.mContext;
            this.mTitleLayoutId = builder.mTitleLayoutId;
            this.mTitleBarLayoutId = builder.mTitleBarLayoutId;
            this.mLeftBackLayoutId = builder.mLeftBackLayoutId;
            this.mTitleId = builder.mTitleId;
            this.mRightLayoutId = builder.mRightLayoutId;
            this.mRightTextId = builder.mRightTextId;
            this.mContentLayoutId = builder.mContentLayoutId;
            this.mPlaceHolderLayoutId = builder.mPlaceHolderLayoutId;
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
     * @return 获取顶部titleBar的布局
     */
    public ViewGroup getTitleBarLayout() {
        ViewGroup view = null;
        if (mTitleLayout != null) {
            if (mLeftBackLayoutId != 0) {
                view = mTitleLayout.findViewById(mTitleBarLayoutId);
            }
        }
        return view;
    }

    /**
     * @return 获取左侧返回键的根布局
     */
    public ViewGroup getLeftBackLayout() {
        ViewGroup view = null;
        if (mTitleLayout != null) {
            if (mLeftBackLayoutId != 0) {
                view = mTitleLayout.findViewById(mLeftBackLayoutId);
            }
        }
        return view;
    }

    /**
     * @return 获取中间的标题
     */
    public TextView getTitleView() {
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
     * 右侧标题中的文字id
     */
    public TextView getRightTextView() {
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

    /**
     * title 底部真实用到的布局
     */
    public ViewGroup getPlaceHolderLayout() {
        ViewGroup mContentLayout = null;
        if (mTitleLayout != null) {
            if (mPlaceHolderLayoutId != 0) {
                View view = mTitleLayout.findViewById(mPlaceHolderLayoutId);
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
