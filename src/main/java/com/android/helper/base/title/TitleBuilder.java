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
     * 左侧返回文字的内容
     */
    protected String mLeftBackText;

    /**
     * 中间标题的id
     */
    protected int mTitleId;

    /**
     * 右侧标题的父布局
     */
    protected int mRightLayoutId;

    /**
     * 右侧标题中的文字id
     */
    protected int mRightTextId;

    /**
     * title的类型
     */
    protected int mTitleType = 1; // 1:默认的，2：指定的

    /**
     * title 布局下面真实使用到的布局，这个对象可用可不用
     */
    protected int mContentLayoutId;

    /**
     * @param titleLayoutId R.layout.xxx
     * @return 设置title的资源布局
     */
    public TitleBuilder setTitleLayoutId(int titleLayoutId) {
        mTitleLayoutId = titleLayoutId;
        if (mTitleLayoutId != 0) {
            mTitleType = 2;
        }
        return this;
    }

    /**
     * @param leftBackLayoutId R.id.xx
     * @return 设置返回的父布局id
     */
    public TitleBuilder setLeftBackLayoutId(int leftBackLayoutId) {
        mLeftBackLayoutId = leftBackLayoutId;
        return this;
    }

    /**
     * @param leftBackTextId R.id.xx
     * @return 设置返回的TextView的id
     */
    public TitleBuilder setLeftBackTextId(int leftBackTextId) {
        mLeftBackTextId = leftBackTextId;
        return this;
    }

    /**
     * @param showBackText true:可见，false:不可见
     * @return 设置返回的文字是否可见
     */
    public TitleBuilder setShowBackText(boolean showBackText) {
        mShowBackText = showBackText;
        return this;
    }

    /**
     * @param leftBackText 具体的返回文字的内容
     * @return 设置返回的文字内容
     */
    public TitleBuilder setLeftBackText(String leftBackText) {
        mLeftBackText = leftBackText;
        return this;
    }

    /**
     * @param titleId R.id.xx
     * @return 设置title的id
     */
    public TitleBuilder setTitleId(int titleId) {
        mTitleId = titleId;
        return this;
    }

    /**
     * @param rightLayoutId R.id.xx
     * @return 设置右侧的父布局id
     */
    public TitleBuilder setRightLayoutId(int rightLayoutId) {
        mRightLayoutId = rightLayoutId;
        return this;
    }

    /**
     * @param rightTextId R.id.xx
     * @return 设置右侧文字的id
     */
    public TitleBuilder setRightTextId(int rightTextId) {
        mRightTextId = rightTextId;
        return this;
    }

    /**
     * @param contentLayoutId R.id.xx
     * @return 底部content具体使用的ViewGroup的布局id
     */
    public TitleBuilder setContentLayoutId(int contentLayoutId) {
        mContentLayoutId = contentLayoutId;
        return this;
    }

    public TitleBar build(Context context) {
        mContext = context;
        return new TitleBar(this);
    }
}
