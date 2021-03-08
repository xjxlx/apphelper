package android.helper.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.helper.utils.photo.GlideUtil;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 自定义BannerView
 */
public class BannerView extends ViewGroup {

    private final static int CODE_WHAT_LOOP = 10000;// 轮播的what

    private Activity activity;

    /**
     * 设置数据：使用View
     */
    private List<View> mViewList;
    /**
     * 设置数据：数据的集合
     */
    private List<String> mPathList;

    private int[] mResourceList;

    /**
     * 设置数据：Fragment的集合
     */
    private List<Fragment> mFragmentList;

    private int childCount;
    private int measuredWidth;

    private GestureDetector mDetector;
    private int mPosition; // 当前的banner角标
    private float mStartX;
    private int mPreset; // 预设的值
    private BannerLoadListener mLoadFinish;
    private LinearLayout mIndicatorLayout;// 指示器的父布局
    private boolean isLoadDataFinish;// 数据是否已经加载完成了
    private int mIndicatorInterval;// 指示器的间距
    private int mIndicatorResource;// 指示器的资源
    private boolean mLoop;// 是否轮播
    private long mLoopDelayMillis = 3 * 1000; // 轮询的间隔,默认三秒的间隔
    private Scroller mScroller;

    public BannerView(Context context) {
        super(context);
        initView(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (context instanceof Activity) {
            activity = (Activity) context;
        }

        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollBy((int) distanceX, 0);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        // 数据加载完成后的监听
        setLoadFinish(this::addIndicator);

        // 滑动组件
        mScroller = new Scroller(activity);
    }

    public void setDateListView(List<View> viewList) {
        this.mViewList = viewList;
        if (mViewList != null && mViewList.size() > 0) {
            for (int i = 0; i < mViewList.size(); i++) {
                View view = mViewList.get(i);
                if (view != null) {
                    addView(view);
                }
            }
            loadFinish();
        }
    }

    public void setDataListPath(List<String> viewList) {
        this.mPathList = viewList;
        if (mPathList != null && mPathList.size() > 0) {
            for (int i = 0; i < mPathList.size(); i++) {
                String path = mPathList.get(i);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                if ((!TextUtils.isEmpty(path)) && (activity != null)) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(params);
                    GlideUtil.loadView(activity, path, imageView);

                    addView(imageView);
                }
            }
            loadFinish();
        }
    }

    /**
     * 设置本地的资源id
     */
    public void setDateListResource(int[] resourceList) {
        this.mResourceList = resourceList;
        if (mResourceList != null && resourceList.length > 0) {
            for (int resourceId : resourceList) {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                if (((resourceId != 0)) && (activity != null)) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(params);
                    imageView.setImageResource(resourceId);
                    addView(imageView);
                }
            }
            loadFinish();
        }
    }

    public void setDateListFragment(List<Fragment> viewList) {
        this.mFragmentList = viewList;
        if (mFragmentList != null && mFragmentList.size() > 0) {
            for (int i = 0; i < mFragmentList.size(); i++) {
                Fragment fragment = mFragmentList.get(i);
                if (fragment != null) {
                    View view = fragment.getView();
                    if (view != null) {
                        addView(view);
                    }
                }
            }
            loadFinish();
        }
    }

    public List<View> getViewList() {
        return mViewList;
    }

    public List<String> getDataList() {
        return mPathList;
    }

    public List<Fragment> getFragmentList() {
        return mFragmentList;
    }

    public int[] getResourceList() {
        return mResourceList;
    }

    private int getPositionForScrollX(int scrollX) {
        return scrollX / measuredWidth;
    }

    private int getOffsetX(int scrollX) {
        return scrollX % measuredWidth;
    }

    public void reset() {
        scrollTo(0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = 0;
        childCount = getChildCount();

        int width = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {

                // 测量子view的 宽高
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec);

                int height = childAt.getMeasuredHeight();
                if (measuredHeight < height) {
                    measuredHeight = height;
                }
            }
        }
        setMeasuredDimension(width, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        measuredWidth = getMeasuredWidth();
        // 预设的值
        mPreset = measuredWidth / 3;
        int left = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                int height = childAt.getMeasuredHeight();
                childAt.layout(left, 0, (measuredWidth + left), height);
                left += measuredWidth;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // 按下的时候，暂停轮播
                onStopLoop();
                break;

            case MotionEvent.ACTION_MOVE:

                int stopScrollX = getScrollX();
                // 第一页禁止右划
                if (stopScrollX <= 0) {
                    scrollTo(0, 0);
                } else if (stopScrollX >= ((childCount - 1) * measuredWidth)) {
                    // 最后一页禁止左滑
                    scrollTo((childCount - 1) * measuredWidth, 0);
                }

                break;

            case MotionEvent.ACTION_UP:
                // 抬起的时候，开始轮播
                onRestartBanner();

                float eventX = event.getX();
                //  是否是向左滑动
                boolean isToLeft = (eventX - mStartX) < 0;

                int scrollX = getScrollX();
                int position = getPositionForScrollX(scrollX);
                int offsetX = getOffsetX(scrollX);

                if (isToLeft) {
                    if (offsetX >= mPreset) {
                        if (position < childCount - 1) {
                            mPosition = position + 1;
                        } else {
                            mPosition = position;
                        }
                    } else {
                        mPosition = position;
                    }
                } else {
                    if ((measuredWidth - offsetX) >= mPreset) {
                        mPosition = position;
                    } else {
                        mPosition = position + 1;
                    }
                }

                // scrollTo(mPosition * measuredWidth, 0);

                /*
                 * 使用平滑的滑动方式
                 * 参1：起始x轴位置
                 * 参2：起始y轴的位置
                 * 参3：偏移的x轴位置（目标终点位置 - 起始的x轴位置）
                 * 参4：偏移的y轴位置
                 * 参5：持续的时间
                 */
                mScroller.startScroll(scrollX, 0, (mPosition * measuredWidth) - scrollX, 0);
                invalidate();
                break;
        }
        return true;
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
     * @param layout     指示器的父类布局
     * @param interval   指示器的间隔
     * @param resourceId 指示器的资源
     */
    public void setIndicatorView(@Nullable LinearLayout layout, int interval, int resourceId) {
        this.mIndicatorLayout = layout;
        this.mIndicatorInterval = interval;
        this.mIndicatorResource = resourceId;
        addIndicator();
    }

    /**
     * 添加指示器
     */
    private void addIndicator() {
        if (mIndicatorLayout != null && isLoadDataFinish) {
            if (mIndicatorLayout.getChildCount() > 0) {
                return;
            }
            int childCount = getChildCount();
            if (childCount > 0) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mIndicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
                for (int i = 0; i < childCount; i++) {
                    ImageView imageView = new ImageView(activity);
                    params.leftMargin = mIndicatorInterval;
                    imageView.setLayoutParams(params);
                    imageView.setAdjustViewBounds(true);
                    imageView.setImageResource(mIndicatorResource);
                    mIndicatorLayout.addView(imageView);
                }
            }
        }
    }

    /**
     * 数据加载完成
     */
    private void loadFinish() {
        isLoadDataFinish = true;
        if (mLoadFinish != null) {
            mLoadFinish.onDataLoadFinish();
        }
    }

    private void setLoadFinish(BannerLoadListener loadFinish) {
        this.mLoadFinish = loadFinish;
    }

    interface BannerLoadListener {
        void onDataLoadFinish();
    }

    /**
     * @param loop 是否无限轮播
     */
    public void setLoop(boolean loop, long delayMillis) {
        this.mLoop = loop;
        if (delayMillis > 0) {
            this.mLoopDelayMillis = delayMillis;
        }

        if (loop) {
            // 发送轮播
            Message message = mHandler.obtainMessage();
            message.what = CODE_WHAT_LOOP;
            mHandler.sendMessage(message);
        } else {
            // 移除轮播
            mHandler.removeMessages(CODE_WHAT_LOOP);
        }
    }

    /**
     * 重新开始轮播
     */
    public void onRestartBanner() {
        if (mLoop) {
            // 发送轮播
            Message message = mHandler.obtainMessage();
            message.what = CODE_WHAT_LOOP;
            mHandler.sendMessageDelayed(message, mLoopDelayMillis);
        }
    }

    /**
     * 暂停轮播
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

                if (mPosition < childCount - 1) {
                    mPosition++;
                } else {
                    mPosition = 0;
                }
                // 移动到下一个view
                scrollTo((mPosition * measuredWidth), 0);

                // 间隔一定的时间后，再次去轮询，由用户自己去指定时间
                Message message = obtainMessage();
                message.what = CODE_WHAT_LOOP;
                sendMessageDelayed(message, mLoopDelayMillis);
            }
        }
    };

}
