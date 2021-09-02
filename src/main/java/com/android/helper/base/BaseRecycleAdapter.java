package com.android.helper.base;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.interfaces.listener.OnItemClickListener;
import com.android.helper.utils.LogUtil;

import java.util.List;

/**
 * RecycleView的封装基类
 *
 * @param <T> 数据的类型
 * @param <E> ViewHolder的对象
 */
public abstract class BaseRecycleAdapter<T, E extends BaseVH> extends RecyclerView.Adapter<E> {

    protected Activity mContext;
    protected List<T> mList;

    protected OnItemClickListener<T> mItemClickListener;

    public BaseRecycleAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    public BaseRecycleAdapter(Activity mContext, List<T> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setList(List<T> mList) {
        this.mList = mList;
        if (mList != null) {
            LogUtil.e("------------------------------------------------size: " + mList.size() + " ----------------------------------");
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * @return 返回一个RecycleView的布局
     */
    protected abstract int getLayout();

    protected abstract E createViewHolder(View inflate);

    @NonNull
    @Override
    public E onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(getLayout(), viewGroup, false);
        return createViewHolder(inflate);
    }

    public void setItemClickListener(OnItemClickListener<T> mOnItemClickListener) {
        this.mItemClickListener = mOnItemClickListener;
    }

}
