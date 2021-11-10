package com.android.helper.base.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

/**
 * @author : 流星
 * @CreateDate: 2021/11/10-10:22 上午
 * @Description:
 */
public interface RecycleBindingListener {

    /**
     * @param inflater  布局管理器
     * @param container 父类的根布局
     * @return 返回一个ViewBinding 的对象，如果不需要嵌套布局的话，就直接使用 xxxBinding.inflate(layoutInflater),
     * 如果需要嵌套布局的话，就使用xxxBinding.inflate(layoutInflater, mBaseBinding.root, true)
     */
    ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    /**
     * @return 设置布局需要用到的view，从bindingView中去获取
     */
    View getRootView();
}
