package com.android.helper.base.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.interfaces.listener.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * RecycleView的封装基类
 *
 * @param <T> 数据的类型
 * @param <E> ViewHolder的对象，目前只适合单数据类型的使用
 */
public abstract class BaseRecycleAdapter<T, E extends RecyclerView.ViewHolder> extends RecycleViewFrameWork<T, E> {

    /**
     * 点击事件的对象
     */
    protected OnItemClickListener<T> mItemClickListener;

    public BaseRecycleAdapter(FragmentActivity activity) {
        super(activity);
    }

    public BaseRecycleAdapter(FragmentActivity activity, List<T> list) {
        super(activity, list);
    }

    public BaseRecycleAdapter(Fragment fragment) {
        super(fragment);
    }

    public BaseRecycleAdapter(Fragment fragment, List<T> list) {
        super(fragment, list);
    }

    /**
     * @return 返回一个RecycleView的布局
     */
    protected abstract int getLayout(int viewType);

    protected abstract E createViewHolder(View inflate, int viewType);

    @NonNull
    @NotNull
    @Override
    public E onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        E viewHolder = null;
        if (viewType != ViewType.TYPE_EMPTY) {
            int layout = getLayout(viewType);
            if (layout > 0) {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                if (inflate != null) {
                    viewHolder = createViewHolder(inflate, viewType);
                }
            }
            if (viewHolder != null) {
                return viewHolder;
            }
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    public abstract void onBindHolder(@NonNull @NotNull E holder, int position);

    @Override
    public void onBindViewHolder(@NonNull @NotNull E holder, int position) {
        super.onBindViewHolder(holder, position);
        if (!isDestroy) {
            int itemViewType = getItemViewType(position);
            if (itemViewType != ViewType.TYPE_EMPTY) {
                onBindHolder(holder, position);
            } else {
                /*
                 * 空数据点击item的事件回调
                 * <ol>
                 *     1:如果是默认的布局的话，回调的时候，会回调id{ R.id.base_placeholder }
                 *     2:如果是指定的布局的话，肯定自己写的布局，自己会知道
                 * </ol>
                 */
                if (mBottomResourceParent != null) {
                    mBottomResourceParent.setOnClickListener(v -> {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(mBottomResourceParent, 0, null);
                        }
                    });
                }
            }
        }
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
    public void onDestroy() {
        super.onDestroy();
        if (mItemClickListener != null) {
            mItemClickListener = null;
        }
    }
}
