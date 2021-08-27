package com.android.helper.widget.banner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.helper.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * banner的图片适配器，适用于加载单独的图片
 */
public class BannerAdapter extends PagerAdapter {

    private List<Object> mListData;
    private BannerLoadListener mLoadListener;

    public BannerAdapter(List<Object> listData) {
        mListData = listData;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    //:初始化每个Item的实布局，类似于getview
    // :viewpager会默认加载三个布局，上一页，本业，和下一页，其他页面会自动销毁，防止内存溢出
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        //:1:首先要拿到position的位置，为了避免角标越界，进行取余运算
        if (mListData != null) {
            position = position % mListData.size();
            //:2:设置对象
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.base_banner, null);
            ImageView imageView = view.findViewById(R.id.iv_banner_image);
            // 此处为了兼容多种处理方式，以一个imageView的形式，把图片给传递出去，让用户手动选择怎么去处理
            Object object = mListData.get(position);
            if (mLoadListener != null) {
                mLoadListener.onLoadView(imageView, object);
            }
            container.addView(view);
        }
        return view;
    }

    /**
     * 销毁Item方法，以后都这么写
     * viewpager会自动销毁不用的Item，我们在销毁Item的时候也要销毁view
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //  super.destroyItem(container, position, object);
        //:从容器中溢出view
        container.removeView((View) object);
    }

    /**
     * 加载的时候,自己去设置图片，
     */
    public void setBannerLoadListener(BannerLoadListener loadListener) {
        this.mLoadListener = loadListener;
    }

}
