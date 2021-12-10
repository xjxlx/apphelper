package com.android.helper.interfaces.listener;

import android.view.View;

import androidx.viewbinding.ViewBinding;

public interface ItemClickListener<V extends ViewBinding, T> {
    void onItemClick(View view, V binding, int position, T t);
}
