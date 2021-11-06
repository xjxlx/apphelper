package com.android.helper.base.recycleview;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.interfaces.listener.OnItemClickListener;
import com.android.helper.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView 的最底层
 */
public abstract class RecycleViewFrameWork<T, R extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<R> implements BaseLifecycleObserver {

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

    public RecycleViewFrameWork(Fragment fragment) {
        mFragment = fragment;
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);

            mActivity = mFragment.getActivity();
        }
    }

    public RecycleViewFrameWork(Fragment fragment, List<T> list) {
        mFragment = fragment;
        mList = list;
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
            mActivity = mFragment.getActivity();
        }
    }

    public RecycleViewFrameWork(FragmentActivity activity) {
        mActivity = activity;
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);
        }
    }

    public RecycleViewFrameWork(FragmentActivity activity, List<T> list) {
        mActivity = activity;
        mList = list;
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
            mList.add(t);
            notifyItemInserted(mList.size());
        }
    }

    /**
     * @param position 删除具体的位置
     */
    public void removeItem(int position) {
        if (position >= 0) {
            // 移除数据源
            mList.remove(position);
            // 刷新adapter  notifyItemRangeRemoved
            notifyItemRemoved(position);

            // 有动画的效果
            /*
             * positionStart : 是从界面哪个位置的Item开始变化,比如你点击界面上的第二个ItemView positionStart是1
             * itemCount : 是已经发生变化的item的个数(包括自己,即正在点击这个),比如,你点击界面上的第二个ItemView,position [1,9] 发生变化,共计
             */
            notifyItemRangeChanged(position, mList.size() - position);
        }
    }

    /**
     * @param position  具体的位置
     * @param animation 是否显示动画，true:显示动画，false:不显示动画
     */
    public void removeItem(int position, boolean animation) {
        if (animation) {
            removeItem(position);
        }
        if (position >= 0) {
            // 移除数据源
            mList.remove(position);
            // 刷新adapter  notifyItemRangeRemoved
            notifyItemRemoved(position);

            // 没有动画效果
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * 设置点击的对象
     *
     * @param mOnItemClickListener 点击对象
     */
    public void setItemClickListener(OnItemClickListener<T> mOnItemClickListener) {
        this.mItemClickListener = mOnItemClickListener;
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
