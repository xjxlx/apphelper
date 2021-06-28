package com.android.helper.base;

import androidx.viewbinding.ViewBinding;

import com.android.helper.interfaces.BindingViewListener;

public abstract class BaseBindingActivity<T extends ViewBinding> extends BaseActivity implements BindingViewListener<T> {

    public T mBinding;

    @Override
    protected int getBaseLayout() {
        return 0;
    }

    @Override
    protected void OnCreatedBefore() {
        super.OnCreatedBefore();

        mBinding = getBinding(getLayoutInflater(), null);
        if (mBinding != null) {
            setContentView(mBinding.getRoot());
        }
    }

}
