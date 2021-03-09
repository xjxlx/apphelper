package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * 自定义BannerView
 */
public class BannerView extends ViewGroup {

    private final LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private Context mContext;
    private int mChildCount;// 数据源的总长度
    private int mMeasuredWidth;// 屏幕的宽度
    private int mScrollPreset; // 滑动预设的值，超过这个值，就会去滑动的下一页
    private GestureDetector mDetector;
    private Scroller mScroller;
    private float mStartX;// 按下的X轴坐标
    private boolean isToLeft;// 是否向左滑动
    private int mPosition;//当前角标的position
    private boolean isEqualsDownAndUp;// 按下和抬起的是否是一个对象

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

        // 滑动计算工具
        mScroller = new Scroller(mContext);

        // 手势滑动器
        mDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // 可以滑动
                scrollBy((int) distanceX, 0);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取view的宽度
        mMeasuredWidth = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));
        // 设置预设的值
        mScrollPreset = mMeasuredWidth / 4;
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

    int positionDown = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 把手机滑动器添加给我触摸事件
        mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                positionDown = getPositionForScrollX(getScrollX());
                break;

            case MotionEvent.ACTION_MOVE:
                float endX = event.getX();
                isToLeft = (endX - mStartX) < 0;

                break;

            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                int position = getPositionForScrollX(scrollX);
                int offsetForScrollX = getOffsetForScrollX(scrollX);
                // 按下和抬起的是否是一个角标
                isEqualsDownAndUp = (positionDown == position);

                if (isToLeft) { // 向左滑动
                    if (offsetForScrollX >= mScrollPreset) { // 大于预设的值
                        if (position < mChildCount - 1) {
                            mPosition = position + 1;
                        } else {
                            mPosition = 0; // 如果滑到了最后面的那个位置，则把角标给改成第一个
                        }
                    } else { // 小于预设的值
                        mPosition = position;
                    }

                } else { // 向右滑动

                    if (position == 0 && isEqualsDownAndUp) {
                        if (offsetForScrollX >= mScrollPreset) {
                            mPosition = mChildCount - 1;
                        } else {
                            mPosition = position;
                        }
                    } else {
                        if ((mMeasuredWidth - offsetForScrollX) >= mScrollPreset) {
                            mPosition = position;
                        } else {
                            mPosition = position + 1;
                        }
                    }
                }

                // 手指抬起时候的滑动
                setCurrentItem(mPosition);
                break;
        }

        return true;
    }

    /**
     * @return 根据滑动的值去获取当前的position
     */
    private int getPositionForScrollX(int scrollX) {
        if (scrollX > 0) {
            return scrollX / mMeasuredWidth;
        }
        return 0;
    }

    /**
     * @return 根据滑动的值获取偏移的量
     */
    private int getOffsetForScrollX(int scrollX) {
        return Math.abs(scrollX % mMeasuredWidth);
    }

    /**
     * 设置当前的item
     */
    public void setCurrentItem(int position) {
        int scrollX = getScrollX();

        // 使用匀速滑动的效果
        mScroller.startScroll(scrollX, 0, ((mMeasuredWidth * position) - scrollX), 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }
}
