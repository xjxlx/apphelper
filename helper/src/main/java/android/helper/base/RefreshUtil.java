package android.helper.base;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import android.helper.R;
import android.helper.base.refresh.BaseRefreshFooter;
import android.helper.base.refresh.BaseRefreshHeader;
import android.helper.base.refresh.BaseRefreshLayout;
import android.helper.widget.BasePlaceholderView;

public class RefreshUtil implements OnRefreshLoadMoreListener {
    
    private BaseRefreshLayout mBrlBaseRefresh;
    private BaseRefreshHeader mBrhBaseRefreshHeader;
    protected FrameLayout mBaseRefreshContent;
    private BaseRefreshFooter mBrfBaseRefreshFooter;
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
    
    /**
     * 设置刷新的布局
     */
    public void setContentView(View refreshRoot) {
        // refresh layout
        mBrlBaseRefresh = (BaseRefreshLayout) refreshRoot.findViewById(R.id.brl_base_refresh);
        // refresh  header
        mBrhBaseRefreshHeader = (BaseRefreshHeader) refreshRoot.findViewById(R.id.brh_base_refresh_header);
        // content
        mBaseRefreshContent = (FrameLayout) refreshRoot.findViewById(R.id.fl_base_refresh_content);
        // refresh footer
        mBrfBaseRefreshFooter = (BaseRefreshFooter) refreshRoot.findViewById(R.id.brf_base_refresh_footer);
        // BasePlaceholderView
        mBpvBasePlaceholder = (BasePlaceholderView) refreshRoot.findViewById(R.id.bpv_base_placeholder);
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
        mBpvBasePlaceholder.setEmptyView(mPaceHolderIcon, mPlaceHolderTitle, mPlaceHolderMsg, mPlaceHolderRefreshButton, mRefreshListener);
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
     * 设置刷新的类型
     *
     * @param refreshType 固定的枚举类型，RefreshType.Type...
     */
    public void setRefreshType(RefreshType refreshType) {
        mRefreshType = refreshType;
    }
    
    /**
     * 刷新事件类型
     */
    protected void initListener() {
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
    
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
    
    }
    
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
    
    }
}
