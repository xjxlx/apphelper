package android.helper.ui.activity.jetpack.room;

import android.annotation.SuppressLint;

import com.android.helper.httpclient.RxUtil;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public class RoomUtil {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @SuppressLint("CheckResult")
    public <T> void execute(Flowable<T> flowable, DisposableSubscriber<T> subscriber) {

        DisposableSubscriber<T> disposable = flowable
                .compose(RxUtil.getScheduler())  // 转换线程
                .onBackpressureLatest()  // 使用背压，保留最后一次的结果
                .subscribeWith(subscriber);// 返回对象

        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(disposable); // 添加到管理类中
        }

    }

}
