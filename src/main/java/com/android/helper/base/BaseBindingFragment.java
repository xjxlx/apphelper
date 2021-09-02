package com.android.helper.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.android.helper.interfaces.BindingViewListener;

import org.jetbrains.annotations.NotNull;

/**
 * 自带绑定ViewBinding的fragment
 *
 * @param <T> 指定的ViewBinding
 */
public abstract class BaseBindingFragment<T extends ViewBinding> extends BaseFragment implements BindingViewListener<T> {

    public T mBinding;

    @Override
    protected int getBaseLayout() {
        return 0;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = getBinding(inflater, container);
        View rootView = getRootView();
        if (rootView != null) {
            mRootView = rootView;
            initView(rootView);
            initData();
        }
        return rootView;
    }

    @Override
    public View getRootView() {
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBinding != null) {
            mBinding = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding != null) {
            mBinding = null;
        }
    }
}
