package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.helper.interfaces.listener.BannerChangeListener;
import android.helper.utils.ConvertUtil;
import android.helper.utils.LogUtil;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import org.jetbrains.annotations.Nullable;

/**
 * 自定义BannerView
 */
public class BannerView extends ViewGroup {

    private final int CODE_WHAT_LOOP = 10000;// 自动轮播的what
    private final LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private Context mContext;
    private int mChildCount;// 数据源的总长度
    private int mMeasuredWidth;// 屏幕的宽度
    private int mScrollPreset; // 滑动预设的值，超过这个值，就会去滑动的下一页
    private GestureDetector mDetector;
    private Scroller mScroller;
    private int mPosition;//当前角标的position
    private float mStartX;// 按下的X轴坐标
    private boolean isToLeft;// 是否向左滑动
    private int mPositionDown = 0;// 按下的角标
    private boolean isLoadFinish;// 数据是否已经加载完毕
    private LinearLayout mIndicatorParentLayout;// 指示器的布局
    private int mIndicatorInterval = (int) ConvertUtil.toDp(5f);//指示器的间隔
    private int mIndicatorResource;//指示器的资源
    private boolean mAutoLoop = true;// 是否自动轮播
    private long mAutoLoopInterval = 3 * 1000;// 默认自动轮播的间隔
    private BannerChangeListener mBannerChangeListener;

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
                ImageView imageView = getImageViewForResource(mParams, resourceList[mChildCount - 1]);
                addView(imageView);
            }

            // 添加数据
            for (int i = 0; i < mChildCount; i++) {
                int resourceId = resourceList[i];
                ImageView imageView = getImageViewForResource(mParams, resourceId);
                addView(imageView);
            }

            if (mChildCount > 1) {
                // 添加最右侧的图片
                ImageView imageView = getImageViewForResource(mParams, resourceList[0]);
                addView(imageView);
            }

            dataLoadFinish();
        }
    }

    /**
     * 数据加载完成后的逻辑
     */
    private void dataLoadFinish() {
        isLoadFinish = true;
        addIndicatorView();

        // 初始化数据
        setCurrentItem(mPosition);
    }

    /**
     * @param resource 本地图片的资源
     * @return 返回一个imageView，并设置本地的资源
     */
    private ImageView getImageViewForResource(ViewGroup.LayoutParams params, int resource) {
        ImageView imageView = new ImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(params);
        imageView.setImageResource(resource);
        return imageView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 把手机滑动器添加给我触摸事件
        mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mPositionDown = getPositionForScrollX(getScrollX());

                // 按下的时候，停止轮播
                onStopLoop();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动的时候，停止轮播
                onStopLoop();

                float endX = event.getX();
                isToLeft = (endX - mStartX) < 0;

                break;

            case MotionEvent.ACTION_UP:
                // 抬起手指的时候，开始轮播
                onRestartLoop();

                int scrollX = getScrollX();
                int position = getPositionForScrollX(scrollX);
                int offsetForScrollX = getOffsetForScrollX(scrollX);
                // 按下和抬起的是否是一个角标
                // 按下和抬起的是否是一个对象
                boolean isEqualsDownAndUp = (mPositionDown == position);

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
                    // 只有满足了第一个角标，且点击和按下的都是角标0的时候，才触发滑动到最后一个item的效果
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

        // 更改指示器的效果
        if ((mIndicatorParentLayout != null) && (mIndicatorParentLayout.getChildCount() > 0)) {
            for (int i = 0; i < mIndicatorParentLayout.getChildCount(); i++) {
                View childAt = mIndicatorParentLayout.getChildAt(i);
                childAt.setSelected(i == position);
            }
        }

        // 选中事件的回调
        if (mBannerChangeListener != null) {
            mBannerChangeListener.onSelector(position);
        }

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

    /**
     * @param indicatorParent 指示器的父布局
     * @param interval        指示器之间的间距
     * @param resource        指示器的资源
     */
    public void setIndicatorView(@Nullable LinearLayout indicatorParent, int interval, int resource) {
        this.mIndicatorParentLayout = indicatorParent;
        if (interval > 0) {
            this.mIndicatorInterval = interval;
        }
        if (resource != 0) {
            this.mIndicatorResource = resource;
        }

        addIndicatorView();
    }

    /**
     * 添加指示器
     */
    private void addIndicatorView() {
        if ((isLoadFinish) && (mIndicatorParentLayout != null) && (mChildCount > 0)) {
            // 如果添加过了就不在添加了
            if (mIndicatorParentLayout.getChildCount() > 0) {
                return;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = mIndicatorInterval;

            for (int i = 0; i < mChildCount; i++) {
                ImageView imageView = getImageViewForResource(params, mIndicatorResource);
                imageView.setSelected(i == mPosition);
                mIndicatorParentLayout.addView(imageView);
            }
        }
    }

    /**
     * @param loop         是否自动轮播
     * @param loopInterval 自动轮播的时间间隔，单位毫秒
     */
    public void setAutoLoop(boolean loop, long loopInterval) {
        this.mAutoLoop = loop;
        if (loopInterval > 0) {
            this.mAutoLoopInterval = loopInterval;
        }

        // 开始发送轮播的消息
        onRestartLoop();
    }

    /**
     * 开始发送轮播的消息
     */
    public void onRestartLoop() {
        // 重新发送
        if (mAutoLoop) {
            Message message = mHandler.obtainMessage();
            message.what = CODE_WHAT_LOOP;
            mHandler.sendMessageDelayed(message, mAutoLoopInterval);
        }
    }

    /**
     * 停止轮询
     */
    public void onStopLoop() {
        mHandler.removeMessages(CODE_WHAT_LOOP);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CODE_WHAT_LOOP) {

                int position = getPositionForScrollX(getScrollX());
                if (mAutoLoop) {
                    if (mPosition < mChildCount - 1) {
                        mPosition = position + 1;
                    } else {
                        mPosition = 0;
                    }
                }
                setCurrentItem(mPosition);

                // 重新发送
                Message message = obtainMessage();
                message.what = CODE_WHAT_LOOP;
                sendMessageDelayed(message, mAutoLoopInterval);
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.e("onDetachedFromWindow");

        onStopLoop();
    }

    public void setBannerChangeListener(BannerChangeListener bannerChange) {
        this.mBannerChangeListener = bannerChange;
    }

    public interface BannerClickListener {
        void onClick(View view, int position);
    }

    /**
     * view的点击事件
     */
    public void setBannerClickListener(BannerClickListener bannerClickListener) {
        if (bannerClickListener != null) {
            for (int i = 1; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt != null) {
                    childAt.setOnClickListener(v -> bannerClickListener.onClick(childAt, mPosition));
                }
            }
        }
    }

}
