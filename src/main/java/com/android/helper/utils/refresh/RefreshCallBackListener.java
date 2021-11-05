package com.android.helper.utils.refresh;

import org.jetbrains.annotations.NotNull;

/**
 * @author : 流星
 * @CreateDate: 2021/11/5-4:21 下午
 * @Description:
 */
public interface RefreshCallBackListener<T> {
    void onStart();

    void onSuccess(@NotNull T t);

    void onError(@NotNull Throwable e);

    void onComplete();
}
