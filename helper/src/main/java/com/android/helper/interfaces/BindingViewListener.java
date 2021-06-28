package com.android.helper.interfaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public interface BindingViewListener<T extends ViewBinding> {

    /**
     * @param inflater  布局管理器
     * @param container 父类的根布局
     * @return 返回一个ViewBinding 的对象，如果不需要嵌套布局的话，就直接使用 xxxBinding.inflate(layoutInflater),
     * 如果需要嵌套布局的话，就使用xxxBinding.inflate(layoutInflater, mBaseBinding.root, true)
     */
    T getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    /**
     * @return 设置布局需要用到的view，从bindingView中去获取
     */
    View getRootView();
}
