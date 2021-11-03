package com.android.helper.utils;

import androidx.annotation.NonNull;

import com.android.helper.httpclient.RxUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author : 流星
 * @CreateDate: 2021/11/3-4:41 下午
 * @Description: 刷新的工具类
 */
public abstract class RefreshUtil<T> implements OnRefreshListener, OnLoadMoreListener {

    private int mPage = 0;// 数据查询的页数

    // 数据请求的对象
    public abstract Observable<T> getObservable();

    // 接口回调的数据
    private CallBackListener<T> mCallBackListener;

    private SmartRefreshLayout mRefreshLayout;
    private RefreshHeader mRefreshHeader;
    private RefreshFooter mRefreshFooter;
    private RefreshType mRefreshType = RefreshType.TYPE_REFRESH;
    private boolean mAutoLoad = true;// 是否自动加载
    private boolean isFirstLoad = true;// 是否是首次加载，默认是首次，只要加载过数据，就设置非首次加载数据

    public enum RefreshType {
        TYPE_NONE, // 不执行任何的操作
        TYPE_REFRESH,// 只能刷新
        TYPE_REFRESH_LOAD_MORE// 既能刷新也能加载更多
    }

    public RefreshUtil(SmartRefreshLayout refreshLayout) {
        this.mRefreshLayout = refreshLayout;
    }

    public RefreshUtil<T> setRefreshHeader(RefreshHeader refreshHeader) {
        this.mRefreshHeader = refreshHeader;
        return this;
    }

    public RefreshUtil<T> setRefreshFooter(RefreshFooter refreshFooter) {
        this.mRefreshFooter = refreshFooter;
        return this;
    }

    /**
     * @param autoLoad 是否自动刷新，默认自动刷新
     */
    public RefreshUtil<T> setAutoRefresh(boolean autoLoad) {
        this.mAutoLoad = autoLoad;
        return this;
    }

    /**
     * @param currentPage 设置从第几页开始请求数据，这个是为了有些傻逼后台，不从0页开始查数据，非要从指定的页面去查数据，默认是从0页开始查数据
     */
    public RefreshUtil<T> setFromPage(int currentPage) {
        mPage = currentPage;
        return this;
    }

    /**
     * @return 获取当前的页数，可以把这个方法当做页数的值去使用
     */
    public int getCurrentPage() {
        return mPage;
    }

    /**
     * @return 是否是首次加载数据
     */
    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    /**
     * 开始执行操作
     */
    public void execute() {
        initRefreshListener();
    }

    /**
     * @param refreshType 设置刷新的类型
     */
    public RefreshUtil<T> setRefreshType(RefreshType refreshType) {
        mRefreshType = refreshType;
        return this;
    }

    public RefreshUtil<T> setCallBackListener(CallBackListener<T> callBackListener) {
        this.mCallBackListener = callBackListener;
        return this;
    }

    /**
     * 刷新事件类型
     */
    private void initRefreshListener() {
        // 刷新布局
        if (mRefreshLayout != null) {
            if (mRefreshType == RefreshType.TYPE_REFRESH) {
                mRefreshLayout.setEnableRefresh(true);
                mRefreshLayout.setEnableLoadMore(false);
                mRefreshLayout.setOnRefreshListener(this);
            } else if (mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE) {
                mRefreshLayout.setEnableRefresh(true);
                mRefreshLayout.setEnableLoadMore(true);
                mRefreshLayout.setOnRefreshListener(this);
                mRefreshLayout.setOnLoadMoreListener(this);
            } else if (mRefreshType == RefreshType.TYPE_NONE) {
                mRefreshLayout.setEnableRefresh(false);
                mRefreshLayout.setEnableLoadMore(false);
            }

            // 设置自动刷新
            if (mAutoLoad) {
                mRefreshLayout.autoRefresh();
            }
        }
    }

    private void clientHttp() {
        getObservable()
                .compose(RxUtil.getSchedulerObservable())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        if (mCallBackListener != null) {
                            mCallBackListener.start();
                        }
                        isFirstLoad = false;
                    }

                    @Override
                    public void onNext(@NotNull T t) {
                        if (mRefreshLayout != null) {
                            mRefreshLayout.finishRefresh();
                            mRefreshLayout.finishLoadMore();
                        }

                        if (mCallBackListener != null) {
                            mCallBackListener.onSuccess(t);
                        }

                        // 这里可以做更多的事情，例如：是否是最后一页，如果是最后一页，需要调用{ finishLoadMoreWithNoMoreData()方法 }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (mRefreshLayout != null) {
                            mRefreshLayout.finishRefresh();
                            mRefreshLayout.finishLoadMore();
                        }

                        if (mCallBackListener != null) {
                            mCallBackListener.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mCallBackListener != null) {
                            mCallBackListener.onComplete();
                        }
                    }
                });
    }

    /**
     * 刷新结束
     */
    protected void finishRefresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
    }

    /**
     * 结束加载更多
     */
    protected void finishLoadMore() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadMore();
        }
    }

    /**
     * 没有更多的数据
     */
    protected void finishLoadMoreWithNoMoreData() {
        // //完成加载并标记没有更多数据 1.0.4
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        mPage = 0;
        // 恢复没有更多数据的原始状态
        if (mRefreshLayout != null) {
            mRefreshLayout.resetNoMoreData();
        }

        // 自动加载
        clientHttp();
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        ++mPage;

        // 自动加载
        clientHttp();
    }

    public interface CallBackListener<T> {
        void start();

        void onSuccess(@NotNull T t);

        void onError(@NotNull Throwable e);

        void onComplete();
    }
}
