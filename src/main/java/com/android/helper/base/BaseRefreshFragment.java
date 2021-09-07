package com.android.helper.base;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.android.helper.R;
import com.android.helper.base.refresh.BaseRefreshFooter;
import com.android.helper.base.refresh.BaseRefreshHeader;
import com.android.helper.base.refresh.BaseRefreshLayout;
import com.android.helper.httpclient.BaseException;
import com.android.helper.httpclient.BaseHttpSubscriber;
import com.android.helper.interfaces.HttpClientListener;
import com.android.helper.widget.BasePlaceholderView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;

/**
 * 带刷新工具的activity
 *
 * @param <T>  网络请求数据类型的对象类型
 * @param <T2> 数据列表的对象类型，如果是不带类表的情况，java可以使用object，Kotlin使用Any
 */
public abstract class BaseRefreshFragment<T, T2> extends BaseFragment implements HttpClientListener<T>, OnRefreshLoadMoreListener {

    protected FrameLayout mFlBaseRefreshContent;
    private BaseRefreshLayout mRefreshLayout;
    private BaseRefreshHeader mRefreshHeader;
    protected FrameLayout mRefreshContent;
    private BaseRefreshFooter mRefreshFooter;
    private BasePlaceholderView mPlaceholder;

    private int mPaceHolderIcon; // 站位图的图片
    private String mPlaceHolderTitle;// 站位图的title
    private String mPlaceHolderMsg;// 站位图的具体消息
    private String mPlaceHolderRefreshButton;// 站位图刷新的按钮内容
    private View.OnClickListener mRefreshListener;// 站位图刷新按钮的点击事件

    // 默认可以刷新
    private RefreshType mRefreshType = RefreshType.TYPE_REFRESH_LOAD_MORE;

    private int mPage;// 当前的分页
    private boolean isShowLoading;// 是否弹窗的条件
    protected final List<T2> mList = new ArrayList<>();// 数据集合
    protected Disposable disposable; // 请求的对象，用于取消网络请求使用

    /**
     * @return Call类型的返回对象，默认空实现
     */
    @Override
    public Call<T> getCall() {
        return null;
    }

    @Override
    public boolean filterForPage(T t) {
        return false;
    }

    /**
     * @return 默认分页的实现，如果需要可以重写
     */
    @Override
    public int pageSize() {
        return 10;
    }

    /**
     * @param map 分页参数的集合
     * @return 如果涉及到分页请求，则必须使用该方法
     */
    @Override
    public HashMap<String, Object> pageControl(HashMap<String, Object> map) {
        map.put("limit", pageSize());
        map.put("page", getPage());
        return map;
    }

    public enum RefreshType {
        TYPE_NONE, // 不执行任何的操作
        TYPE_REFRESH,// 只能刷新
        TYPE_REFRESH_LOAD_MORE// 既能刷新也能加载更多
    }

    @Override
    protected int getBaseLayout() {
        return R.layout.view_base_refresh;
    }

    @Override
    protected void onInitViewBefore(LayoutInflater inflater, View container) {
        super.onInitViewBefore(inflater, container);

        // refresh layout
        mRefreshLayout = (BaseRefreshLayout) container.findViewById(R.id.brl_base_refresh);
        // refresh  header
        mRefreshHeader = (BaseRefreshHeader) container.findViewById(R.id.brh_base_refresh_header);
        // content
        mRefreshContent = (FrameLayout) container.findViewById(R.id.fl_base_refresh_content);
        // refresh footer
        mRefreshFooter = (BaseRefreshFooter) container.findViewById(R.id.brf_base_refresh_footer);
        // BasePlaceholderView
        mPlaceholder = (BasePlaceholderView) container.findViewById(R.id.bpv_base_placeholder);

        // 把继承baseRefresh的基类放入到 content中去
        inflater.inflate(getRefreshLayout(), mRefreshContent);
    }

    protected abstract int getRefreshLayout();

    /**
     * 此方法必须在  {@link BaseRefreshActivity#initListener()}前面去调用，否则不会执行
     *
     * @param refreshType 设置刷新的类型
     */
    public void setRefreshType(RefreshType refreshType) {
        mRefreshType = refreshType;
    }

    @Override
    public void initListener() {
        super.initListener();

        // 初始化刷新事件
        initRefreshListener();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 清空数据集合
        mList.clear();
        // 页数设置为初始值
        setPage(0);
        // 请求数据
        requestData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 页数自增加
        int page = getPage();
        setPage(++page);
        requestData();
    }

    /**
     * 刷新事件类型
     */
    protected void initRefreshListener() {
        // 刷新布局
        if (mRefreshType != null) {
            if (mRefreshType == RefreshType.TYPE_REFRESH) {
                mRefreshLayout.setEnableRefresh(true);
                mRefreshLayout.setOnRefreshListener(this);
            } else if (mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE) {
                mRefreshLayout.setEnableRefresh(true);
                mRefreshLayout.setOnRefreshListener(this);
                mRefreshLayout.setEnableLoadMore(true);
                mRefreshLayout.setOnLoadMoreListener(this);
            } else if (mRefreshType == RefreshType.TYPE_NONE) {
                mRefreshLayout.setEnableRefresh(false);
                mRefreshLayout.setEnableLoadMore(false);
            }
        }
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        this.mPage = page;
    }

    @Override
    public void initData() {
        // 开始请求数据
        disposable = requestData();
    }

    @Override
    public boolean filterForDialog() {
        // 默认的话，进来的时候第一次开始弹窗，其他时候不弹窗
        return !isShowLoading;
    }

    @Override
    public void onHttpStart() {
    }

    @Override
    public Disposable requestData() {
        // 返回请求的对象
        Disposable disposable = null;

        Flowable<T> apiService = getApiService();
        if (apiService != null) {
            disposable = net(apiService, new BaseHttpSubscriber<T>() {
                @Override
                protected void onStart() {
                    super.onStart();
                    // 回调子页面的处理事件
                    onHttpStart();

                    if (filterForDialog()) {
                        // 开始 弹窗

                    }
                }

                @Override
                public void onSuccess(T t) {
                    // 标记已经加载过首次了
                    isShowLoading = true;

                    if (filterForPage(t)) {
                        // 加载完所有的数据
                        mRefreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                    }
                    BaseRefreshFragment.this.onSuccess(t);
                }

                @Override
                public void onFailure(BaseException e) {

                    BaseRefreshFragment.this.onFailure(e);
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    // 回调子页面的处理事件
                    onHttpComplete();

                    // 1： todo 结束弹窗

                    // 2：关闭刷新和加载
                    mRefreshLayout.finishLoadMore();
                    mRefreshLayout.finishRefresh();
                }
            });
        }
        return disposable;
    }

    @Override
    public void onHttpComplete() {

    }

}
