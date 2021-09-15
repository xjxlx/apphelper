package com.android.helper.widget.banner;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.helper.R;
import com.android.helper.utils.LogUtil;

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
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    //:初始化每个Item的实布局，类似于getview
    // :viewpager会默认加载三个布局，上一页，本业，和下一页，其他页面会自动销毁，防止内存溢出
    @SuppressLint({"SetTextI18n", "InflateParams"})
    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        // LogUtil.e("instantiateItem:");

        View view = null;
        if ((mListData != null) && (mListData.size() > 0)) {
            // 获取其中一个数据的类型
            T temp = mListData.get(0);
            if (temp instanceof View) {
                // View类型的处理
                T t1 = mListData.get(position);
                if (t1 instanceof View) {
                    view = (View) t1;
                    // 该类型，不用去设置view的加载数据了，直接去返回整个view
//                    if (mLoadListener != null) {
//                        mLoadListener.onLoadView(view, position, mListData.get(position));
//                    }
                }
            } else if ((temp instanceof String) || (temp instanceof Integer)) {
                // String类型 或者Integer类型的处理

                //:2:设置对象
                view = LayoutInflater.from(container.getContext()).inflate(R.layout.base_banner, null);

                ImageView imageView = view.findViewById(R.id.iv_banner_image);
                TextView tvPosition = view.findViewById(R.id.tv_position);
                tvPosition.setText("" + position);

                // 此处为了兼容多种处理方式，以一个imageView的形式，把图片给传递出去，让用户手动选择怎么去处理
                if (mLoadListener != null) {
                    mLoadListener.onLoadView(imageView, position, mListData.get(position));
                }

                // 整个view的点击事件
                View finalView = view;
                view.setOnClickListener(v -> {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(finalView, position, mListData.get(position));
                    }
                });
            }

            // 先移除，后添加
            int childCount = container.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View child = container.getChildAt(i);
                    if (child == view) {
                        container.removeView(child);
                        // 移除了相同的view
                        LogUtil.e("移除了相同的view");
                    }
                }
            }

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
