package android.helper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 自定义BannerView
 */
public class BannerView extends ViewGroup {

    private final LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private Context mContext;
    private int mChildCount;// 数据源的总长度
    private int mMeasuredWidth;// 屏幕的宽度

    public BannerView(Context context) {
        super(context);
        initView(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取view的宽度
        mMeasuredWidth = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));
        // 动态设置view的高度
        int measuredHeight = 0;

        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != null) {
                    // 测量子view的高度
                    measureChildren(widthMeasureSpec, heightMeasureSpec);
                    int height = childAt.getMeasuredHeight();
                    if (height >= measuredHeight) {
                        measuredHeight = height;
                    }
                }
            }
        }
        // 设置view的宽高
        setMeasuredDimension(mMeasuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != null) {
                    if (i == 0) {
                        // 最左侧view的处理
                        childAt.layout(-mMeasuredWidth, 0, 0, childAt.getMeasuredHeight());
                    } else if (i == (childCount - 1)) {
                        // 最右侧view的处理
                        childAt.layout((mChildCount * mMeasuredWidth), 0, ((mChildCount + 1) * mMeasuredWidth), childAt.getMeasuredHeight());
                    } else {
                        childAt.layout(((i - 1) * mMeasuredWidth), 0, (i * mMeasuredWidth), childAt.getMeasuredHeight());
                    }
                }
            }
        }
    }

    /**
     * 设置本地数据集合
     *
     * @param resourceList 本地的数据数组
     */
    public void setDataList(int[] resourceList) {
        if (resourceList != null && resourceList.length > 0) {
            this.mChildCount = resourceList.length;

            if (mChildCount > 1) {
                // 添加最左侧的图片
                ImageView imageView = getImageViewForResource(resourceList[mChildCount - 1]);
                addView(imageView);
            }

            // 添加数据
            for (int i = 0; i < mChildCount; i++) {
                int resourceId = resourceList[i];
                ImageView imageView = getImageViewForResource(resourceId);
                addView(imageView);
            }

            if (mChildCount > 1) {
                // 添加最右侧的图片
                ImageView imageView = getImageViewForResource(resourceList[0]);
                addView(imageView);
            }
        }
    }

    /**
     * @param resource 本地图片的资源
     * @return 返回一个imageView，并设置本地的资源
     */
    private ImageView getImageViewForResource(int resource) {
        ImageView imageView = new ImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(mParams);
        imageView.setImageResource(resource);
        return imageView;
    }

}
