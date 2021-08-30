package com.android.helper.widget.banner;

import android.view.View;

import androidx.fragment.app.Fragment;

public interface BannerItemClickListener {
    /**
     * @param fragment 如果是fragment的话，就传递出当前的fragment对象,如果是普通图片则为null,使用的时候要进行判断
     * @param view     点击的view
     * @param position 当前的角标
     * @param object   传递进来的数据,需要去自己强转，如果是fragment的话，返回的数据为null，使用的时候要进行数据的判断
     */
    void onItemClick(Fragment fragment, View view, int position, Object object);
}
