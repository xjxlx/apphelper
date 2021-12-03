package com.android.helper.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.android.helper.interfaces.BindingViewListener;
import com.android.helper.utils.TextViewUtil;

/**
 * 封装viewBinding 的activity 的基类
 *
 * @param <T> 指定的viewBinding的类型
 */
public abstract class BaseBindingActivity<T extends ViewBinding> extends BaseActivity implements BindingViewListener<T> {

    public T mBinding;
    private View mBindingRoot;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = getBinding(getLayoutInflater(), null);
        if (mBinding != null) {
            mBindingRoot = mBinding.getRoot();
            setContentView(mBindingRoot);

            initView();
            initListener();
            initData(savedInstanceState);
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

    /**
     * 设置标题
     *
     * @param tv_title 标题的id
     * @param title    标题的内容
     */
    protected void setTitleContent(int tv_title, String title) {
        if (tv_title != 0 && (!TextUtils.isEmpty(title))) {
            View view = findViewById(tv_title);
            if (view instanceof TextView) {
                TextViewUtil.setText((TextView) view, title);
            }
        }
    }

    /**
     * 设置返回按钮的点击事件
     */
    protected void setTitleBack(int tv_title_bar_left) {
        if (tv_title_bar_left != 0) {
            View view = findViewById(tv_title_bar_left);
            if (view != null) {
                view.setOnClickListener(v -> finish());
            }
        }
    }

}
