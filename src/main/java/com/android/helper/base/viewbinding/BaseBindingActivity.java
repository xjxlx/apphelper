package com.android.helper.base.viewbinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.android.helper.R;
import com.android.helper.httpclient.BaseHttpSubscriber;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.BindingViewListener;
import com.android.helper.interfaces.UIListener;
import com.android.helper.utils.ClickUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.statusBar.StatusBarUtil;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 封装viewBinding 的activity 的基类
 *
 * @param <T> 指定的viewBinding的类型
 */
public abstract class BaseBindingActivity<T extends ViewBinding> extends AppCompatActivity implements
        BindingViewListener<T>, UIListener, View.OnClickListener {

    public T mBinding;
    public BaseBindingActivity<T> mContext;

    /*
     *此处不能写成静态的，否则就会和使用RxManager一样了
     */
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // 设置状态栏
        StatusBarUtil.getInstance(mContext).setStatusColor(R.color.base_title_background_color);

        onBeforeCreateView();
        mBinding = getBinding(getLayoutInflater(), null);

        View rootView = getRootView();
        if (rootView != null) {
            setContentView(rootView);
        }

        initView();
        initListener();
        initData();
    }

    @Override
    public void onBeforeCreateView() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public View getRootView() {
        return mBinding.getRoot();
    }

    /**
     * 新建一个Intent，然后跳转到指定的界面
     *
     * @param cls 指定跳转的界面
     */
    protected void startActivity(Class<? extends Activity> cls) {
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        startActivity(intent);
    }

    /**
     * @param intent 指定的intent
     * @param cls    指定的界面
     */
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

    /**
     * 设置view的点击事件,检测点击的时间间隔
     *
     * @param array view的数组
     */
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
        // 在指定的间隔时间内是否做了双击
        boolean doubleClick = ClickUtil.isDoubleClick(400);
        if (doubleClick) {
            LogUtil.e("点击速度过快了！");
        } else {
            onClick(v);
        }
    }


    @Override
    public void onClick(View v) {

    }

    @SuppressLint("CheckResult")
    public <F> Disposable net(@NonNull Flowable<F> flowAble, BaseHttpSubscriber<F> subscriber) {
        BaseHttpSubscriber<F> httpSubscriber = flowAble
                .compose(RxUtil.getScheduler())  // 转换线程
                .onBackpressureLatest()  // 使用背压，保留最后一次的结果
                .subscribeWith(subscriber);  // 返回对象

        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(httpSubscriber); // 添加到管理类中
        }
        return httpSubscriber;
    }
}
