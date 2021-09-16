package com.android.helper.widget.banner;

import android.view.View;

/**
 * banner加载时候的图片处理
 */
public interface BannerLoadListener<T> {

    /**
     * @param view 当前展示的 view,如果传入的Integer 类型，或者String类型，返回的是一个ImageView,
     *             如果传入的是View类型的，则返回整个View供使用者去使用
     * @param t    当前的数据类型
     */
    void onLoadView(View view, int position, T t);

}
