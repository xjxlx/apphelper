package com.android.helper.httpclient;

//import io.reactivex.FlowableTransformer;
//import io.reactivex.ObservableTransformer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {

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

    /**
     * @param initialDelay 第一次发送的延迟时间
     * @param period       间隔的时间，单位由unit来确认，这里只是指定数值
     * @param unit         间隔时间的单位，TimeUnit是一个枚举类型，直接调用需要使用的单位即可，如：TimeUnit.MINUTES
     *                     1毫秒 ： {@link TimeUnit#MILLISECONDS}
     *                     1秒   ： {@link TimeUnit#SECONDS}
     *                     1分钟 ：{@link TimeUnit#MINUTES}
     *                     1小时 ：{@link TimeUnit#HOURS}
     *                     1天   ：{@link TimeUnit#DAYS}
     */
    public static void countdown(long initialDelay, long period, TimeUnit unit) {
        Disposable subscribe = Observable.interval(initialDelay, period, unit)
                .compose(RxUtil.getSchedulerObservable())
                .takeUntil(new Predicate<Long>() {
                    @Override
                    public boolean test(@NotNull Long aLong) throws Exception {

                        return false;
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                    }
                });

    }

}
