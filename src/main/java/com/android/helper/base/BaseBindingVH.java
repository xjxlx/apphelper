package com.android.helper.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

/**
 * 绑定viewBinding的RecycleView的ViewHolder
 *
 * @param <V> 指定类型的ViewBinding
 */
public class BaseBindingVH<V extends ViewBinding> extends RecyclerView.ViewHolder {

    public V mBinding;

    public BaseBindingVH(@NonNull @NotNull V viewBinding) {
        super(viewBinding.getRoot());
        this.mBinding = viewBinding;
    }

}
