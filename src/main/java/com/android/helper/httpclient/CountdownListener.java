package com.android.helper.httpclient;

import io.reactivex.disposables.Disposable;

/**
 * @author : 流星
 * @CreateDate: 2021/12/21-9:12 下午
 * @Description: 倒计时的监听返回
 */
public interface CountdownListener {

    /**
     * @param disposable 计时器的对象，可以用来中断计时器
     * @param counter    当前的计数器
     * @param countdown  当前的倒计时
     */
    void countdown(Disposable disposable, long counter, long countdown);
}
