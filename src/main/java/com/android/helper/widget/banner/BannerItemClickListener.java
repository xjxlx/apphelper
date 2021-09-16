package com.android.helper.widget.banner;

import android.view.View;

public interface BannerItemClickListener<T> {
    /**
     * @param view     点击的view
     * @param position 当前的角标
     * @param t        传递进来的数据,需要去自己强转，如果是fragment的话，返回的数据为null，使用的时候要进行数据的判断
     */
    void onItemClick(View view, int position, T t);
}
