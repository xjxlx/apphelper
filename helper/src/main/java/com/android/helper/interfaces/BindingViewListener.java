package com.android.helper.interfaces;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public interface BindingViewListener<T extends ViewBinding> {
    T getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);
}
