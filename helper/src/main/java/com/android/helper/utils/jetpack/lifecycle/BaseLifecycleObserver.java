package com.android.helper.utils.jetpack.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * lifecycle的基类，为了书写方便，进行基类的封装
 */
public interface BaseLifecycleObserver extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy();

}
