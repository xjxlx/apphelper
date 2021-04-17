package android.helper.ui.activity;

import android.annotation.SuppressLint;
import android.helper.R;
import android.view.View;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.httpclient.RetrofitHelper;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.httpclient.TestApi;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.ToastUtil;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import retrofit2.Response;

public class RxJava2Activity extends BaseTitleActivity {

//    //不指定背压策略
//    MISSING,
//    //出现背压就抛出异常
//    ERROR,
//    //指定无限大小的缓存池，此时不会出现异常，但无限制大量发送会发生OOM
//    BUFFER,
//    //如果缓存池满了就丢弃掉之后发出的事件
//    DROP,
//    //在DROP的基础上，强制将最后一条数据加入到缓存池中
//    LATEST

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_rx_java2;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        super.initView();
        setTitleContent("测试RxJava2");
        setonClickListener(R.id.btn_test1, R.id.btn_test2);

        findViewById(R.id.btn_test1).setOnClickListener(v -> {
            request();
//
//            // 被观察者  发起者
//            Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
//                @Override
//                public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
//                    for (int i = 0; i < 200; i++) {
//                        emitter.onNext(i);
//                        LogUtil.e("发送了第：" + i + "个!");
//                    }
//                }
//            }, BackpressureStrategy.MISSING);
//
//            // 观察者  响应者
//            flowable.subscribe(new FlowableSubscriber<Integer>() {
//                @Override
//                public void onSubscribe(@NonNull Subscription s) {
//                    LogUtil.e("~~~~~~~~~>onSubscribe!");
//
//                    s.cancel();
//
//                    s.request(1);
//                }
//
//                @Override
//                public void onNext(Integer integer) {
//                    LogUtil.e("~~~~~~~~~>onNext!");
//                    LogUtil.e("接收了第：" + integer + "个!");
//                    request();
//                }
//
//                @Override
//                public void onError(Throwable t) {
//                    LogUtil.e("~~~~~~~~~>Throwable!");
//                }
//
//                @Override
//                public void onComplete() {
//                    LogUtil.e("~~~~~~~~~>onComplete!");
//                }
//            })
            ;
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_test1:
                request();
                break;

            case R.id.btn_test2:

                Flowable.create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        for (int i = 0; i < 10000; i++) {
                            emitter.onNext("i = " + i);
                        }
                    }
                }, BackpressureStrategy.LATEST)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onSubscribe(Subscription s) {
                                s.request(5);

                            }

                            @Override
                            public void onNext(String s) {
                                LogUtil.e("tag ----> " + s);
                            }

                            @Override
                            public void onError(Throwable t) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void request() {

        RetrofitHelper.create(TestApi.class).myInfo("13213211130", "0000", "", "f2d928e6edd8a88a")
                .compose(RxUtil.getScheduler())  // 转换线程
                .subscribeWith(new DisposableSubscriber<Response<String>>() {
                    @Override
                    public void onNext(Response<String> stringResponse) {
                        LogUtil.e("onNext:" + stringResponse);
                        ToastUtil.show(stringResponse.body());
                    }

                    @Override
                    public void onError(Throwable t) {
                        LogUtil.e("onError:" + t.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}