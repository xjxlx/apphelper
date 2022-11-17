package com.android.helper.interfaces.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * lifecycle的基类，为了书写方便，进行基类的封装
 */
public interface LifecycleDestroyObserver extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy();

}
