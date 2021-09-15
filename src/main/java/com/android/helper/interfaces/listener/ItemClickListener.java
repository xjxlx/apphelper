package com.android.helper.interfaces.listener;

import androidx.viewbinding.ViewBinding;

public interface ItemClickListener<E extends ViewBinding, T> {
    void onItemClick(E e, int position, T t);
}
