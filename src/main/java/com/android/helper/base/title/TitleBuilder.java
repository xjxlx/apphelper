package com.android.helper.base.title;

import android.content.Context;

/**
 * @author : 流星
 * @CreateDate: 2021/12/2-2:01
 * @Description:
 */
public class TitleBuilder {
    protected Context mContext;

    public TitleBuilder() {
    }

    /**
     * title的根布局资源
     */
    protected int mTitleLayoutId;

    /**
     * 左侧返回键父布局id
     */
    protected int mLeftBackLayoutId;

    /**
     * 左侧返回键文字的id
     */
    protected int mLeftBackTextId;
    /**
     * 返回文字是否可见
     */
    protected boolean mShowBackText;

    /**
     * 中间标题的id
     */
    protected int mTitleId;

    /**
     * 右侧标题的父布局
     */
    protected int mRightLayoutId;

    /**
     * 右侧布局是否可见
     */
    protected boolean mShowRightLayout;
    /**
     * 右侧标题中的文字id
     */
    protected int mRightTextId;

    /**
     * 右侧文字是否可见
     */
    protected boolean mShowRightText;

    /**
     * title的类型
     */
    protected int mTitleType = 1; // 1:默认的，2：指定的

    /**
     * title 布局下面真实使用到的布局，这个对象可用可不用
     */
    protected int mContentLayoutId;

    public TitleBuilder setTitleLayoutId(int titleLayoutId) {
        mTitleLayoutId = titleLayoutId;
        if (mTitleLayoutId != 0) {
            mTitleType = 2;
        }
        return this;
    }

    public TitleBuilder setLeftBackLayoutId(int leftBackLayoutId) {
        mLeftBackLayoutId = leftBackLayoutId;
        return this;
    }

    public TitleBuilder setLeftBackTextId(int leftBackTextId) {
        mLeftBackTextId = leftBackTextId;
        return this;
    }

    public TitleBuilder setShowBackText(boolean showBackText) {
        mShowBackText = showBackText;
        return this;
    }

    public TitleBuilder setTitleId(int titleId) {
        mTitleId = titleId;
        return this;
    }

    public TitleBuilder setRightLayoutId(int rightLayoutId) {
        mRightLayoutId = rightLayoutId;
        return this;
    }

    public TitleBuilder setShowRightLayout(boolean showRightLayout) {
        mShowRightLayout = showRightLayout;
        return this;
    }

    public TitleBuilder setRightTextId(int rightTextId) {
        mRightTextId = rightTextId;
        return this;
    }

    public TitleBuilder setShowRightText(boolean showRightText) {
        mShowRightText = showRightText;
        return this;
    }


    public TitleBuilder setContentLayoutId(int contentLayoutId) {
        mContentLayoutId = contentLayoutId;
        return this;
    }

    public TitleBar build(Context context) {
        mContext = context;
        return new TitleBar(this);
    }
}
