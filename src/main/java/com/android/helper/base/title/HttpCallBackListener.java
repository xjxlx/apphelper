package com.android.helper.base.title;

import org.jetbrains.annotations.NotNull;

/**
 * @author : 流星
 * @CreateDate: 2022/1/1-18:23
 * @Description:
 */
public interface HttpCallBackListener<T> {

    void onHttpStart();

    void onHttpSuccess(@NotNull T t);

    void onHttpError(@NotNull Throwable e);

    void onHttpComplete();
}
