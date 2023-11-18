package com.android.helper.interfaces.listener;


import com.android.common.base.recycleview.BaseVH;

/**
 * RecycleView数据加载完成的监听回调
 *
 * @param <E> vh的具体类型
 */
public interface OnRecycleLoadCompletedListener<E extends BaseVH> {
    void onLoadComplete(E vh, int position);
}
