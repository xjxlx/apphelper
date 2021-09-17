package com.android.helper.interfaces.listener;

import android.view.View;

public interface OnSelectorListener<T> {
    void onSelector(View view, int position, T t);
}
