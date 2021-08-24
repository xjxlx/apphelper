package com.android.helper.interfaces.listener;

import android.view.View;

public interface ViewCallBackListener<T> {
    void callBack(View view, T t);
}
