package com.android.helper.utils.jetpack;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.android.helper.interfaces.TagListener;
import com.android.helper.utils.ClassUtil;
import com.android.helper.utils.LogUtil;

/**
 * 监听Activity 和 fragment 的生命周期
 */
public class AndroidLifecycle implements LifecycleEventObserver, TagListener {

    private Object mObj;

    /**
     * 监听Activity 和 fragment 的生命周期
     *
     * @param obj 当前类的对象
     */
    public AndroidLifecycle(Object obj) {
        mObj = obj;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        LogUtil.e(getTag(), event.name());
    }

    @Override
    public String getTag() {
        return ClassUtil.getClassName(mObj);
    }
}
