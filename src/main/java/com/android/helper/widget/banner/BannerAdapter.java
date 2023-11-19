package com.android.helper.widget.banner;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.common.utils.LogUtil;
import com.android.helper.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * banner的图片适配器，适用于加载单独的图片
 */
public class BannerAdapter<T> extends PagerAdapter {
    private final List<T> mListData;
    String tag = "Adapter ---> ";
    private BannerLoadListener<T> mLoadListener;
    private BannerItemClickListener<T> mItemClickListener;// 点击事件

    public BannerAdapter(List<T> listData) {
        mListData = listData;
    }

    @Override
    public int getCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    // :初始化每个Item的实布局，类似于getview
    // :viewpager会默认加载三个布局，上一页，本业，和下一页，其他页面会自动销毁，防止内存溢出
    @SuppressLint({"SetTextI18n", "InflateParams"})
    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        LogUtil.e(tag, "instantiateItem: " + position);
        View view = null;
        if ((mListData != null) && (mListData.size() > 0)) {
            // String类型 或者Integer类型的处理
            // :2:设置对象
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.base_banner, null);
            ImageView imageView = view.findViewById(R.id.iv_banner_image);
            // 此处为了兼容多种处理方式，以一个imageView的形式，把图片给传递出去，让用户手动选择怎么去处理
            if (mLoadListener != null) {
                mLoadListener.onLoadView(imageView, position, mListData.get(position));
            }
            // 整个view的点击事件
            View finalView = view;
            view.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(finalView, position - 1, mListData.get(position));
                }
            });
        }
        // 先移除，后添加
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent == null) {
                container.addView(view);
                LogUtil.e("移除了相同的view");
            }
        }
        return view;
    }

    /**
     * 销毁Item方法，以后都这么写
     * viewpager会自动销毁不用的Item，我们在销毁Item的时候也要销毁view
     */
    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        // super.destroyItem(container, position, object);
        // :从容器中溢出view
        container.removeView((View) object);
        LogUtil.e(tag, "destroyItem: " + position);
    }

    /**
     * 加载的时候,自己去设置图片，
     */
    public void setBannerLoadListener(BannerLoadListener<T> loadListener) {
        this.mLoadListener = loadListener;
    }

    public void setItemClickListener(BannerItemClickListener<T> itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

}
