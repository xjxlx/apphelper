package com.android.helper.base.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.android.helper.base.BaseBindingVH;
import com.android.helper.interfaces.BindingViewListener;
import com.android.helper.interfaces.listener.ItemClickListener;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 加入了viewBinding的RecycleView
 *
 * @param <T> 数据类型
 * @param <V> ViewBinding的具体类型，目前只适合单一的数据holder类型
 */
public abstract class BaseBindingRecycleAdapter<T, V extends ViewBinding> extends RecycleViewFrameWork<T, RecyclerView.ViewHolder> implements BindingViewListener<V> {

    protected ItemClickListener<V, T> mItemBindingClickListener;
    private BaseBindingVH<V> mBaseBindingVH;

    public BaseBindingRecycleAdapter(FragmentActivity activity) {
        super(activity);
    }

    public BaseBindingRecycleAdapter(FragmentActivity activity, List<T> list) {
        super(activity, list);
    }

    public BaseBindingRecycleAdapter(Fragment fragment) {
        super(fragment);
    }

    public BaseBindingRecycleAdapter(Fragment fragment, List<T> list) {
        super(fragment, list);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        BaseBindingVH<V> vh;
        if (viewType != ViewType.TYPE_EMPTY) {
            V binding = null;
            if (mActivity != null) {
                binding = getBinding(LayoutInflater.from(mActivity), parent);
            } else {
                if (mFragment != null) {
                    binding = getBinding(LayoutInflater.from(mFragment.getContext()), parent);
                }
            }

            if (binding == null) {
                LogUtil.e("BaseBindingRecycle ---> context 为空！");
            }
            assert (binding != null);
            vh = new BaseBindingVH<>(binding);
            return vh;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    public abstract void onBindHolder(@NonNull @NotNull BaseBindingVH<V> holder, V mBinding, int position);

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (!isDestroy) {
            int itemViewType = getItemViewType(position);
            if (itemViewType != ViewType.TYPE_EMPTY) {
                // 只返回正常的布局，不返回空布局的holder
                if (holder instanceof BaseBindingVH) {
                    mBaseBindingVH = (BaseBindingVH<V>) holder;
                    V mBinding = mBaseBindingVH.mBinding;
                    onBindHolder(mBaseBindingVH, mBinding, position);
                }
            } else {
                /*
                 * 空数据点击item的事件回调
                 * <ol>
                 *     1:如果是默认的布局的话，回调的时候，会回调id{ R.id.base_placeholder }
                 *     2:如果是指定的布局的话，肯定自己写的布局，自己会知道
                 * </ol>
                 */
                if (mEmptyView != null) {
                    mEmptyView.setOnClickListener(v -> {
                        if (mItemBindingClickListener != null) {
                            mItemBindingClickListener.onItemClick(mEmptyView, null, 0, null);
                        }
                    });
                }
            }
        }
    }

    @Override
    public View getRootView() {
        assert mBaseBindingVH != null;
        return mBaseBindingVH.itemView;
    }

    public void setItemClickListener(ItemClickListener<V, T> itemClickListener) {
        this.mItemBindingClickListener = itemClickListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 销毁对象
        if (mBaseBindingVH != null) {
            if (mBaseBindingVH.mBinding != null) {
                mBaseBindingVH.mBinding = null;
            }
            mBaseBindingVH.mBinding = null;
        }

        if (mItemBindingClickListener != null) {
            mItemBindingClickListener = null;
        }
    }
}
