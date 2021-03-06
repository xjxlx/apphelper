package android.helper.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.helper.httpclient.BaseHttpSubscriber;
import android.helper.httpclient.RxUtil;
import android.helper.interfaces.listener.HttpManagerListener;
import android.helper.utils.ClickUtil;
import android.helper.utils.LogUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * fragment的基类，全面使用viewBinding
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener, HttpManagerListener {

    /*
     *此处不能写成静态的，否则就会和使用RxManager一样了
     */
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    protected FragmentActivity mContext;
    protected View mRootView;
    protected String TAG;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentActivity) {
            mContext = (FragmentActivity) activity;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            mContext = (FragmentActivity) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TAG = getClass().getSimpleName();
        LogUtil.e("当前的页面：Fragment：--->  " + TAG);

        int layout = getBaseLayout();
        if (layout != 0) {
            mRootView = inflater.inflate(layout, container, false);
        }

        onInitViewBefore(inflater, mRootView);

        initView(mRootView);
        initListener();

        // 获取布局资源文件
        return mRootView;
    }

    protected abstract int getBaseLayout();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
    }

    protected void initListener() {
    }

    protected void onInitViewBefore(LayoutInflater inflater, View container) {
    }

    protected abstract void initView(View view);

    protected abstract void initData();

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

    /**
     * 设置view的点击事件
     *
     * @param ids id的数组
     */
    protected void setViewClickListener(int... ids) {
        if (mRootView != null && ids != null && ids.length > 0) {
            for (int id : ids) {
                View view = mRootView.findViewById(id);
                if (view != null) {
                    view.setOnClickListener(this::onViewClick);
                }
            }
        }
    }

    protected void setViewClickListener(View... array) {
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

    protected void startActivity(Class<? extends Activity> cls) {
        Intent intent = new Intent(mContext, cls);
        startActivity(intent);
    }

    protected void startActivity(Intent intent, Class<? extends Activity> cls) {
        if (intent != null && cls != null) {
            startActivity(intent, cls);
        }
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
    public void onDestroy() {
        super.onDestroy();
        // 销毁了rx2
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }

}
