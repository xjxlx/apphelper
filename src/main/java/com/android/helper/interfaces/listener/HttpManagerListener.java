package com.android.helper.interfaces.listener;

import io.reactivex.disposables.Disposable;

/**
 * 网络请求管理器的接口
 */
public interface HttpManagerListener {

    /**
     * 移除一个指定的请求对象
     */
    void removeHttp(Disposable disposable);

}
