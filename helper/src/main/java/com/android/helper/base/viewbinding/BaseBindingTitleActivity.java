package com.android.helper.base.viewbinding;

import android.text.TextUtils;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.android.helper.R;
import com.android.helper.databinding.BaseTitleActivityBinding;
import com.android.helper.utils.TextViewUtil;

/**
 * 目的：打造一个使用viewBinding封装的基类activity
 * 逻辑：
 * 1：首先封装一个基类的布局
 * 2：构造一个接口对象，传入所需要的子布局
 * 3：泛型类
 */
public abstract class BaseBindingTitleActivity<T extends ViewBinding> extends BaseBindingActivity<T> {

    public BaseTitleActivityBinding mTitleBinding;
    protected View.OnClickListener mBackClickListener = null;// 返回的点击事件

    @Override
    public void onBeforeCreateView() {
        super.onBeforeCreateView();
        mTitleBinding = BaseTitleActivityBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initListener() {
        super.initListener();
        // 设置返回事件
        setonClickListener(R.id.ll_base_title_back);
    }

    @Override
    public View getRootView() {
        return mTitleBinding.getRoot();
    }

    /**
     * 给activity设置title
     *
     * @param title 标题内容
     */
    protected void setTitleContent(String title) {
        if (!TextUtils.isEmpty(title)) {
            TextViewUtil.setText(mTitleBinding.baseTitle.tvBaseTitle, title);
        }
    }

    /**
     * 设置返回按钮的点击事件
     *
     * @param listener 返回的点击事件
     */
    protected void setBackClickListener(View.OnClickListener listener) {
        this.mBackClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        // 返回键
        if (id == R.id.ll_base_title_back) {
            if (mBackClickListener != null) {
                // 处理额外的点击事件
                mBackClickListener.onClick(v);
            } else {
                // 直接返回
                finish();
            }
        }
    }
}
