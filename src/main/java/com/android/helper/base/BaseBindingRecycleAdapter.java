package com.android.helper.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.android.helper.interfaces.BindingViewListener;
import com.android.helper.interfaces.listener.ItemClickListener;
import com.android.helper.interfaces.listener.OnItemClickListener;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 加入了viewBinding的RecycleView
 *
 * @param <T> 数据类型
 * @param <E> ViewBinding的具体类型
 */
public abstract class BaseBindingRecycleAdapter<T, E extends ViewBinding> extends RecyclerView.Adapter<BaseBindingVH<E>> implements BindingViewListener<E> {

    protected Context mContext;
    protected List<T> mList;
    protected E mBinding;
    protected OnItemClickListener<T> mItemClickListener;
    protected ItemClickListener<E, T> mItemBindingClickListener;

    public BaseBindingRecycleAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public BaseBindingRecycleAdapter(Context mContext, List<T> mList) {
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

    @NonNull
    @NotNull
    @Override
    public BaseBindingVH<E> onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (mContext != null) {
            mBinding = getBinding(LayoutInflater.from(mContext), parent);
            if (mBinding != null) {
                return new BaseBindingVH<E>(mBinding);
            }
        } else {
            throw new NullPointerException("BaseBindingRecycle ---> context 为空！");
        }
        return null;
    }

    public void setItemClickListener(OnItemClickListener<T> mOnItemClickListener) {
        this.mItemClickListener = mOnItemClickListener;
    }

    public void setItemClickListener(ItemClickListener<E, T> itemClickListener) {
        this.mItemBindingClickListener = itemClickListener;
    }

    @Override
    public View getRootView() {
        return mBinding.getRoot();
    }
}
