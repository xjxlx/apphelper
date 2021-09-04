package com.android.helper.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

/**
 * 绑定viewBinding的RecycleView的ViewHolder
 *
 * @param <T> 指定类型的ViewBinding
 */
public class BaseBindingVH<T extends ViewBinding> extends RecyclerView.ViewHolder {

    public BaseBindingVH(@NonNull @NotNull T t) {
        super(t.getRoot());
    }
}
