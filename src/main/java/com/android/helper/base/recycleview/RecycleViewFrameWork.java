package com.android.helper.base.recycleview;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.R;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.TextViewUtil;
import com.android.helper.utils.refresh.RefreshUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView 的最底层
 */
public abstract class RecycleViewFrameWork<T, E extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<E> implements BaseLifecycleObserver {

    protected boolean isDestroy;// 页面是不是已经销毁了

    /**
     * Activity的对象
     */
    protected FragmentActivity mActivity;

    /**
     * Fragment的对象
     */
    protected Fragment mFragment;

    /**
     * 集合的数据
     */
    protected List<T> mList = new ArrayList<>();

    private boolean isEmpty; // 当前数据是否为空

    /**
     * 布局的类型
     * <ol>
     *     -1：空布局
     *     -2：头布局
     *     -3：脚布局
     * </ol>
     */
    protected int mItemType;
    private Placeholder mPlaceHolder;       // 单个的占位图
    private Placeholder mGlobalPlaceholder; // 全局的占位图
    private View mEmptyView;
    private int mErrorType;  // 1:空数据  2：错误数据
    private RefreshUtil<?> mRefreshUtil; // 刷新工具类
    private RecyclerView mRecycleView;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ViewType.TYPE_EMPTY, ViewType.TYPE_HEAD, ViewType.TYPE_FOOT})
    public @interface ViewType {
        /**
         * 空布局
         */
        int TYPE_EMPTY = -11;
        /**
         * 头布局
         */
        int TYPE_HEAD = -22;
        /**
         * 脚布局
         */
        int TYPE_FOOT = -33;
    }

    public RecycleViewFrameWork(FragmentActivity activity) {
        addObserverActivity(activity, null);
    }

    public RecycleViewFrameWork(FragmentActivity activity, List<T> list) {
        addObserverActivity(activity, list);
    }

    public RecycleViewFrameWork(Fragment fragment) {
        addObserverFragment(fragment, null);
    }

    public RecycleViewFrameWork(Fragment fragment, List<T> list) {
        addObserverFragment(fragment, list);
    }

    private void addObserverFragment(Fragment fragment, List<T> list) {
        this.mFragment = fragment;
        this.mList = list;
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
            mActivity = mFragment.getActivity();
        }
        // 获取临时的占位图
        mGlobalPlaceholder = Placeholder.getGlobalPlaceholder();
    }

    private void addObserverActivity(FragmentActivity activity, List<T> list) {
        this.mActivity = activity;
        this.mList = list;
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);
        }
        // 获取临时的占位图
        mGlobalPlaceholder = Placeholder.getGlobalPlaceholder();
    }

    /**
     * @return 获取数据源
     */
    public List<T> getList() {
        return mList;
    }

    /**
     * 刷新全部的数据
     *
     * @param list 数据源
     */
    public void setList(List<T> list) {
        mErrorType = 1; // 普通数据的设置
        this.mList = list;
        if (mList != null) {
            LogUtil.e("------------------------------------------------size: " + mList.size() + " ----------------------------------");
        }
        notifyDataSetChanged();
    }

    /**
     * <p>
     * 针对有上拉加载更多的时候去使用，如果是刷新，就刷新所有，如果是加载更多，就去添加数据
     * </p>
     *
     * @param list      数据源
     * @param isRefresh 是否是下拉刷新，如果是首次加载数据，就刷新全部数据，否则就添加数据
     */
    public void setList(List<T> list, boolean isRefresh) {
        mErrorType = 1; // 普通数据的设置
        if (list != null) {
            LogUtil.e("------------------------------------------------size: " + list.size() + " ----------------------------------");
            // 首次加载数据，刷新全部的数据源
            if (isRefresh) {
                this.mList = list;
                notifyDataSetChanged();
                LogUtil.e("------------------------------------------------ 全部刷新了数据 ----------------------------------");
            } else {
                // 针对上拉加载的时候，如果是下拉加载，就添加新的数据源
                insertedList(list);
                LogUtil.e("------------------------------------------------ 插入了新的数据 ----------------------------------");
            }
        }
        // 空数据的时候，不执行其他操作，避免数据显示异常
    }

    /**
     * @param list        数据源
     * @param refreshUtil 刷新工具的对象
     */
    public void setList(List<T> list, RefreshUtil<?> refreshUtil) {
        if (refreshUtil != null) {
            setList(list, refreshUtil.isRefresh());
        } else {
            setList(list);
        }
    }

    /**
     * <p>
     * 插入一个数据集合
     * </p>
     *
     * @param list 插入的数据
     */
    public void insertedList(List<T> list) {
        if ((list != null) && (list.size() > 0)) {
            if (mList == null) {
                mList = new ArrayList<>();
            }
            mList.addAll(list);
            notifyItemInserted(mList.size());
        }
    }

    /**
     * <p>
     * 插入单个的数据
     * </p>
     *
     * @param t 具体的数据
     */
    public void insertedItem(T t) {
        if (t != null) {
            if (mList == null) {
                mList = new ArrayList<>();
            }
            mList.add(t);
            notifyItemInserted(mList.size());
        }
    }

    /**
     * <ol>
     *     注意：删除item的时候，position的取值，不能按照数据集合的position取值，应该使用{@link RecyclerView.ViewHolder#getBindingAdapterPosition()}
     *     去获取当前点击时候的position
     * </ol>
     *
     * @param position 删除具体的位置，有动画的效果
     */
    public void removeItem(int position) {
        if ((position >= 0) && (mList != null) && (mList.size() > position)) {
            // 移除数据源
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 更新一个对象
     *
     * @param position 具体的位置
     */
    public void updateItem(int position) {
        if ((mList != null) && (position < mList.size())) {
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mList == null) {
            isEmpty = true;
        } else {
            isEmpty = mList.isEmpty();
            count = mList.size();
        }

        // 空布局的条目
        if (isEmpty && ((mPlaceHolder != null || (mGlobalPlaceholder != null && mGlobalPlaceholder.isFromGlobal())))) {
            count = 1;
        }
        return count;
    }

    @NonNull
    @NotNull
    @Override
    public E onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        E vh = null;
        if (viewType == ViewType.TYPE_EMPTY) { // 设置空布局
            if (mEmptyView == null) {
                if (mPlaceHolder != null) {
                    mEmptyView = mPlaceHolder.getEmptyView(parent);
                } else {
                    if (mGlobalPlaceholder != null) {
                        mEmptyView = mGlobalPlaceholder.getEmptyView(parent);
                    }
                }
            }
            if (mEmptyView != null) {
                EmptyVH emptyVH = new EmptyVH(mEmptyView);
                vh = (E) emptyVH;
            }
        }
        assert vh != null;
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull E holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ViewType.TYPE_EMPTY) { // 空布局的数据设置
            if (holder instanceof EmptyVH) {
                EmptyVH emptyVH = (EmptyVH) holder;

                if (mPlaceHolder == null) { // 如果没有设置，就是用全局的站位图信息
                    if (mGlobalPlaceholder != null) {
                        String emptyContent = mGlobalPlaceholder.getEmptyContent();
                        int emptyContentColor = mGlobalPlaceholder.getEmptyContentColor();
                        int emptyContentSize = mGlobalPlaceholder.getEmptyContentSize();
                        int emptyResource = mGlobalPlaceholder.getEmptyResource();

                        // 文字的描述
                        if (!TextUtils.isEmpty(emptyContent)) {
                            emptyVH.mTvMsg.setText(emptyContent);
                        }

                        // 描述文字的大小
                        if (emptyContentSize != 0) {
                            emptyVH.mTvMsg.setTextSize(emptyContentSize);
                        }

                        // 文字的颜色
                        if (emptyContentColor != 0) {
                            emptyVH.mTvMsg.setTextColor(emptyContentColor);
                        }

                        // 图片
                        if (emptyResource != 0) {
                            emptyVH.mIvImage.setImageResource(emptyResource);
                        }
                    }
                } else {  // 设置了指定的占位图信息
                    if (mPlaceHolder.getTypeForView() == 2) { // 默认的占位图

                        switch (mErrorType) {
                            case 1: // 空数据布局
                                // 刷新按钮不可见
                                emptyVH.mIvButton.setVisibility(View.GONE);

                                // 空布局提示
                                String emptyContent = mPlaceHolder.getEmptyContent();
                                if (!TextUtils.isEmpty(emptyContent)) {
                                    TextViewUtil.setText(emptyVH.mTvMsg, emptyContent);
                                }

                                // 描述文字的大小
                                int contentSize = mPlaceHolder.getEmptyContentSize();
                                if (contentSize != 0) {
                                    emptyVH.mTvMsg.setTextSize(contentSize);
                                }

                                // 提示信息的颜色
                                int contentColor = mPlaceHolder.getEmptyContentColor();
                                if (contentColor != 0) {
                                    emptyVH.mTvMsg.setTextColor(contentColor);
                                }

                                // 空布局资源
                                int emptyResource = mPlaceHolder.getEmptyResource();
                                if (emptyResource != 0) {
                                    emptyVH.mIvImage.setImageResource(emptyResource);
                                }

                                break;
                            case 2: // 错误的布局可见
                                emptyVH.mIvButton.setVisibility(View.VISIBLE);

                                int mGlobalErrorImage = 0;
                                String mGlobalErrorContent = null;
                                String mGlobalErrorButtonContent = null;
                                int mGlobalErrorButtonBackground = 0;

                                if (mGlobalPlaceholder != null) {
                                    mGlobalErrorImage = mGlobalPlaceholder.getErrorImage();
                                    mGlobalErrorContent = mGlobalPlaceholder.getErrorContent();
                                    mGlobalErrorButtonContent = mGlobalPlaceholder.getErrorButtonContent();
                                    mGlobalErrorButtonBackground = mGlobalPlaceholder.getErrorButtonBackground();
                                }

                                // 错误布局资源
                                int errorImageResource = mPlaceHolder.getErrorImage();
                                if (errorImageResource != 0) {
                                    emptyVH.mIvImage.setImageResource(errorImageResource);
                                } else {
                                    // 设置全局的异常图片资源
                                    if (mGlobalErrorImage != 0) {
                                        emptyVH.mIvImage.setImageResource(mGlobalErrorImage);
                                    }
                                }

                                // 错误布局提示
                                String errorContent = mPlaceHolder.getErrorContent();
                                if (!TextUtils.isEmpty(errorContent)) {
                                    TextViewUtil.setText(emptyVH.mTvMsg, errorContent);
                                } else {
                                    // 全局的错误提示
                                    if (!TextUtils.isEmpty(mGlobalErrorContent)) {
                                        TextViewUtil.setText(emptyVH.mTvMsg, mGlobalErrorContent);
                                    }
                                }

                                // 错误布局按钮的文字
                                String errorButtonContent = mPlaceHolder.getErrorButtonContent();
                                if (!TextUtils.isEmpty(errorButtonContent)) {
                                    TextViewUtil.setText(emptyVH.mIvButton, errorButtonContent);
                                } else {
                                    // 全局的异常Button文字
                                    if (!TextUtils.isEmpty(mGlobalErrorButtonContent)) {
                                        TextViewUtil.setText(emptyVH.mIvButton, mGlobalErrorButtonContent);
                                    }
                                }

                                // 错误布局的背景
                                int errorButtonBackground = mPlaceHolder.getErrorButtonBackground();
                                if (errorButtonBackground != 0) {
                                    emptyVH.mIvButton.setBackgroundResource(errorButtonBackground);
                                } else {
                                    // 全局的异常背景
                                    if (mGlobalErrorButtonBackground != 0) {
                                        emptyVH.mIvButton.setBackgroundResource(mGlobalErrorButtonBackground);
                                    }
                                }

                                emptyVH.mIvButton.setOnClickListener(v -> {
                                    if (mRefreshUtil != null) {
                                        mRefreshUtil.refresh();
                                    }
                                });

                                break;
                        }

                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty) {
            mItemType = ViewType.TYPE_EMPTY;
            return mItemType;
        }
        return super.getItemViewType(position);
    }

    /**
     * 给数据设置站位图的信息
     *
     * @param emptyPlaceholder 占位图的信息
     */
    public void setPlaceholderData(Placeholder emptyPlaceholder) {
        this.mPlaceHolder = emptyPlaceholder;
    }

    /**
     * 设置网络错误的展示
     */
    public void setErrorHttpClient(RefreshUtil<?> refreshUtil) {
        this.mRefreshUtil = refreshUtil;
        mErrorType = 2;
        notifyDataSetChanged();
    }

    public static class EmptyVH extends RecyclerView.ViewHolder {
        private final ImageView mIvImage;
        private final TextView mTvMsg;
        private final TextView mIvButton;

        public EmptyVH(@NonNull @NotNull View itemView) {
            super(itemView);
            mIvImage = itemView.findViewById(R.id.iv_base_placeholder_image);
            mTvMsg = itemView.findViewById(R.id.tv_base_placeholder_msg);
            mIvButton = itemView.findViewById(R.id.iv_base_error_placeholder);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecycleView = recyclerView;
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
        if (mFragment != null) {
            boolean detached = mFragment.isDetached();
            if (detached) {
                isDestroy = true;
            }
        }

        if (mActivity != null) {
            if (mActivity.isFinishing() || mActivity.isDestroyed()) {
                isDestroy = true;
            }
        }

        if (mEmptyView != null) {
            mEmptyView = null;
        }

        if (mList != null) {
            mList = null;
        }

        LogUtil.e("isDestroy:" + isDestroy);
    }

}
