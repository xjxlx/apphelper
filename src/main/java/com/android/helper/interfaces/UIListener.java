package com.android.helper.interfaces;

import android.os.Bundle;
import android.view.View;

public interface UIListener {

    /**
     * 在setContentView之前的调用方法，用于特殊的使用
     */
    void onBeforeCreateView();

    /**
     * Fragment初始化view
     */
    void initView(View rootView);

    /**
     * Activity初始化view
     */
    void initView();

    /**
     * 初始化点击事件
     */
    void initListener();

    /**
     * 初始化数据
     */
    void initData(Bundle savedInstanceState);

}
