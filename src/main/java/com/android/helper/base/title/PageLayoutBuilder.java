package com.android.helper.base.title;

import android.content.Context;

/**
 * @author : 流星
 * @CreateDate: 2021/12/2-2:01
 * @Description:页面布局的对象
 */
public class PageLayoutBuilder {
    protected Context mContext;

    public PageLayoutBuilder() {
    }

    /**
     * title的根布局资源
     */
    protected int mTitleLayoutId;

    /**
     * 顶部titleBar的资源id
     */
    protected int mTitleBarLayoutId;

    /**
     * 左侧返回键父布局id
     */
    protected int mLeftBackLayoutId;

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
     * title布局下面，和真实使用到的activity布局，同一层级的view，用来展示错误数据的布局
     */
    protected int mPlaceHolderLayoutId;

    /**
     * @param titleLayoutId R.layout.xxx
     * @return 设置title的资源布局
     */
    public PageLayoutBuilder setTitleLayoutId(int titleLayoutId) {
        mTitleLayoutId = titleLayoutId;
        if (mTitleLayoutId != 0) {
            mTitleType = 2;
        }
        return this;
    }

    /**
     * @param titleBarLayoutId R.id.xx
     * @return 设置顶部titleBar的资源id
     */
    public PageLayoutBuilder setTitleBarLayoutId(int titleBarLayoutId) {
        mTitleBarLayoutId = titleBarLayoutId;
        return this;
    }

    /**
     * @param leftBackLayoutId R.id.xx
     * @return 设置返回的父布局id
     */
    public PageLayoutBuilder setLeftBackLayoutId(int leftBackLayoutId) {
        mLeftBackLayoutId = leftBackLayoutId;
        return this;
    }

    /**
     * @param titleId R.id.xx
     * @return 设置title的id
     */
    public PageLayoutBuilder setTitleId(int titleId) {
        mTitleId = titleId;
        return this;
    }

    /**
     * @param rightLayoutId R.id.xx
     * @return 设置右侧的父布局id
     */
    public PageLayoutBuilder setRightLayoutId(int rightLayoutId) {
        mRightLayoutId = rightLayoutId;
        return this;
    }

    /**
     * @param rightTextId R.id.xx
     * @return 设置右侧文字的id
     */
    public PageLayoutBuilder setRightTextId(int rightTextId) {
        mRightTextId = rightTextId;
        return this;
    }

    /**
     * @param contentLayoutId R.id.xx
     * @return 底部content具体使用的ViewGroup的布局id
     */
    public PageLayoutBuilder setContentLayoutId(int contentLayoutId) {
        mContentLayoutId = contentLayoutId;
        return this;
    }

    /**
     * @param placeHolderLayoutId R.id.xx
     * @return title布局下面，和真实使用到的activity布局，同一层级的view，用来展示错误数据的布局
     */
    public PageLayoutBuilder setPlaceHolderLayoutId(int placeHolderLayoutId) {
        mPlaceHolderLayoutId = placeHolderLayoutId;
        return this;
    }

    public PageLayoutManager build(Context context) {
        mContext = context;
        return new PageLayoutManager(this);
    }
}
