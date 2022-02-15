package com.android.helper.httpclient;

import io.reactivex.disposables.Disposable;

/**
 * @author : 流星
 * @CreateDate: 2022/2/15-10:40 上午
 * @Description:计数器的回调，例如：1、2、3、4、5、6
 */
public interface CounterListener {

    /**
     * @param disposable 计时器的对象，可以用来中断计时器
     * @param counter    当前计数器的值
     */
    void counter(Disposable disposable, long counter);

}
