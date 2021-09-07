package com.android.helper.base;

import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.android.helper.interfaces.BindingViewListener;

/**
 * 封装viewBinding 的activity 的基类
 *
 * @param <T> 指定的viewBinding的类型
 */
public abstract class BaseBindingActivity<T extends ViewBinding> extends BaseActivity implements BindingViewListener<T> {

    public T mBinding;

    @Override
    public void onBeforeCreateView() {
        super.onBeforeCreateView();

        mBinding = getBinding(getLayoutInflater(), null);

        if (mBinding != null) {
            setContentView(mBinding.getRoot());
        }

        View rootView = getRootView();
        if (rootView != null) {
            setContentView(rootView);
        }
    }

    @Override
    public View getRootView() {
        return mBinding.getRoot();
    }

    @Override
    protected int getBaseLayout() {
        return 0;
    }

}
