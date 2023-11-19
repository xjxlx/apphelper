package com.android.helper.utils.livedata;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.common.utils.LogUtil;
import com.android.helper.app.BaseApplication;

import org.jetbrains.annotations.NotNull;

/**
 * @author : 流星
 * @CreateDate: 2021/12/28-2:45 下午
 * @Description: 使用livedata 去传递数据
 */
public class LiveDataBus implements LifecycleEventObserver {

    private static LiveDataBus liveDataBus;
    private final String FLAG = "ON_DESTROY";
    private final String TAG = "LiveDataBus --->";
    private Observer<LiveDataMessage> mObserver;
    private MutableLiveData<LiveDataMessage> mLiveData;

    private LiveDataBus() {
        FragmentActivity commonLivedata = BaseApplication.getInstance().getCommonLivedata();
        if (commonLivedata != null) {
            LiveDataModel liveDataModel = new ViewModelProvider(commonLivedata).get(LiveDataModel.class);
            mLiveData = liveDataModel.getLiveData();
            // 公共类数据的监听
            commonLivedata.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
                if (TextUtils.equals(event.name(), FLAG)) {
                    if (mLiveData != null) {
                        mLiveData = null;
                    }
                    if (mObserver != null) {
                        mObserver = null;
                    }
                    if (liveDataBus != null) {
                        liveDataBus = null;
                    }
                    LogUtil.e(TAG, "移除了公共数据的监听对象！");
                }
            });
        }
    }

    public static LiveDataBus getSingleInstance() {
        if (liveDataBus == null) {
            synchronized (LiveDataBus.class) {
                if (liveDataBus == null) {
                    liveDataBus = new LiveDataBus();
                }
            }
        }
        return liveDataBus;
    }

    /**
     * 设置数据
     *
     * @param message 设置发送出去的数据
     */
    public void postMessage(LiveDataMessage message) {
        if (mLiveData != null && message != null) {
            LogUtil.e(TAG, "发送了数据的对象：" + message);
            mLiveData.postValue(message);
        }
    }

    /**
     * 关联生命周期的数据监听
     *
     * @param listener 数据的回调
     */
    public void onMessage(FragmentActivity activity, LiveDataBusListener listener) {
        if (mLiveData != null && activity != null) {
            activity.getLifecycle().addObserver(this);
            mLiveData.observe(activity, liveDataMessage -> {
                if (listener != null) {
                    listener.onLiveDataBus(liveDataMessage);
                }
            });
        }
    }

    /**
     * 关联生命周期的数据监听
     *
     * @param listener 数据的回调
     */
    public void onMessage(Fragment fragment, LiveDataBusListener listener) {
        if (mLiveData != null && fragment != null) {
            fragment.getLifecycle().addObserver(this);
            mLiveData.observe(fragment, liveDataMessage -> {
                if (listener != null) {
                    listener.onLiveDataBus(liveDataMessage);
                }
            });
        }
    }

    /**
     * 不关联生命周期的数据接收，但是必须绑定页面，在页面移除的时候，结束数据的关联
     *
     * @param activity 当前的页面，用来结束数据的绑定，否则可能会导致内存的泄露
     * @param listener 数据的回调
     */
    public void onForeverMessage(FragmentActivity activity, LiveDataBusListener listener) {
        if (activity != null) {
            activity.getLifecycle().addObserver(this);
        }
        if (mLiveData != null) {
            mObserver = message -> {
                if (listener != null) {
                    listener.onLiveDataBus(message);
                }
            };
            // 不关联生命周期的数据监听
            mLiveData.observeForever(mObserver);
        }
    }

    /**
     * 不关联生命周期的数据接收，但是必须绑定页面，在页面移除的时候，结束数据的关联
     *
     * @param fragment 当前的页面，用来结束数据的绑定，否则可能会导致内存的泄露
     * @param listener 数据的回调
     */
    public void onForeverMessage(Fragment fragment, LiveDataBusListener listener) {
        if (fragment != null) {
            fragment.getLifecycle().addObserver(this);
        }
        if (mLiveData != null) {
            mObserver = message -> {
                if (listener != null) {
                    listener.onLiveDataBus(message);
                }
            };
            // 不关联生命周期的数据监听
            mLiveData.observeForever(mObserver);
        }
    }

    @Override
    public void onStateChanged(@NonNull @NotNull LifecycleOwner source, @NonNull @NotNull Lifecycle.Event event) {
        // E/ZHGJ: │ event:ON_CREATE
        // E/ZHGJ: │ event:ON_START
        // E/ZHGJ: │ event:ON_RESUME
        // E/ZHGJ: │ event:ON_PAUSE
        // E/ZHGJ: │ event:ON_STOP
        // E/ZHGJ: │ event:ON_START
        // E/ZHGJ: │ event:ON_RESUME
        // E/ZHGJ: │ event:ON_PAUSE
        // E/ZHGJ: │ event:ON_STOP
        // E/ZHGJ: │ event:ON_DESTROY
        if (TextUtils.equals(event.name(), FLAG)) {
            if (mLiveData != null) {
                if (mObserver != null) {
                    mLiveData.removeObserver(mObserver);
                    LogUtil.e(TAG, "移除了关联生命周期的监听");
                }
            }
        }
    }
}
