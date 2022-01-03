package com.android.helper.base.title;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.android.helper.R;
import com.android.helper.interfaces.BindingViewListener;

/**
 * @author : 流星
 * @CreateDate: 2021/11/29-2:39
 * @Description: 带title的绑定布局
 *
 * <ol>
 *     使用说明：
 *     1：如果使用了这个方法，则必须在调用 {@link BindingViewListener#getBinding(LayoutInflater, ViewGroup)}的时候，在viewGroup的属性中传递true
 *        否则，布局无法正常引用
 * </ol>
 */
public abstract class BaseBindingTitleActivity<T extends ViewBinding> extends AppBaseTitleActivity implements BindingViewListener<T> {

    public T mBinding;
    private View mBindingRoot;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mContentLayout != null) {
            mBinding = getBinding(getLayoutInflater(), mContentLayout);
        }

        if (mBinding != null) {
            mBindingRoot = mBinding.getRoot();
            if (mTitleRootLayout != null) {
                setContentView(mTitleRootLayout);

                // 避免加载顺序导致的异常
                initView();
                initListener();
                initData(savedInstanceState);
                initDataAfter();
            }
        }
    }

    @Override
    protected int getTitleLayout() {
        return 0;
    }

    @Override
    public View getRootView() {
        return mBindingRoot;
    }

}
