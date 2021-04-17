package com.android.helper.interfaces;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public interface IViewBinding<R extends ViewBinding> {
    R getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);
}
