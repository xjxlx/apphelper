package com.android.helper.base.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * RecycleView的封装基类
 *
 * @param <T> 数据的类型
 * @param <E> ViewHolder的对象，目前只适合单数据类型的使用
 */
public abstract class BaseRecycleAdapter<T, E extends RecyclerView.ViewHolder> extends RecycleViewFrameWork<T, E> {

    public BaseRecycleAdapter(Fragment fragment) {
        super(fragment);
    }

    public BaseRecycleAdapter(Fragment fragment, List<T> list) {
        super(fragment, list);
    }

    public BaseRecycleAdapter(FragmentActivity activity) {
        super(activity);
    }

    public BaseRecycleAdapter(FragmentActivity activity, List<T> list) {
        super(activity, list);
    }

    /**
     * @return 返回一个RecycleView的布局
     */
    protected abstract int getLayout();

    protected abstract E createViewHolder(View inflate);

//    @NonNull
//    @NotNull
//    @Override
//    public E onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        E vh = null;
//        if (viewType != 1) {
//            int layout = getLayout();
//            if (layout > 0) {
//                View inflate = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
//                if (inflate != null) {
//                    vh = createViewHolder(inflate);
//                }
//            }
//
//            if (vh != null) {
//                return vh;
//            }
//        }
//        return super.onCreateViewHolder(parent, viewType);
//    }


    @NonNull
    @NotNull
    @Override
    public E onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        E viewHolder = null;
        if (viewType != 1) {
            int layout = getLayout();
            if (layout > 0) {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
                if (inflate != null) {
                    viewHolder = createViewHolder(inflate);
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
            if (itemViewType != 1) {
                onBindHolder(holder, position);
            }
        }
    }
}
