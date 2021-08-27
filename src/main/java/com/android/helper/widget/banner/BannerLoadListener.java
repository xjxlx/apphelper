package com.android.helper.widget.banner;

import android.widget.ImageView;

/**
 * banner加载时候的图片处理
 */
public interface BannerLoadListener {
    /**
     * @param view 当前展示的imageview
     * @param object    当前的数据类型，为了做兼容，此处传递的是object，需要用户去手动转换需要的具体类型
     */
    void onLoadView(ImageView view, Object object);
}
