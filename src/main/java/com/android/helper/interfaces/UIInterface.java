package com.android.helper.interfaces;

import android.os.Bundle;

public interface UIInterface {

    /**
     * 初始化状态栏
     */
    void initStatusBar();

    /**
     * 在setContentView之前的调用方法，用于特殊的使用
     */
    void onBeforeCreateView();

    /**
     * 初始化点击事件
     */
    void initListener();

    /**
     * 初始化数据
     *
     * @param savedInstanceState 保存的数据对象
     */
    void initData(Bundle savedInstanceState);

    /**
     * 初始化initData之后的操作，在某些场景中去使用
     */
    void initDataAfter();

}
