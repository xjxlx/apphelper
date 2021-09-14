package com.android.helper.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.android.helper.R;
import com.android.helper.httpclient.BaseHttpSubscriber;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.TagListener;
import com.android.helper.interfaces.UIListener;
import com.android.helper.interfaces.listener.HttpManagerListener;
import com.android.helper.utils.ClassUtil;
import com.android.helper.utils.ClickUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.TextViewUtil;
import com.android.helper.utils.statusBar.StatusBarUtil;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 最基层的Activity
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, HttpManagerListener, TagListener, UIListener {

    public FragmentActivity mContext;
    private int mClickInterval = 500;// view的点击事件间隔

    /*
     *此处不能写成静态的，否则就会和使用RxManager一样了
     */
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected View.OnClickListener mBackClickListener = null;// 返回的点击事件
    protected TextView mTvBaseTitle;
    protected LinearLayout mLlBaseTitleBack;
    protected LinearLayout mRlBaseTitleRoot;
    protected ImageView mIvBaseTitleBack;
    protected TextView mTvBaseTitleBackTitle;
    protected FrameLayout mFlBaseTitleRightParent;
    protected TextView mTvBaseTitleRightTitle;
    protected FrameLayout mFlBaseTitleContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        LogUtil.e("当前的页面：Activity：--->  " + getClass().getName());

        // 在onCreate之前调用的方法
        onBeforeCreateView();

        int baseLayout = getBaseLayout();
        if (baseLayout != 0) {
            setContentView(baseLayout);
        }

        // 设置状态栏
        StatusBarUtil.getInstance(mContext).setStatusColor(R.color.base_title_background_color);

        initView();
        initListener();
        initData();
    }

    @Override
    public void onBeforeCreateView() {

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
        mContext = this;
    }

    protected abstract int getBaseLayout();

    @Override
    public void initView() {
        // root
        mRlBaseTitleRoot = findViewById(R.id.rl_base_title_root);
        // left parent
        mLlBaseTitleBack = findViewById(R.id.ll_base_title_back);
        // lift icon
        mIvBaseTitleBack = findViewById(R.id.iv_base_title_back);
        // left title
        mTvBaseTitleBackTitle = findViewById(R.id.tv_base_title_back_title);

        // right parent
        mFlBaseTitleRightParent = findViewById(R.id.fl_base_title_right_parent);
        // right  title
        mTvBaseTitleRightTitle = findViewById(R.id.tv_base_title_right_title);
        // content
        mFlBaseTitleContent = findViewById(R.id.fl_base_title_content);
    }

    @Override
    public void initListener() {

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
        boolean doubleClick = ClickUtil.isDoubleClick(mClickInterval);
        if (doubleClick) {
            LogUtil.e("点击速度过快了！");
        } else {
            onClick(v);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 返回键
        if (id == R.id.ll_base_title_back) {
            if (mBackClickListener != null) {
                // 处理额外的点击事件
                mBackClickListener.onClick(v);
            } else {
                // 直接返回
                finish();
            }
        }
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

    /**
     * 给activity设置title
     *
     * @param title 标题内容
     */
    protected void setTitleContent(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTvBaseTitle = findViewById(R.id.tv_base_title);
            TextViewUtil.setText(mTvBaseTitle, title);
        }
    }

    /**
     * @param showTitle 隐藏或者显示title
     */
    protected void showTitle(boolean showTitle) {
        if (mRlBaseTitleRoot != null) {
            mRlBaseTitleRoot.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置返回按钮的点击事件
     *
     * @param listener 返回的点击事件
     */
    protected void setBackClickListener(View.OnClickListener listener) {
        this.mBackClickListener = listener;
    }

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 右侧的title
     */
    protected void setRightTitle(String rightTitle) {
        if (TextUtils.isEmpty(rightTitle)) {
            return;
        }

        int visibility = mTvBaseTitleRightTitle.getVisibility();
        if (visibility != View.VISIBLE) {
            mTvBaseTitleRightTitle.setVisibility(View.VISIBLE);
        }

        TextViewUtil.setText(mTvBaseTitleRightTitle, rightTitle);
    }

    /**
     * 设置右侧的标题
     *
     * @param rightTitle 标题
     * @param color      颜色
     * @param size       大小
     */
    protected void setRightTitle(String rightTitle, int color, int size) {
        setRightTitle(rightTitle);
        if (color != 0) {
            mTvBaseTitleRightTitle.setTextColor(color);
        }
        if (size > 0) {
            mTvBaseTitleRightTitle.setTextSize(size);
        }
    }

    /**
     * 设置右侧的点击事件
     *
     * @param listener 右侧的点击事件
     */
    protected void setRightTitleClickListener(View.OnClickListener listener) {
        if (listener != null) {
            mFlBaseTitleRightParent.setOnClickListener(listener);
        }
    }

    /**
     * 页面的返回
     *
     * @param backId    返回的id
     * @param listeners 返回的点击事件
     */
    public void setTitleBack(int backId, View.OnClickListener listeners) {
        if (backId != 0) {
            if (listeners != null) {
                findViewById(backId).setOnClickListener(listeners);
            } else {
                setTitleBack(backId);
            }
        }
    }

    /**
     * 页面的返回
     *
     * @param backId 返回的id
     */
    public void setTitleBack(int backId) {
        if (backId != 0) {
            findViewById(backId).setOnClickListener(v -> finish());
        }
    }

    /**
     * 设置标题
     *
     * @param titleId      标题控件的id
     * @param titleContent 标题的内容
     */
    public void setTitleContent(int titleId, String titleContent) {
        if (titleId != 0) {
            View view = findViewById(titleId);
            if (view instanceof TextView) {
                TextView titleView = (TextView) view;
                TextViewUtil.setText(titleView, titleContent);
            }
        }
    }

}
