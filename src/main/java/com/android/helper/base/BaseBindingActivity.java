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
    private View mBindingRoot;

    @Override
    public void onBeforeCreateView() {
        super.onBeforeCreateView();

        mBinding = getBinding(getLayoutInflater(), null);

        if (mBinding != null) {
            mBindingRoot = mBinding.getRoot();
            setContentView(mBindingRoot);
        }
    }

    @Override
    public View getRootView() {
        return mBindingRoot;
    }

    @Override
    protected int getBaseLayout() {
        return 0;
    }

}
