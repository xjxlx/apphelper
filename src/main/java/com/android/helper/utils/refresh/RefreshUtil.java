package com.android.helper.utils.refresh;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.base.recycleview.RecycleViewFrameWork;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;
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
 *               <ol>
 *               使用说明： 使用该工具类，必然是在请求接口的时候使用，否则没有任何意义
 *               <p>
 *               使用文档： 一：T 泛型，这个是数据请求数据的类型 二：请求页数，默认是从第0页开始的，如果有特殊的需求，调用方法{@link RefreshUtil#setFromPage(int)}去设置
 *               三：请求数量，默认的是指是20条，如果有其他需求，调用方法{@link RefreshUtil#setPageSize(int)}去做设置
 *               四：刷新类型，默认的刷新类型是既能下拉刷新，也能上拉加载，如果要改变刷新类型，调用方法{@link RefreshUtil#setRefreshType(RefreshType)} 五：构造方法，
 *               1：需要传入参数： ①：SmartRefreshLayout 刷新的对象，这个是必填参数 ②：Activity ：上下文的对象，必填参数
 *               ③：RecycleViewFrameWork：适配器的对象，这个是针对列表使用的，传入了该对象，就可以主动去设置接口异常的占位图
 *               2：抽象方法{@link RefreshUtil#getObservable()} 这个方法是用来返回一个请求对象使用的，只要调用了构造方法，就必然要实现这个方法，
 *               值得注意的是，如果有分页的话，需要调用获取页数的方法{@link RefreshUtil#getCurrentPage()}去动态变化页数
 *               3：抽象方法{@link RefreshUtil#setNoMoreData(Object)}这个是用来检测是否有更多数据的方法，如果数据类型能统一的话，这个方法可以省略，目前
 *               接口不统一，需要手动去返回当前接口返回的集合对象，用于判定是否还有更多的数据 六：刷新方法，调用{@link RefreshUtil#refresh()},这里刷新的是第一页的数据
 *               七：数据回调，调用{@link RefreshUtil#setCallBackListener(RefreshCallBack)}，会把接口获取到的数据返回出去
 *               八：调用刷新流程：使用方法{@link RefreshUtil#execute()}
 *               <p>
 *               使用例子： RefreshUtil mRefreshUtil = new RefreshUtil<Page<List<UserCollection>>>(this, sr) {
 * @Override public Observable<Page<List<UserCollection>>> getObservable() { return
 *           RetrofitUtils.getAPIInstance().create(TipAPI.class).getCollectListV2(id, getCurrentPage()); }
 *           <p>
 * @Override public List<?> setNoMoreData(Page<List<UserCollection>> listPage) { if (listPage != null) { return
 *           listPage.getContent(); } return null; } } .setCallBackListener(new
 *           RefreshCallBack<Page<List<UserCollection>>>() {
 * @Override public void onSuccess(@NotNull Page<List<UserCollection>> listPage) { if (listPage.getContent() != null) {
 *           adapter.setList(listPage.getContent(), mRefreshUtil.isRefresh()); } }
 *           <p>
 * @Override public void onError(@NotNull Throwable e) { ToastUtil.show(e.getMessage()); } }) .execute();
 * 
 *           // demo
 *           mRefreshUtil = object : RefreshUtil<ConsecrateBean>(fragment, refreshLayout) { // 用来返回接口的数据 override fun
 *           getObservable(): Observable<ConsecrateBean> { val parameter = hashMapOf<String, Any>() parameter["pageNum"]
 *           = currentPage parameter["pageSize"] = 10 parameter["searchWords"] = searchWords parameter["tabletStatus"] =
 *           tabletStatus return RetrofitHelper .create(ApiInterface::class.java)
 *           .getConsecrateList2(RetrofitHelper.createBodyForMap(parameter)) }
 *
 *           // 告诉控制器当前是否还有数据，必须喝下面的setPageSize方法配合 override fun setNoMoreData(t: ConsecrateBean?):
 *           List<ConsecrateBean.Data.Row>? { if (t?.data?.rows != null) { return t.data.rows } return null } }
 *           .setCallBackListener(object : RefreshCallBack<ConsecrateBean>() { override fun onStart() { super.onStart()
 *           this@ControllerConsecrate.onStart() }
 *
 *           override fun onSuccess(refreshUtil: RefreshUtil<ConsecrateBean>, t: ConsecrateBean) {
 *           this@ControllerConsecrate.onRefreshSuccess(t, refreshUtil.isRefresh) }
 *
 *           override fun onError(e: Throwable) { this@ControllerConsecrate.onFailure(e) } }) .setFromPage(1)
 *           .setPageSize(10) ..execute()
 *           </ol>
 */
public abstract class RefreshUtil<T> implements OnRefreshListener, OnLoadMoreListener, BaseLifecycleObserver {

    private int mPage = 0; // 当前数据查询的页数
    private int mOriginalPage = mPage; // 最开始的页数，默认和当前查询的页数相同
    private int mPageSiZe = 20; // 每页查询的数量，默认是20条数据
    private T mData; // 当前页面的数据

    // 接口回调的数据
    private RefreshCallBack<T> mCallBack;
    private SmartRefreshLayout mRefreshLayout;
    private RefreshHeader mRefreshHeader;
    private RefreshFooter mRefreshFooter;
    private RefreshType mRefreshType = RefreshType.TYPE_REFRESH_LOAD_MORE;
    private boolean mAutoLoad = true; // 是否自动加载
    private boolean isFirstLoad = true; // 是否是首次加载，默认是首次，只要加载过数据，就设置非首次加载数据
    private boolean isRefresh = true; // 是否是刷新的状态，用于控制数据是添加还是在更新
    private Disposable mDisposable; // 请求数据的对象，用于取消数据的请求
    private RecycleViewFrameWork<?, ?> mAdapter; // RecycleView的适配器

    /**
     * @param activity
     *            activity的对象
     * @param refreshLayout
     *            刷新布局的对象
     */
    public RefreshUtil(FragmentActivity activity, SmartRefreshLayout refreshLayout) {
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);
        }
        this.mRefreshLayout = refreshLayout;
    }

    /**
     * @param fragment
     *            fragment的对象
     * @param refreshLayout
     *            刷新布局的对象
     */
    public RefreshUtil(Fragment fragment, SmartRefreshLayout refreshLayout) {
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
        }
        this.mRefreshLayout = refreshLayout;
    }

    public RefreshUtil<T> setAdapter(RecycleViewFrameWork<?, ?> adapter) {
        this.mAdapter = adapter;
        return this;
    }

    /**
     * @param refreshHeader
     *            刷新头的对象
     * @return 设置刷新头
     */
    public RefreshUtil<T> setRefreshHeader(RefreshHeader refreshHeader) {
        this.mRefreshHeader = refreshHeader;
        if (mRefreshLayout != null && mRefreshHeader != null) {
            mRefreshLayout.setRefreshHeader(mRefreshHeader);
        }
        return this;
    }

    /**
     * @param refreshFooter
     *            刷新脚的对象
     * @return 设置刷新脚
     */
    public RefreshUtil<T> setRefreshFooter(RefreshFooter refreshFooter) {
        this.mRefreshFooter = refreshFooter;
        if (mRefreshLayout != null && mRefreshFooter != null) {
            mRefreshLayout.setRefreshFooter(mRefreshFooter);
        }
        return this;
    }

    /**
     * @param autoLoad
     *            是否自动刷新，默认自动刷新
     */
    public RefreshUtil<T> setAutoRefresh(boolean autoLoad) {
        this.mAutoLoad = autoLoad;
        return this;
    }

    /**
     * @param size
     *            每页查询的数量
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
     * 这个方法，用起来巨傻逼，还要每次自己去判断逻辑，检测是不是最后一个数据，如果后期数据结构能够统一的话，可以重新构造一个方法， 在这里直接判断好就行，就不用每次去手动判断了
     * </p>
     *
     * @return 用来控制是否已经没有更多的数据了, 默认是还有数据
     */
    private boolean isNoMoreData() {
        boolean isNoMoreData = false; // 是否还有跟多的数据
        if ((mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE) || (mRefreshType == RefreshType.TYPE_LOAD_MORE)) {
            // 只有在上拉加载的时候，判断这个才有意义，避免数据的繁琐逻辑
            if (mData != null) {
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
        }
        return isNoMoreData;
    }

    /**
     * @param t
     *            当前请求下来的数据对象
     * @return 返回一个实际使用到的集合列表，去判断还有没有跟多的数据，这个方法只适合使用列表型的数据
     */
    public abstract List<?> setNoMoreData(T t);

    // 数据请求的对象
    public abstract Observable<T> getObservable();

    /**
     * @return 获取当前页面的数据
     */
    public T getCurrentData() {
        return mData;
    }

    /**
     * 设置当前页面的数据，仅供内部使用
     *
     * @param data
     *            仅限于当前页面的数据
     */
    private void setCurrentData(T data) {
        mData = data;
    }

    /**
     * @param fromPage
     *            从第几页开始刷新
     * @return 设置从第几页开始请求数据，这个是为了有些傻逼后台，不从0页开始查数据，非要从指定的页面去查数据，默认是从0页开始查数据
     */
    public RefreshUtil<T> setFromPage(int fromPage) {
        mPage = fromPage;
        mOriginalPage = fromPage;
        return this;
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
     * @param refreshType
     *            设置刷新的类型，默认是可以下拉刷新也可以上拉加载更多 单独刷新，使用：{@link RefreshType#TYPE_REFRESH} 刷新 +
     *            下拉加载更多，使用{@link RefreshType#TYPE_REFRESH_LOAD_MORE}
     *            不做刷新也不做加载更多，使用：{@link RefreshType#TYPE_REFRESH_LOAD_MORE}
     */
    public RefreshUtil<T> setRefreshType(RefreshType refreshType) {
        mRefreshType = refreshType;
        return this;
    }

    /**
     * @return 设置刷新数据的监听对象
     */
    public RefreshUtil<T> setCallBackListener(RefreshCallBack<T> callBack) {
        this.mCallBack = callBack;
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
            if (mRefreshType == RefreshType.TYPE_REFRESH) { // 只能刷新
                mRefreshLayout.setEnableRefresh(true);
                mRefreshLayout.setEnableLoadMore(false);
                mRefreshLayout.setOnRefreshListener(this);
            } else if (mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE) { // 可以刷新，也可以加载
                mRefreshLayout.setEnableRefresh(true);
                mRefreshLayout.setEnableLoadMore(true);
                mRefreshLayout.setOnRefreshListener(this);
                mRefreshLayout.setOnLoadMoreListener(this);
            } else if (mRefreshType == RefreshType.TYPE_LOAD_MORE) { // 只能加载跟多
                mRefreshLayout.setEnableRefresh(false);
                mRefreshLayout.setEnableLoadMore(true);
            } else if (mRefreshType == RefreshType.TYPE_NONE) { // 不能加载也不能刷新
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
        Observable<T> observable = getObservable();
        if (observable != null) {
            observable.compose(RxUtil.getSchedulerObservable()).subscribe(new Observer<T>() {
                @Override
                public void onSubscribe(@NotNull Disposable d) {
                    mDisposable = d;

                    if (mCallBack != null) {
                        mCallBack.onStart();
                    }
                }

                @Override
                public void onNext(@NotNull T t) {
                    setDataSuccess(t);
                }

                @Override
                public void onError(@NotNull Throwable e) {
                    setDataError(e);
                }

                @Override
                public void onComplete() {
                    if (mCallBack != null) {
                        mCallBack.onComplete();
                    }
                    isFirstLoad = false;
                }
            });
        }
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
        isRefresh = true;
        mPage = mOriginalPage;

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
     * 数据成功的操作
     *
     * @param t
     *            请求的数据
     */
    public void setDataSuccess(T t) {

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

        if (mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE || mRefreshType == RefreshType.TYPE_LOAD_MORE) {
            boolean noMoreData = isNoMoreData();
            LogUtil.e("加载更多：" + noMoreData);
            if (noMoreData) {
                finishLoadMoreWithNoMoreData();
            }
        }

        if (mCallBack != null) {
            mCallBack.onSuccess(this, t);
        }
    }

    /**
     * 数据异常的操作
     *
     * @param throwable
     *            异常的对象
     */
    public void setDataError(Throwable throwable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
        }

        if (mCallBack != null) {
            mCallBack.onError(throwable);
        }

        // 接口错误的回调
        if (mAdapter != null) {
            mAdapter.setErrorHttpClient(RefreshUtil.this);
        }
    }

    @Override
    public void onCreate() {

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
        // 页面销毁的时候，去切断网络的连接，避免异常
        if ((mDisposable != null) && (!mDisposable.isDisposed())) {
            mDisposable.dispose();
            mDisposable = null;
            LogUtil.e("销毁了刷新的接口对象！");
        }

        if (mRefreshLayout != null) {
            mRefreshLayout = null;
        }

        if (mCallBack != null) {
            mCallBack = null;
        }
    }

}
