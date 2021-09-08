package com.android.helper.base;

import android.view.View;
import android.widget.FrameLayout;

import com.android.helper.R;
import com.android.helper.base.refresh.BaseRefreshFooter;
import com.android.helper.base.refresh.BaseRefreshHeader;
import com.android.helper.base.refresh.BaseRefreshLayout;
import com.android.helper.widget.BasePlaceholderView;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * 带刷新工具的activity
 */
public abstract class BaseRefreshActivity extends BaseActivity implements OnRefreshLoadMoreListener {

    protected View mBaseRefreshLayout;

    private BaseRefreshLayout mBrlBaseRefresh;
    private BaseRefreshHeader mBrhBaseRefreshHeader;
    private BaseRefreshFooter mBrfBaseRefreshFooter;
    private FrameLayout mFlBaseRefreshTopContent;
    protected FrameLayout mBaseRefreshContent;
    private FrameLayout mFlBaseRefreshBottomContent;

    private BasePlaceholderView mBpvBasePlaceholder;

    private int mPaceHolderIcon; // 站位图的图片
    private String mPlaceHolderTitle;// 站位图的title
    private String mPlaceHolderMsg;// 站位图的具体消息
    private String mPlaceHolderRefreshButton;// 站位图刷新的按钮内容
    private View.OnClickListener mRefreshListener;// 站位图刷新按钮的点击事件

    // 默认可以刷新
    private RefreshType mRefreshType = RefreshType.TYPE_REFRESH;

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
    public void onBeforeCreateView() {
        super.onBeforeCreateView();

        // refresh layout
        mBrlBaseRefresh = (BaseRefreshLayout) findViewById(R.id.brl_base_refresh);
        // refresh  header
        mBrhBaseRefreshHeader = (BaseRefreshHeader) findViewById(R.id.brh_base_refresh_header);
        // 顶部固定不动的view
        mFlBaseRefreshTopContent = findViewById(R.id.fl_base_refresh_top_content);
        // content
        mBaseRefreshContent = (FrameLayout) findViewById(R.id.fl_base_refresh_content);
        // 底部固定不动的view
        mFlBaseRefreshBottomContent = findViewById(R.id.fl_base_refresh_bottom_content);
        // refresh footer
        mBrfBaseRefreshFooter = (BaseRefreshFooter) findViewById(R.id.brf_base_refresh_footer);
        // BasePlaceholderView
        mBpvBasePlaceholder = (BasePlaceholderView) findViewById(R.id.bpv_base_placeholder);

        // 把继承baseRefresh的基类放入到 content中去
        mBaseRefreshLayout = getLayoutInflater().inflate(getRefreshLayout(), mBaseRefreshContent);
    }

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
        // 刷新事件
        initRefreshListener();
    }

    protected abstract int getRefreshLayout();

    /**
     * 在顶部添加一个固定的view
     *
     * @param view 需要添加的view
     */
    protected void addFixedHeadView(View view) {
        mFlBaseRefreshTopContent.setVisibility(View.VISIBLE);
        mFlBaseRefreshTopContent.addView(view);
    }

    /**
     * 在底部添加一个固定的view
     *
     * @param view 需要添加的view
     */
    protected void addFixedFootView(View view) {
        mFlBaseRefreshBottomContent.setVisibility(View.VISIBLE);
        mFlBaseRefreshBottomContent.addView(view);
    }

    /**
     * 添加一个view
     *
     * @param view 需要添加的view
     */
    protected void addContentView(View view) {
        mBaseRefreshContent.addView(view, 0);
    }

    /**
     * 设置数据为空的默认显示内容
     */
    protected void setDataEmpty() {
        // 隐藏内容 显示站位图
        mBaseRefreshContent.setVisibility(View.GONE);
        mBpvBasePlaceholder.setVisibility(View.VISIBLE);

        // 图标
        mPaceHolderIcon = R.drawable.icon_base_refresh_data_empty;
        // 文字
        mPlaceHolderTitle = "暂时没有查到您的数据";
        // 具体的消息 --- 默认不展示
        mPlaceHolderMsg = "";
        // 刷新按钮 --- 默认不展示
        mPlaceHolderRefreshButton = "";
        //  刷新事件
        mRefreshListener = null;

        // 设置布局
        mBpvBasePlaceholder.setEmptyView(mPaceHolderIcon, mPlaceHolderTitle, mPlaceHolderMsg, mPlaceHolderRefreshButton, null);
    }

    /**
     * 设置数据网络异常的默认显示内容
     */
    protected void setDataHttpError(View.OnClickListener refreshListener) {
        // 隐藏内容 显示站位图
        mBaseRefreshContent.setVisibility(View.GONE);
        mBpvBasePlaceholder.setVisibility(View.VISIBLE);

        // 图标
        mPaceHolderIcon = R.drawable.icon_base_empty_http_error;
        // 文字
        mPlaceHolderTitle = "网络异常";
        // 具体的消息
        mPlaceHolderMsg = "";
        // 刷新按钮
        mPlaceHolderRefreshButton = "";
        //  刷新事件
        mRefreshListener = refreshListener;

        // 设置布局
        mBpvBasePlaceholder.setEmptyView(mPaceHolderIcon, mPlaceHolderTitle, mPlaceHolderMsg, mPlaceHolderRefreshButton, mRefreshListener);
    }

    /**
     * 数据发生异常的默认展示
     */
    protected void setDataError() {
        // 隐藏内容 显示站位图
        mBaseRefreshContent.setVisibility(View.GONE);
        mBpvBasePlaceholder.setVisibility(View.VISIBLE);

        // 图标
        mPaceHolderIcon = R.drawable.icon_base_refresh_data_error;
        // 文字
        mPlaceHolderTitle = "数据异常";
        // 具体的消息
        mPlaceHolderMsg = "";
        // 刷新按钮
        mPlaceHolderRefreshButton = "";
        //  刷新事件
        mRefreshListener = null;

        // 设置布局
        mBpvBasePlaceholder.setEmptyView(mPaceHolderIcon, mPlaceHolderTitle, mPlaceHolderMsg, mPlaceHolderRefreshButton, null);
    }

    /**
     * 设置站位图的icon
     *
     * @param paceHolderIcon icon的对象
     */
    public void setPaceHolderIcon(int paceHolderIcon) {
        this.mPaceHolderIcon = paceHolderIcon;
    }

    /**
     * @param placeHolderTitle 设置站位图的标题
     */
    public void setPlaceHolderTitle(String placeHolderTitle) {
        this.mPlaceHolderTitle = placeHolderTitle;
    }

    /**
     * @param placeHolderMsg 设置站位图的具体msg
     */
    public void setPlaceHolderMsg(String placeHolderMsg) {
        this.mPlaceHolderMsg = placeHolderMsg;
    }

    /**
     * @param placeHolderRefreshButton 设置站位图的刷新按钮文字
     */
    public void setPlaceHolderRefreshButton(String placeHolderRefreshButton) {
        this.mPlaceHolderRefreshButton = placeHolderRefreshButton;
    }

    /**
     * @param refreshListener 设置站位图的点击事件
     */
    public void setRefreshListener(View.OnClickListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    /**
     * 刷新事件类型
     */
    protected void initRefreshListener() {
        // 刷新布局
        if (mRefreshType != null) {
            if (mRefreshType == RefreshType.TYPE_REFRESH) {
                mBrlBaseRefresh.setEnableRefresh(true);
                mBrlBaseRefresh.setOnRefreshListener(this);
            } else if (mRefreshType == RefreshType.TYPE_REFRESH_LOAD_MORE) {
                mBrlBaseRefresh.setEnableRefresh(true);
                mBrlBaseRefresh.setOnRefreshListener(this);
                mBrlBaseRefresh.setEnableLoadMore(true);
            } else if (mRefreshType == RefreshType.TYPE_NONE) {
                mBrlBaseRefresh.setEnableRefresh(false);
                mBrlBaseRefresh.setEnableLoadMore(false);
            }
        }
    }

    /**
     * 刷新结束
     */
    protected void finishRefresh() {
        mBrlBaseRefresh.finishRefresh();
    }

    /**
     * 结束加载更多
     */
    protected void finishLoadMore() {
        mBrlBaseRefresh.finishLoadMore();
    }

    /**
     * 结束加载更多和刷新
     */
    protected void finishRefreshLoadMore() {
        mBrlBaseRefresh.finishRefresh();
        mBrlBaseRefresh.finishLoadMore();
    }

    /**
     * 没有更多的数据
     */
    protected void finishLoadMoreWithNoMoreData() {
        // //完成加载并标记没有更多数据 1.0.4
        mBrlBaseRefresh.finishLoadMoreWithNoMoreData();
    }

}
