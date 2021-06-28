package com.android.helper.interfaces;

public interface UIListener {

    /**
     * 在setContentView之前的调用方法，用于特殊的使用
     */
    void onBeforeCreateView();

    /**
     * 初始化view
     */
    void initView();

    /**
     * 初始化点击事件
     */
    void initListener();

    /**
     * 初始化数据
     */
    void initData();

}
