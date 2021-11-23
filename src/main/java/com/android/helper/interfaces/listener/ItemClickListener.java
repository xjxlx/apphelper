package com.android.helper.interfaces.listener;

import androidx.viewbinding.ViewBinding;

public interface ItemClickListener<V extends ViewBinding, T> {
    void onItemClick(V binding, int position, T t);
}
