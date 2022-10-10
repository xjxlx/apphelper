package com.android.helper.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.android.helper.R;
import com.android.helper.httpclient.BaseHttpSubscriber;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.ActivityUiInterface;
import com.android.helper.interfaces.TagListener;
import com.android.helper.interfaces.UIInterface;
import com.android.helper.interfaces.listener.HttpManagerListener;
import com.android.helper.utils.ClassUtil;
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
public abstract class AppBaseActivity extends AppCompatActivity implements View.OnClickListener, HttpManagerListener, TagListener, UIInterface, ActivityUiInterface {

    public FragmentActivity mActivity;
    /*
     *此处不能写成静态的，否则就会和使用RxManager一样了
     */
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        LogUtil.e("当前的页面：Activity：--->  " + getClass().getName());

        // 在onCreate之前调用的方法
        onBeforeCreateView();
        // 初始化状态栏
        initStatusBar();

        int baseLayout = getBaseLayout();
        if (baseLayout != 0) {
            setContentView(baseLayout);
            // 只有在设置完了布局之后才会去走初始化方法，避免顺序异常
            initView();
            initListener();
            initData(savedInstanceState);
            initDataAfter();
        }
    }

    @SuppressLint("CheckResult")
    public <T> Disposable net(@NonNull Flowable<T> flowAble, BaseHttpSubscriber<T> subscriber) {
        BaseHttpSubscriber<T> httpSubscriber = flowAble
                .compose(RxUtil.getSchedulerFlowable())  // 转换线程
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
        mActivity = this;
    }

    protected abstract int getBaseLayout();

    /**
     * 在setContentView之前的调用方法，用于特殊的使用
     */
    @Override
    public void onBeforeCreateView() {
    }

    /**
     * 初始化状态栏
     */
    @Override
    public void initStatusBar() {
        // 设置状态栏
        StatusBarUtil.getInstance(this).setStatusColor(R.color.base_title_background_color);
    }

    /**
     * Activity初始化view
     */
    @Override
    public void initView() {
    }

    /**
     * 初始化点击事件
     */
    @Override
    public void initListener() {
    }

    /**
     * 初始化initData之后的操作，在某些场景中去使用
     */
    @Override
    public void initDataAfter() {
    }

    /**
     * 新建一个Intent，然后跳转到指定的界面
     *
     * @param cls 指定跳转的界面
     */
    protected void startActivity(Class<? extends Activity> cls) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
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
    public void onViewClick(View v) {
        // 在指定的间隔时间内是否做了双击
        // view的点击事件间隔
        int clickInterval = 500;
        boolean doubleClick = ClickUtil.isDoubleClick(clickInterval);
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

    @Override
    public String getTag() {
        return ClassUtil.getClassName(this);
    }

}
