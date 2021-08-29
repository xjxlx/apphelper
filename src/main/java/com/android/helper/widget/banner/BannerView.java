package com.android.helper.widget.banner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.android.helper.common.CommonConstants;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义轮播图，可以实现自动滚动
 * 使用方式：
 * 一：如果是xml创建的view，那么需要走两步
 * 1：调用 {@link BannerView#createBuild(Builder)} 创建一个Builder,用来设置各种数据
 * 2：调用{@link BannerView#start(Activity)}方法去开启轮播
 */
public class BannerView extends ViewPager implements BaseLifecycleObserver {
    private final int CODE_WHAT_LOOP = 1000;// 轮询的code值
    private int CODE_LOOP_INTERVAL;// 轮询的时间间隔，默认5s
    private boolean mAutoLoop = true;// 是否开启轮询，默认开启
    private List<Fragment> mListFragmentData;// fragment的集合
    private List<Object> mListImageData;// 图片的集合
    private int mImageType;// 1：普通的ImageView,2:fragment类型的
    private BannerLoadListener mLoadListener;// 加载本地图片页面的回调
    private BannerIndicator mIndicator;
    private int mMaxWidth, mMaxHeight;
    private final Map<Integer, Integer> mMapHeight = new HashMap<>(); // 用来存储每个item的高度
    private float mStartX = 0;
    private int mCurrent;// 当前的position
    private BannerView mBannerView;

    public BannerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 如果是new出来的类型，则必须使用这个对象
     *
     * @param context 上下文
     * @param builder 构建的build对象
     */
    public BannerView(@NonNull @NotNull Context context, Builder builder) {
        super(context);
        mImageType = builder.mImageType;
        mAutoLoop = builder.mAutoLoop;
        CODE_LOOP_INTERVAL = builder.mInterval;

        mListFragmentData = builder.mListFragmentData;
        mListImageData = builder.mListImageData;
        mLoadListener = builder.loadListener;
        mIndicator = builder.mIndicator;
        initView();
    }

    /**
     * @param builder 参数依赖的builder
     * @return 如果是一个xml形式的引用，则必须设置一个builder
     */
    public BannerView createBuild(Builder builder) {
        mImageType = builder.mImageType;
        mAutoLoop = builder.mAutoLoop;
        CODE_LOOP_INTERVAL = builder.mInterval;

        mListFragmentData = builder.mListFragmentData;
        mListImageData = builder.mListImageData;
        mLoadListener = builder.loadListener;
        mIndicator = builder.mIndicator;

        initView();
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 0;

        if (isInEditMode()) { // 预览模式
            // 假如还没有数据，就用指示的高度去预览
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            // 如果是wrap_content模式的话，就显示高度为0
            if (mode == MeasureSpec.AT_MOST) {
                height = 0;
            } else {
                height = getDefaultSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
            }
        } else { // 正常模式

            if (mImageType == 1) { // 图片模式
                if ((mListImageData != null) && (mListImageData.size() > 0)) {
                    int currentItem = getCurrentItem();
                    // 求出当前item是第几列
                    int position = currentItem % mListImageData.size();

                    // 获取当前view的高度
                    Integer heightForMap = mMapHeight.get(position);
                    if ((heightForMap != null) && (heightForMap != 0)) {
                        // 直接去赋值
                        height = heightForMap;
                    } else {
                        // 获取当前的view高度
                        View childAt = getChildAt(position);
                        if (childAt != null) {
                            if (childAt instanceof ViewGroup) {
                                ViewGroup viewGroup = (ViewGroup) childAt;
                                if (viewGroup.getChildCount() > 0) {
                                    View child = viewGroup.getChildAt(0);
                                    if (child != null) {
                                        height = child.getMeasuredHeight();
                                        //  存入的高度
                                        mMapHeight.put(position, height);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (mImageType == 2) { // fragment模式
                if ((mListFragmentData != null) && (mListFragmentData.size() > 0)) {

                    LogUtil.e("current:------>" + mCurrent);
                    Fragment fragment = mListFragmentData.get(mCurrent);

                    Integer integer = mMapHeight.get(mCurrent);
                    if ((integer != null) && (integer > 0)) { // 如果是能直接拿到数据，就直接使用
                        height = integer;
                    } else { // 如果拿不到数据，就去自己获取
                        if (fragment != null) {
                            View view = fragment.getView();
                            if (view != null) {
                                // 先测量子View的大小
                                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);//为子View准备测量的参数
                                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
                                view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                                // 子View测量之后的宽和高
                                height = view.getMeasuredHeight();
                            }
                            if (height != 0) {
                                mMapHeight.put(mCurrent, height);
                            }
                        }
                    }
                }
            }
        }
        if (mMaxWidth == 0) {
            mMaxWidth = getDefaultSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        }
        mMaxHeight = height + getPaddingBottom() + getPaddingTop();

        LogUtil.e("------>width:" + mMaxWidth + "  height:" + mMaxHeight);
        setMeasuredDimension(1080, mMaxHeight);
    }

    private void initView() {
        mBannerView = this;
        // 设置按下的手势操作
        addPageListener();
    }

    /**
     * 开始播放轮播图
     *
     * @param fragment fragment类型的上下文
     */
    public void start(Fragment fragment) {
        // 感知fragment的生命周期
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
        }
        FragmentManager manager = fragment.getFragmentManager();
        setAdapter(manager);
    }

    /**
     * 开始播放轮播图
     *
     * @param activity activity 类型的上下文
     */
    public void start(Activity activity) {
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            Lifecycle lifecycle = fragmentActivity.getLifecycle();
            lifecycle.addObserver(this);

            FragmentManager manager = fragmentActivity.getSupportFragmentManager();

            setAdapter(manager);
        }
    }

    /**
     * 设置adapter，内部使用
     */
    private void setAdapter(FragmentManager manager) {
        if (mImageType == 2) {
            if ((mListFragmentData != null) && (mListFragmentData.size() > 0)) {
                BannerFragmentAdapter fragmentAdapter = new BannerFragmentAdapter(manager, mListFragmentData);
                setAdapter(fragmentAdapter);

                setOffscreenPageLimit(mListFragmentData.size());
                // 添加指示器
                addIndicator(mIndicator);
                // 设置当前默认的位置是在最中间的位置
                setCurrentItem(0);
            }
        } else if (mImageType == 1) {
            if ((mListImageData != null) && (mListImageData.size() > 0)) {
                BannerAdapter bannerAdapter = new BannerAdapter(mListImageData);
                if (mLoadListener != null) {
                    bannerAdapter.setBannerLoadListener(mLoadListener);
                }
                setAdapter(bannerAdapter);

                setOffscreenPageLimit(mListImageData.size());
                // 添加指示器
                addIndicator(mIndicator);
                // 设置当前默认的位置是在最中间的位置
                setCurrentItem(CommonConstants.BANNER_LENGTH / mListImageData.size());
            }
        }

        // 开始轮询播放
        sendMessage();
    }

    /**
     * 开始发送消息
     */
    private void sendMessage() {
        // 发送轮询
        if ((mHandler != null) && mAutoLoop) {
            mHandler.sendEmptyMessageDelayed(CODE_WHAT_LOOP, CODE_LOOP_INTERVAL);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addPageListener() {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mIndicator != null) {
                    mIndicator.onPageSelected(position);
                }

                if (mImageType == 2) {
                    mCurrent = position;
                } else if (mImageType == 1) {
                    mCurrent = position % mListImageData.size();
                }
                LogUtil.e("当前选中的position：" + mCurrent);
                // 重新测量当前view的宽高
                if (mBannerView != null) {
                    mBannerView.requestLayout();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 向左滑动
        boolean isLeft = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //:在按下的时候停止发送Handler消息
                onStop();
                mStartX = event.getX();
                return true;

            case MotionEvent.ACTION_MOVE:
                onStop();
                float endX = event.getX();
                float dx = endX - mStartX;

                if (dx > 0) {
                    LogUtil.e("向右滑动 dx :" + dx);
                    isLeft = false;
                } else {
                    LogUtil.e("向左滑动 dx: " + dx);
                    isLeft = true;
                }
                mStartX = endX;

                if (mImageType == 2) {
                    if (isLeft) {    // 向左滑动
                        if (mCurrent == (mListFragmentData.size() - 1)) {
                            setCurrentItem(0);
                        }
                    } else {  // 向右滑动
                        if (mCurrent == 0) {
                            setCurrentItem(mListFragmentData.size() - 1);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //:在抬起的时候继续发送消息
                sendMessage();
                break;
        }
        return super.onTouchEvent(event);// 此处不能消费掉事件, 否则viewpager自带滑动效果无法响应
    }

    /**
     * builder的设计模式
     */
    public static class Builder {
        private int mInterval = 3 * 1000;// 轮询的时间间隔，默认5s
        private boolean mAutoLoop = true;// 是否开启轮询，默认开启
        private List<Fragment> mListFragmentData;// fragment的集合
        private List<Object> mListImageData;// 图片的集合
        private int mImageType;// 1：普通的ImageView,2:fragment类型的
        private BannerLoadListener loadListener;// 加载本地图片页面的回调
        private BannerIndicator mIndicator; // 加载指示器

        /**
         * @param interval 轮询的时间间隔，默认是5s
         */
        public Builder setInterval(int interval) {
            mInterval = interval;
            return this;
        }

        /**
         * 是否开启轮询
         *
         * @param autoLoop true:自动轮询，false:不轮询
         */
        public Builder autoLoop(boolean autoLoop) {
            this.mAutoLoop = autoLoop;
            return this;
        }

        public Builder setImageData(List<Object> listImageData) {
            mListImageData = listImageData;
            mImageType = 1;
            return this;
        }

        public Builder setFragmentData(List<Fragment> fragmentList) {
            this.mListFragmentData = fragmentList;
            mImageType = 2;
            return this;
        }

        /**
         * 加载的时候,自己去设置图片，
         */
        public Builder setBannerLoadListener(BannerLoadListener loadListener) {
            this.loadListener = loadListener;
            return this;
        }

        /**
         * @return 设置指示器
         */
        public Builder addIndicator(BannerIndicator bannerIndicator) {
            this.mIndicator = bannerIndicator;
            return this;
        }

        public BannerView build(Context context) {
            return new BannerView(context, this);
        }
    }

    /**
     * 添加指示器
     *
     * @param indicator 指示器控件
     */
    private void addIndicator(BannerIndicator indicator) {
        if (indicator != null) {
            if (mImageType == 1) {
                indicator.setViewPager(this, mListImageData.size());
            } else if (mImageType == 2) {
                indicator.setViewPager(this, mListFragmentData.size());
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == CODE_WHAT_LOOP) {
                //:移除掉所有的回调和message的消息，如果传入null的话
                onStop();

                //:获取当前的页面
                int currentItem = getCurrentItem();
                if (mImageType == 1) {
                    //:自动循环到下一个页面
                    setCurrentItem(++currentItem);
                } else if (mImageType == 2) {
                    if (currentItem == (mListFragmentData.size() - 1)) {
                        currentItem = 0;
                    } else {
                        ++currentItem;
                    }
                    setCurrentItem(currentItem);
                }
                // 重新发送
                BannerView.this.sendMessage();
            }
        }
    };

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        sendMessage();
    }

    @Override
    public void onResume() {
        onStart();
    }

    @Override
    public void onPause() {
        onStop();
    }

    @Override
    public void onStop() {
        if (mHandler != null) {
            mHandler.removeMessages(CODE_WHAT_LOOP);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        onStop();
        if (mListFragmentData != null) {
            mListFragmentData.clear();
            mListFragmentData = null;
        }
    }

}
