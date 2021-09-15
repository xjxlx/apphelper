package com.android.helper.widget.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.LogUtil;

/**
 * BannerView的指示器，用于配合Banner的联动
 */
public class BannerIndicator extends LinearLayout {

    private float mInterval;
    private int mSelectorResource, mUnSelectedResource;
    private int mMaxWidth, mMaxHeight;
    private BannerView mBannerView;
    private int mWidth, mHeight;
    private String TAG = "BannerIndicator ---> ";

    public BannerIndicator(Context context) {
        super(context);
        initView(context, null);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LogUtil.e(TAG, "initView:");

        // 固定设置横向
        setOrientation(LinearLayout.HORIZONTAL);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator);
            // 获取间距
            mInterval = typedArray.getDimension(R.styleable.BannerIndicator_bi_interval, 0);
            // 选中的图形
            mSelectorResource = typedArray.getResourceId(R.styleable.BannerIndicator_bi_selector_resource, 0);
            // 未选中的图形
            mUnSelectedResource = typedArray.getResourceId(R.styleable.BannerIndicator_bi_unselected_resource, 0);

            typedArray.recycle();
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtil.e(TAG, "onMease:");

        if (isInEditMode()) {
            // 解决预览模式不显示的问题
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            // 如果是wrap_content模式的话，就显示高度为0
            if (mode == MeasureSpec.AT_MOST) {
                mMaxHeight = 0;
            } else {
                mMaxHeight = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
            }
            mMaxWidth = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        } else {
            // 如果宽高为0，则重新去测量一遍
            int childCount = getChildCount();
            if (childCount > 1) { // 只有数量大于1，才会去显示，否则不去显示
                // 如果没有测量出宽高，则重新测量一边
                if (mWidth <= 0 || mHeight <= 0) {
                    // 如果没有设置宽高，就自己去计算
                    View childAt = getChildAt(0);
                    if (childAt != null) {
                        measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
                        mWidth = childAt.getMeasuredWidth();
                        mHeight = childAt.getMeasuredHeight();
                    }
                }

                // 总体宽度 = view 的个数 * 宽度 + view的个数 -1 * 间距
                mMaxWidth = (int) (((childCount - 1) * mInterval) + (mWidth * childCount));
                mMaxHeight = mHeight;

                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 结合viewPager
     */
    public void setViewPager(BannerView bannerView, int count) {
        this.mBannerView = bannerView;
        LogUtil.e(TAG, "setViewPager: ");

        // 先清空，在加入
        int childCount = getChildCount();
        if (childCount > 0) {
            removeAllViews();
        }

        // 添加指示器
        if (count > 1) { // 只有数据大于1的时候，才去添加，否则就不添加数据
            for (int i = 0; i < count - 2; i++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                ImageView imageView = new ImageView(getContext());

                //:从第二个开始设置
                if (mInterval > 0) {
                    if (i > 0) {
                        // 设置属性
                        params.leftMargin = (int) mInterval;
                    }
                }

                imageView.setLayoutParams(params);
                //:3:把view添加到viewGroup中
                addView(imageView);
            }

            // 默认的选中
            onPageSelected(0);

            // 设置数据后，重新测量view的宽高
            post(this::requestLayout);

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtil.e(TAG, "onSizeChange:  w:" + w + "  h:" + h);
    }

    /**
     * viewPager选中时候的状态监听
     */
    public void onPageSelected(int position) {
        LogUtil.e(TAG, "onPageSelected:" + position);

        if (mBannerView != null) {
            int childCount = getChildCount();
            if (childCount > 0) {

                // 重新设置数据
                setSelector(position);

                for (int i = 0; i < childCount; i++) {
                    View childAt = getChildAt(i);
                    // 设置点击事件
                    int finalI = i;
                    childAt.setOnClickListener(v -> {
                        // 先停止轮询的事件
                        mBannerView.onStop();
                        // 跳转到指定的页面去，因为banner最左侧加了数据，所以正确的角标应该是当前的角标+1
                        mBannerView.setCurrentItem(finalI + 1);
                        mBannerView.onStart();
                    });
                }
            }
        }
    }

    /**
     * 设置选中和没有选中的资源
     */
    private void setSelector(int position) {
        //:首先将所有的子元素全部都设置为不点击
        if (mUnSelectedResource != 0) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt instanceof ImageView) {
                    ImageView imageView = (ImageView) childAt;
                    imageView.setImageResource(mUnSelectedResource);
                }
            }
        }

        //:当选中某个元素的时候设置为true
        if (mSelectorResource != 0) {
            View childAt = getChildAt(position);
            if (childAt instanceof ImageView) {
                ImageView imageView = (ImageView) childAt;
                imageView.setImageResource(mSelectorResource);
            }
        }
    }

}
