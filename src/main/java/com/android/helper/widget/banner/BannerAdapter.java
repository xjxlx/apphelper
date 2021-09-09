package com.android.helper.widget.banner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.helper.R;
import com.android.helper.common.CommonConstants;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.photo.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * banner的图片适配器，适用于加载单独的图片
 */
public class BannerAdapter<T> extends PagerAdapter {

    private final List<T> mListData;
    private BannerLoadListener<T> mLoadListener;
    private BannerItemClickListener<T> mItemClickListener;// 点击事件
    private BannerView mBannerView;

    public BannerAdapter(List<T> listData) {
        mListData = listData;
    }

    @Override
    public int getCount() {
        if (mListData.size() == 1) {
            return mListData.size();
        }
        return CommonConstants.BANNER_LENGTH;
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    //:初始化每个Item的实布局，类似于getview
    // :viewpager会默认加载三个布局，上一页，本业，和下一页，其他页面会自动销毁，防止内存溢出
    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        LogUtil.e("instantiateItem:");
        View view = null;
        //:1:首先要拿到position的位置，为了避免角标越界，进行取余运算
        if (mListData != null) {
            position = position % mListData.size();
            //:2:设置对象
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.base_banner, null);

            ImageView imageView = view.findViewById(R.id.iv_banner_image);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            if ((mBannerView != null) && (params != null)) {
                int width = mBannerView.getWidth();
                int height = mBannerView.getHeight();

                // 设置view的宽高
                params.width = width;
                params.height = height;
                imageView.setLayoutParams(params);
                imageView.setAdjustViewBounds(true);
            }

            // 此处为了兼容多种处理方式，以一个imageView的形式，把图片给传递出去，让用户手动选择怎么去处理
            T t = mListData.get(position);
            if (mLoadListener != null) {
                mLoadListener.onLoadView(imageView, t);
            }
            if (t instanceof String) {
                String url = (String) t;
                GlideUtil.loadViewCenterCrop(container.getContext(), imageView, url);
            }

            int finalPosition = position;
            view.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(null, imageView, finalPosition, t);
                }
            });

            container.addView(view);
        }
        assert view != null;
        return view;
    }

    /**
     * 销毁Item方法，以后都这么写
     * viewpager会自动销毁不用的Item，我们在销毁Item的时候也要销毁view
     */
    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        //  super.destroyItem(container, position, object);
        //:从容器中溢出view
        container.removeView((View) object);
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

    /**
     * 设置轮播图的对象，用来获取轮播图的信息
     */
    public void setParentView(BannerView bannerView) {
        mBannerView = bannerView;
    }
}
