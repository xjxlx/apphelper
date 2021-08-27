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

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 自定义轮播图，可以实现自动滚动
 * 使用方式：
 * 一：如果是xml创建的view，那么需要走两步
 * 1：调用 {@link BannerView#createBuild(Builder)} 创建一个Builder,用来设置各种数据
 * 2：调用{@link BannerView#start(Activity)}方法去开启轮播
 */
public class BannerView extends ViewPager implements BaseLifecycleObserver {
    private final int CODE_WHAT_LOOP = 1000;// 轮询的code值
    private int maxHeight; // view的高度

    private int CODE_LOOP_INTERVAL;// 轮询的时间间隔，默认5s
    private boolean mAutoLoop = true;// 是否开启轮询，默认开启
    private List<Fragment> mListFragmentData;// fragment的集合
    private List<Object> mListImageData;// 图片的集合
    private int mImageType;// 1：普通的ImageView,2:fragment类型的
    private BannerLoadListener mLoadListener;// 加载本地图片页面的回调
    private BannerIndicator mIndicator;

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

        int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) child;
                int childCount = group.getChildCount();
                if (childCount > 0) {
                    for (int j = 0; j < childCount; j++) {
                        View childAt = group.getChildAt(j);
                        if (childAt != null) {
                            int measuredHeight = childAt.getMeasuredHeight();
                            if (measuredHeight > maxHeight) {
                                maxHeight = measuredHeight;
                            }
                        }
                    }
                }
            }
        }

        int width = getDefaultSize(0, widthMeasureSpec);
        // 重新设置view的高度，避免预览的时候看不到视图

        setMeasuredDimension(width, maxHeight + getPaddingBottom() + getPaddingTop());
    }

    private void initView() {
        // 设置按下的手势操作
        setTouch();
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
            if (mListFragmentData != null && mListFragmentData.size() > 0) {
                BannerFragmentAdapter fragmentAdapter = new BannerFragmentAdapter(manager, mListFragmentData);
                setAdapter(fragmentAdapter);
            }
        } else if (mImageType == 1) {
            BannerAdapter bannerAdapter = new BannerAdapter(mListImageData);
            if (mLoadListener != null) {
                bannerAdapter.setBannerLoadListener(mLoadListener);
            }
            setAdapter(bannerAdapter);
        }

        // 设置当前默认的位置
        setCurrentItem(Integer.MAX_VALUE / 2);

        // 开始轮询播放
        sendMessage();

        // 添加指示器
        addIndicator(mIndicator);
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
    private void setTouch() {
        // 设置viewPager按下的时候，页面停止滑动
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //:在按下的时候停止发送Handler消息
                    mHandler.removeMessages(CODE_WHAT_LOOP);
                    mHandler.removeCallbacksAndMessages(null);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    //:在抬起的时候继续发送消息
                    sendMessage();
                    break;
            }
            return false;// 此处不能消费掉事件, 否则viewpager自带滑动效果无法响应
        });
    }

    /**
     * builder的设计模式
     */
    public static class Builder {
        private int mInterval = 5 * 1000;// 轮询的时间间隔，默认5s
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
                //:自动循环到下一个页面
                setCurrentItem(++currentItem);
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
        sendMessage();
    }

    @Override
    public void onPause() {

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
