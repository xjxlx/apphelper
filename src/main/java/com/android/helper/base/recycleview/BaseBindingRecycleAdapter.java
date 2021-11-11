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
 * @param <E> ViewBinding的具体类型，目前只适合单一的数据holder类型
 */
public abstract class BaseBindingRecycleAdapter<T, E extends ViewBinding> extends RecycleViewFrameWork<T, RecyclerView.ViewHolder> implements BindingViewListener<E> {

    protected E mBinding;
    protected ItemClickListener<E, T> mItemBindingClickListener;

    public BaseBindingRecycleAdapter(Fragment fragment) {
        super(fragment);
    }

    public BaseBindingRecycleAdapter(Fragment fragment, List<T> list) {
        super(fragment, list);
    }

    public BaseBindingRecycleAdapter(Fragment fragment, EmptyPlaceholder placeholder) {
        super(fragment, placeholder);
    }

    public BaseBindingRecycleAdapter(Fragment fragment, List<T> list, EmptyPlaceholder placeholder) {
        super(fragment, list, placeholder);
    }

    public BaseBindingRecycleAdapter(FragmentActivity activity) {
        super(activity);
    }

    public BaseBindingRecycleAdapter(FragmentActivity activity, EmptyPlaceholder placeholder) {
        super(activity, placeholder);
    }

    public BaseBindingRecycleAdapter(FragmentActivity activity, List<T> list) {
        super(activity, list);
    }

    public BaseBindingRecycleAdapter(FragmentActivity activity, List<T> list, EmptyPlaceholder placeholder) {
        super(activity, list, placeholder);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        BaseBindingVH<E> vh;
        if (viewType != ViewType.TYPE_EMPTY) {
            mBinding = getBinding(LayoutInflater.from(mActivity), parent);

            if (mBinding == null) {
                LogUtil.e("BaseBindingRecycle ---> context 为空！");
            }
            assert (mBinding != null);
            vh = new BaseBindingVH<>(mBinding);
            return vh;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    public abstract void onBindHolder(@NonNull @NotNull BaseBindingVH<E> holder, int position);

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (!isDestroy) {
            int itemViewType = getItemViewType(position);
            if (itemViewType != ViewType.TYPE_EMPTY) {
                // 只返回正常的布局，不返回空布局的holder
                if (holder instanceof BaseBindingVH) {
                    onBindHolder((BaseBindingVH<E>) holder, position);
                }
            }
        }
    }

    @Override
    public View getRootView() {
        assert mBinding != null;
        return mBinding.getRoot();
    }

    public void setItemClickListener(ItemClickListener<E, T> itemClickListener) {
        this.mItemBindingClickListener = itemClickListener;
    }

}
