package com.android.helper.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.android.helper.R;

/**
 * BannerView的指示器，用于配合Banner的联动
 */
public class BannerIndicator extends LinearLayout {

    private float mInterval;
    private Drawable mDrawable;
    private int mMaxHeight;
    private int mCount;

    public BannerIndicator(Context context) {
        super(context);
        initView(context, null);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        // 固定设置横向
        setOrientation(LinearLayout.HORIZONTAL);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator);
            // 获取间距
            mInterval = typedArray.getDimension(R.styleable.BannerIndicator_bi_interval, 0);
            // 获取图形
            mDrawable = typedArray.getDrawable(R.styleable.BannerIndicator_bi_drawable);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;

        int size = getChildCount();
        if (size > 0) {
            View child = getChildAt(0);
            // 计算高度
            width = child.getMeasuredHeight();
            // 计算宽度
            mMaxHeight = child.getMeasuredWidth();
        }

        // 总体宽度 = view 的个数 * 宽度 + view的个数 -1 * 间距
        int maxWidth = (int) (((size - 1) * mInterval) + (width * size));

        setMeasuredDimension(maxWidth, mMaxHeight);
    }

    /**
     * 结合viewPager
     */
    public void setViewPager(BannerView bannerView, int count) {
        mCount = count;
        if (bannerView != null) {
            //  添加view
            for (int i = 0; i < count; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ImageView imageView = new ImageView(bannerView.getContext());
                if (mDrawable != null) {
                    imageView.setImageDrawable(mDrawable);
                    //:设置控件不可以按下
                    imageView.setEnabled(false);
                }

                //:从第二个开始设置
                if (mInterval > 0) {
                    if (i > 0) {
                        params.leftMargin = (int) mInterval;
                    }
                }

                // 设置属性
                imageView.setLayoutParams(params);
                //:3:把view添加到viewGroup中
                addView(imageView);
            }

            // 监听滑动
            bannerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    //:防止角标越界
                    position = position % mCount;

                    //:首先将所有的子元素全部都设置为不点击
                    for (int i = 0; i < getChildCount(); i++) {
                        View childAt = getChildAt(i);
                        childAt.setEnabled(false);
                    }

                    //:当选中某个元素的时候设置为true
                    getChildAt(position).setEnabled(true);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
}
