package com.android.helper.base.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.helper.base.BaseVH;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * RecycleView的封装基类
 *
 * @param <T> 数据的类型
 * @param <E> ViewHolder的对象，目前只适合单数据类型的使用
 */
public abstract class BaseRecycleAdapter<T, E extends BaseVH> extends RecycleViewFrameWork<T, E> {

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

    @NonNull
    @NotNull
    @Override
    public E onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        E vh = null;
        int layout = getLayout();
        if (layout > 0) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
            if (inflate != null) {
                vh = createViewHolder(inflate);
            }
        }

        if (vh == null) {
            LogUtil.e("BaseRecycleView的 ViewHolder --->  为空！");
        }

        assert (vh != null);
        return vh;
    }

    public abstract void onBindHolder(@NonNull @NotNull E holder, int position);

    @Override
    public void onBindViewHolder(@NonNull @NotNull E holder, int position) {
        if (!isDestroy) {
            onBindHolder(holder, position);
        }
    }

}
