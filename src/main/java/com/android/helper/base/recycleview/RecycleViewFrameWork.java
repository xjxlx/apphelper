package com.android.helper.base.recycleview;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.R;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.interfaces.listener.OnItemClickListener;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.TextViewUtil;

import org.jetbrains.annotations.NotNull;

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
    private Fragment mFragment;

    /**
     * 集合的数据
     */
    protected List<T> mList = new ArrayList<>();

    /**
     * 点击事件的对象
     */
    protected OnItemClickListener<T> mItemClickListener;
    private boolean isEmpty; // 当前数据是否为空

    /**
     * 布局的类型
     * <ol>
     *     1：空布局
     *     2：头布局
     *     3：脚布局
     * </ol>
     */
    protected int mItemType;
    private EmptyPlaceholder mEmptyPlaceHolder;

    public RecycleViewFrameWork(Fragment fragment) {
        addObserverFragment(fragment, null, null);
    }

    public RecycleViewFrameWork(Fragment fragment, List<T> list) {
        addObserverFragment(fragment, list, null);
    }

    public RecycleViewFrameWork(Fragment fragment, EmptyPlaceholder placeholder) {
        addObserverFragment(fragment, null, placeholder);
    }

    public RecycleViewFrameWork(Fragment fragment, List<T> list, EmptyPlaceholder placeholder) {
        addObserverFragment(fragment, list, placeholder);
    }


    public RecycleViewFrameWork(FragmentActivity activity) {
        addObserverActivity(activity, null, null);
    }

    public RecycleViewFrameWork(FragmentActivity activity, EmptyPlaceholder placeholder) {
        addObserverActivity(activity, null, placeholder);
    }

    public RecycleViewFrameWork(FragmentActivity activity, List<T> list) {
        addObserverActivity(activity, list, null);
    }

    public RecycleViewFrameWork(FragmentActivity activity, List<T> list, EmptyPlaceholder placeholder) {
        addObserverActivity(activity, list, placeholder);
    }

    private void addObserverFragment(Fragment fragment, List<T> list, EmptyPlaceholder placeholder) {
        this.mFragment = fragment;
        this.mList = list;
        this.mEmptyPlaceHolder = placeholder;
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
            mActivity = mFragment.getActivity();
        }
    }

    private void addObserverActivity(FragmentActivity activity, List<T> list, EmptyPlaceholder placeholder) {
        this.mActivity = activity;
        this.mList = list;
        this.mEmptyPlaceHolder = placeholder;
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);
        }
    }


    /**
     * <p>
     * 刷新全部的数据
     * </p>
     *
     * @param list 数据源
     */
    public void setList(List<T> list) {
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
        // todo 待测试
        notifyItemChanged(position);
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
        if (isEmpty && (mEmptyPlaceHolder != null)) {
            count = 1;
        }
        return count;
    }

    @NonNull
    @NotNull
    @Override
    public E onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        E vh = null;
        if (viewType == 1) { // 设置空布局
            if (mEmptyPlaceHolder != null) {
                View emptyView = mEmptyPlaceHolder.getEmptyView();
                if (emptyView != null) {
                    EmptyVH emptyVH = new EmptyVH(emptyView);
                    vh = (E) emptyVH;
                }
            }
        }
        assert vh != null;
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull E holder, int position) {
        int itemViewType = getItemViewType(position);

        if (itemViewType == 1) { // 空布局的数据设置
            if (holder instanceof EmptyVH) {
                EmptyVH emptyVH = (EmptyVH) holder;
                if (mEmptyPlaceHolder.getTypeForView() == 2) {

                    int emptyResource = mEmptyPlaceHolder.getEmptyResource();
                    String emptyContent = mEmptyPlaceHolder.getEmptyContent();
                    int contentSize = mEmptyPlaceHolder.getContentSize();
                    int contentColor = mEmptyPlaceHolder.getContentColor();

                    if (emptyResource != 0) {
                        emptyVH.mIvBasePlaceholderImage.setImageResource(emptyResource);
                    }
                    if (!TextUtils.isEmpty(emptyContent)) {
                        TextViewUtil.setText(emptyVH.mTvBasePlaceholderMsg, emptyContent);
                    }
                    if (contentSize != 0) {
                        emptyVH.mTvBasePlaceholderMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
                    }

                    if (contentColor != 0) {
                        emptyVH.mTvBasePlaceholderMsg.setTextColor(contentColor);
                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty) {
            return 1;
        }
        return super.getItemViewType(position);
    }

    /**
     * 给数据设置空布局的站位资源
     *
     * @param emptyPlaceholder 空布局的对象
     */
    public void setEmptyData(EmptyPlaceholder emptyPlaceholder) {
        this.mEmptyPlaceHolder = emptyPlaceholder;
    }

    /**
     * 设置点击的对象
     *
     * @param mOnItemClickListener 点击对象
     */
    public void setItemClickListener(OnItemClickListener<T> mOnItemClickListener) {
        this.mItemClickListener = mOnItemClickListener;
    }


    public static class EmptyVH extends RecyclerView.ViewHolder {
        private final ImageView mIvBasePlaceholderImage;
        private final TextView mTvBasePlaceholderMsg;

        public EmptyVH(@NonNull @NotNull View itemView) {
            super(itemView);
            mIvBasePlaceholderImage = itemView.findViewById(R.id.iv_base_placeholder_image);
            mTvBasePlaceholderMsg = itemView.findViewById(R.id.tv_base_placeholder_msg);
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
        LogUtil.e("isDestroy:" + isDestroy);
    }

}
