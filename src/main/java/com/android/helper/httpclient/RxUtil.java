package com.android.helper.httpclient;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.common.utils.LogUtil;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;

import java.util.concurrent.TimeUnit;

import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxUtil implements BaseLifecycleObserver {

    private FragmentActivity mActivity;
    private Fragment mFragment;
    private Disposable mSubscribeCountdown;

    public RxUtil(Builder builder) {
        if (builder != null) {
            mActivity = builder.mActivity;
            mFragment = builder.mFragment;

            if (mActivity != null) {
                mActivity.getLifecycle()
                        .addObserver(this);
            }

            if (mFragment != null) {
                mFragment.getLifecycle()
                        .addObserver(this);
            }
        }
    }

    /**
     * @param <T>
     * @return 只做线程的转换，其实如果这里用了flatMap做一次数据转换其实会更好
     */
    public static <T> FlowableTransformer<T, T> getSchedulerFlowable() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param <T>数据类型
     * @return 线程间的转换
     */
    public static <T> ObservableTransformer<T, T> getSchedulerObservable() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * <ol>
     * 倒计时的工具
     * </ol>
     *
     * @param totalTime    总的时长，单位是毫秒
     * @param initialDelay 第一次发送的延迟时间，单位是毫秒
     * @param period       间隔的时间，单位是毫秒，这里只是指定数值
     *                     <ui>
     *                     间隔时间的单位，TimeUnit是一个枚举类型，直接调用需要使用的单位即可，如：TimeUnit.MINUTES
     *                     1毫秒 ： {@link TimeUnit#MILLISECONDS}
     *                     1秒 ： {@link TimeUnit#SECONDS}
     *                     1分钟 ：{@link TimeUnit#MINUTES}
     *                     1小时 ：{@link TimeUnit#HOURS}
     *                     1天 ：{@link TimeUnit#DAYS}
     *                     </ui>
     */
    public void countdown(long totalTime, long initialDelay, long period, CountdownListener countdownListener) {
        /*
         * 第一个参数：总的倒计时数据
         * 第二个参数：当前的计数器
         * 第三个参数：当前的倒计时
         */
        long[] countdown = {totalTime, 0, 0};

        mSubscribeCountdown = Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerObservable())
                .map(aLong -> { // 转换数据，把倒计时的数据发送出去
                    // 当前的计数器
                    countdown[1] = aLong + 1;

                    // 总计数每次减一秒
                    countdown[0] -= 1000;

                    return countdown[0];
                })
                .takeUntil(aLong -> { // 条件处理器，用来中断倒计时
                    // takeUntil takeUntil
                    return countdown[0] < 0;
                })
                .subscribe(aLong -> { // 发送结果
                    countdown[2] = aLong;
                    if (countdownListener != null) {
                        countdownListener.countdown(mSubscribeCountdown, countdown[1], countdown[2]);
                    }
                });
    }

    /**
     * @param totalTime       最大的时间，如果是0，则没有任何限制
     * @param initialDelay    第一次回调的间隔
     * @param period          每次间隔的时间
     * @param counterListener 计数器的回调
     */
    public void counter(long totalTime, long initialDelay, long period, CounterListener counterListener) {
        mSubscribeCountdown = Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerObservable())
                .takeUntil(aLong -> { // 条件处理器，用来中断倒计时,
                    if (totalTime != 0) {
                        return aLong * period >= totalTime;
                    } else {
                        return false;
                    }
                })
                .subscribe(aLong -> { // 发送结果
                    if (counterListener != null) {
                        counterListener.counter(mSubscribeCountdown, aLong);
                    }
                });
    }

    @Override
    public void onCreate() {
        LogUtil.e("rxUtil ---> onCreate");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        LogUtil.e("rxUtil ---> onDestroy");
        // 中断的操作
        if (mSubscribeCountdown != null) {
            if (!mSubscribeCountdown.isDisposed()) {
                mSubscribeCountdown.dispose();
            }
        }
    }

    public static class Builder {
        private final FragmentActivity mActivity;
        private final Fragment mFragment;

        public Builder(FragmentActivity activity) {
            mActivity = activity;
            mFragment = null;
        }

        public Builder(Fragment fragment) {
            mFragment = fragment;
            mActivity = null;
        }

        public RxUtil build() {
            return new RxUtil(this);
        }
    }
}
