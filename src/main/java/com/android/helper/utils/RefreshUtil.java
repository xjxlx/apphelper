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

import java.util.List;

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
    private int mPageSiZe = 20;// 每页查询的数量，默认是20条数据
    private T mData;// 当前页面的数据

    // 数据请求的对象
    public abstract Observable<T> getObservable();

    // 接口回调的数据
    private CallBackListener<T> mCallBackListener;

    private final SmartRefreshLayout mRefreshLayout;
    private RefreshHeader mRefreshHeader;
    private RefreshFooter mRefreshFooter;
    private RefreshType mRefreshType = RefreshType.TYPE_REFRESH;
    private boolean mAutoLoad = true;       // 是否自动加载
    private boolean isFirstLoad = true;     // 是否是首次加载，默认是首次，只要加载过数据，就设置非首次加载数据
    private boolean isRefresh = true;       // 是否是刷新的状态，用于控制数据是添加还是在更新

    public enum RefreshType {
        TYPE_NONE, // 不执行任何的操作
        TYPE_REFRESH,// 只能刷新
        TYPE_REFRESH_LOAD_MORE// 既能刷新也能加载更多
    }

    /**
     * @param refreshLayout 刷新布局的对象
     */
    public RefreshUtil(SmartRefreshLayout refreshLayout) {
        this.mRefreshLayout = refreshLayout;
    }

    /**
     * @param refreshHeader 刷新头的对象
     * @return 设置刷新头
     */
    public RefreshUtil<T> setRefreshHeader(RefreshHeader refreshHeader) {
        this.mRefreshHeader = refreshHeader;
        return this;
    }

    /**
     * @param refreshFooter 刷新脚的对象
     * @return 设置刷新脚
     */
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
     * @param currentPage 从第几页开始刷新
     * @return 设置从第几页开始请求数据，这个是为了有些傻逼后台，不从0页开始查数据，非要从指定的页面去查数据，默认是从0页开始查数据
     */
    public RefreshUtil<T> setFromPage(int currentPage) {
        mPage = currentPage;
        return this;
    }

    /**
     * @param size 每页查询的数量
     * @return 设置每页查询的数据的条数，默认是一次查询二十条数据
     */
    public RefreshUtil<T> setPageSize(int size) {
        mPageSiZe = size;
        return this;
    }

    /**
     * @return 获取当前页面查询数据的条数
     */
    public int getPageSiZe() {
        return mPageSiZe;
    }

    /**
     * <p>
     * 这个方法，用起来巨傻逼，还要每次自己去判断逻辑，检测是不是最后一个数据，如果后期数据结构能够统一的话，可以重新构造一个方法，
     * 在这里直接判断好就行，就不用每次去手动判断了
     * </p>
     *
     * @return 用来控制是否已经没有更多的数据了, 默认是还有数据
     */
    private boolean isNoMoreData() {
        boolean isNoMoreData = false;   // 是否还有跟多的数据
        if (mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE) { // 只有在上拉加载的时候，判断这个才有意义，避免数据的繁琐逻辑
            List<?> list = setNoMoreData(mData);
            if (list == null) {
                /*
                 * 数据为空的时候，设置为没有更多数据了
                 */
                isNoMoreData = true;
            } else {
                /*
                 * 1:数据不为空，数据为0的时候，设置为没有更多数据
                 * 2:数据不为空，数据长度小于每页查询页数长度的时候，设置为没有更多数据
                 */
                if ((list.size() == 0) || (list.size() < getPageSiZe())) { //
                    isNoMoreData = true;
                }
            }
        }
        return isNoMoreData;
    }

    /**
     * @param t 当前请求下来的数据对象
     * @return 返回一个实际使用到的集合列表，去判断还有没有跟多的数据，这个方法只适合使用列表型的数据
     */
    public List<?> setNoMoreData(T t) {
        return null;
    }

    /**
     * @return 获取当前页面的数据
     */
    public T getCurrentData() {
        return mData;
    }

    /**
     * 设置当前页面的数据，仅供内部使用
     *
     * @param data 仅限于当前页面的数据
     */
    private void setCurrentData(T data) {
        mData = data;
    }

    /**
     * @return 获取当前的页数，可以把这个方法当做页数的值去使用
     */
    public int getCurrentPage() {
        return mPage;
    }

    /**
     * @return 判定是否是首次加载数据，用来处理一些逻辑
     */
    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    /**
     * 单独刷新一次，刷新的是第0页
     */
    public void refresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    /**
     * 开始执行操作刷新的操作
     */
    public RefreshUtil<T> execute() {
        initRefreshListener();
        return this;
    }

    /**
     * @param refreshType 设置刷新的类型，默认是单独刷新
     *                    单独刷新，使用：{@link RefreshType#TYPE_REFRESH}
     *                    刷新 + 下拉加载更多，使用{@link RefreshType#TYPE_REFRESH_LOAD_MORE}
     *                    不做刷新也不做加载更多，使用：{@link RefreshType#TYPE_REFRESH_LOAD_MORE}
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
     * @return 当前是刷新还是下拉加载更多
     */
    public boolean isRefresh() {
        return isRefresh;
    }

    /**
     * 初始化刷新事件
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

    /**
     * 网络请求
     */
    private void clientHttp() {
        getObservable()
                .compose(RxUtil.getSchedulerObservable())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        RefreshUtil.this.onStart();
                        isFirstLoad = false;
                    }

                    @Override
                    public void onNext(@NotNull T t) {
                        if (mRefreshLayout != null) {
                            mRefreshLayout.finishRefresh();
                            mRefreshLayout.finishLoadMore();
                        }

                        /*
                         * 这里可以做更多的事情，例如：是否是最后一页，如果是最后一页，
                         * 需要调用{@link RefreshUtil#finishLoadMoreWithNoMoreData()}
                         * 去通知刷新对象，停止刷新的操作
                         */

                        // 设置当前页面请求下来的数据
                        setCurrentData(t);

                        if (mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE) {
                            boolean noMoreData = isNoMoreData();
                            LogUtil.e("加载更多：" + noMoreData);
                            if (noMoreData) {
                                finishLoadMoreWithNoMoreData();
                            }
                        }

                        if (mCallBackListener != null) {
                            mCallBackListener.onSuccess(t);
                        }
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
                        RefreshUtil.this.onComplete();
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
        isRefresh = true;

        // 只有在没有跟多数据的情况下，才回去判断重置状态
        if (isNoMoreData()) {
            // 恢复没有更多数据的原始状态
            if (mRefreshLayout != null) {
                mRefreshLayout.resetNoMoreData();
            }
        }

        // 自动加载
        clientHttp();
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        ++mPage;
        isRefresh = false;

        // 自动加载
        clientHttp();
    }

    /**
     * 预置的网络刷新开始的回调，因为有些地方是在刷新的布局头上显示的动画，所以没有必要所有的接口都重写这个方法
     * 这里预置一个方法，在有需要的时候去进行重写
     */
    public void onStart() {
    }

    /**
     * 预置的网络刷新结束的回调，因为有些地方是在成功和失败的时候已经处理了逻辑，所以没有必要所有的接口都重写这个方法
     * 这里预置一个方法，在有需要的时候去进行重写
     */
    public void onComplete() {
    }

    public interface CallBackListener<T> {
        void onSuccess(@NotNull T t);

        void onError(@NotNull Throwable e);
    }
}
