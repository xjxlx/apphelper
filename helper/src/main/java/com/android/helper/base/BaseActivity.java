package com.android.helper.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.helper.R;
import com.android.helper.httpclient.BaseHttpSubscriber;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.listener.HttpManagerListener;
import com.android.helper.utils.ClickUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.statusBar.StatusBarUtil;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 最基层的Activity
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, HttpManagerListener {

    public BaseActivity mContext;
    protected String TAG;

    /*
     *此处不能写成静态的，否则就会和使用RxManager一样了
     */
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // 使用本类的名字作为tag
        TAG = getClass().getSimpleName();

        LogUtil.e("当前的页面：Activity：--->  " + getClass().getName());

        // 在onCreate之前调用的方法
        OnCreatedBefore();

        int baseLayout = getBaseLayout();
        if (baseLayout != 0) {
            setContentView(baseLayout);
        }

        // 设置状态栏
        StatusBarUtil.getInstance(mContext).setStatusColor(R.color.base_title_background_color);

        onInitViewBefore();
        initView();
        initListener();
        initData();
    }

    @SuppressLint("CheckResult")
    public <T> Disposable net(@NonNull Flowable<T> flowAble, BaseHttpSubscriber<T> subscriber) {

        BaseHttpSubscriber<T> httpSubscriber = flowAble
                .compose(RxUtil.getScheduler())  // 转换线程
                .onBackpressureLatest()  // 使用背压，保留最后一次的结果
                .subscribeWith(subscriber);  // 返回对象

        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(httpSubscriber); // 添加到管理类中
        }
        return httpSubscriber;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mContext = this;
    }

    /**
     * 在onCreate之前调用的方法，用于特殊的使用
     */
    protected void OnCreatedBefore() {
    }

    protected void initListener() {
    }

    protected void onInitViewBefore() {
    }

    protected void initView() {
    }

    protected void initData() {
    }

    protected abstract int getBaseLayout();

    protected void startActivity(Class<? extends Activity> cls) {
        Intent intent = new Intent(mContext, cls);
        startActivity(intent);
    }

    protected void startActivity(Intent intent, Class<? extends Activity> cls) {
        if (intent != null && cls != null) {
            startActivity(intent, cls);
        }
    }

    /**
     * 设置view的点击事件
     *
     * @param ids id的数组
     */
    protected void setonClickListener(int... ids) {
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                View view = findViewById(id);
                if (view != null) {
                    view.setOnClickListener(this::onViewClick);
                }
            }
        }
    }

    protected void setonClickListener(View... array) {
        if (array != null && array.length > 0) {
            for (View view : array) {
                if (view != null) {
                    view.setOnClickListener(this::onViewClick);
                }
            }
        }
    }

    /**
     * 过滤点击的事件
     *
     * @param v 点击的view
     */
    private void onViewClick(View v) {
        boolean doubleClick = ClickUtil.isDoubleClick(1000);
        if (doubleClick) {
            LogUtil.e("点击速度过快了！");
        } else {
            onClick(v);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void removeHttp(Disposable disposable) {
        if (disposable != null) {
            boolean disposed = disposable.isDisposed();
            if (!disposed) {
                disposable.dispose();
                LogUtil.e("移除一个指定的请求对象！");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁了rx2
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }

}
