package com.android.helper.httpclient;

//import io.reactivex.FlowableTransformer;
//import io.reactivex.ObservableTransformer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;

import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil<T> {

    /**
     * @param <T>
     * @return 只做线程的转换，其实如果这里用了flatMap做一次数据转换其实会更好
     */
    public static <T> FlowableTransformer<T, T> getSchedulerFlowable() {
        return upstream ->
                upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param <T>数据类型
     * @return 线程间的转换
     */
    public static <T> ObservableTransformer<T, T> getSchedulerObservable() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
