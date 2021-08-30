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
 * 1：调用 {@link BannerView# } 创建一个Builder,用来设置各种数据
 * 2：调用{@link BannerView#start(Activity)}方法去开启轮播
 */
public class BannerView extends ViewPager implements BaseLifecycleObserver {
    private final int CODE_WHAT_LOOP = 1000;// 轮询的code值
    private int CODE_LOOP_INTERVAL = 3 * 1000;// 轮询的时间间隔，默认5s
    private boolean mAutoLoop = true;// 是否开启轮询，默认开启
    private List<Fragment> mListFragmentData;// fragment的集合
    private List<Object> mListImageData;// 图片的集合
    private int mImageType;// 1：普通的ImageView,2:fragment类型的
    private BannerLoadListener mLoadListener;// 加载本地图片页面的回调
    private BannerItemClickListener mBannerItemClickListener;// 点击事件的处理
    private BannerIndicator mIndicator;
    private int mMaxWidth, mMaxHeight;
    private final Map<Integer, Integer> mMapHeight = new HashMap<>(); // 用来存储每个item的高度
    private int mCurrent;// 当前的position
    private BannerView mBannerView;
    private boolean isLast = true; //滑动是否可用

    public BannerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
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
                                // int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);//为子View准备测量的参数
                                // int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
                                view.measure(widthMeasureSpec, heightMeasureSpec);
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
        setMeasuredDimension(mMaxWidth, mMaxHeight);
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
    public void show(Fragment fragment, FragmentManager manager) {
        // 感知fragment的生命周期
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
        }
        if (manager != null) {
            setAdapter(manager);
        }
    }

    /**
     * 开始播放轮播图
     *
     * @param activity activity 类型的上下文
     */
    public void show(Activity activity) {
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
                if (mBannerItemClickListener != null) {
                    bannerAdapter.setItemClickListener(mBannerItemClickListener);
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
                // 处理点击事件
                if (mImageType == 2) {
                    Fragment fragment = mListFragmentData.get(position);
                    View view = fragment.getView();
                    if (view != null) {
                        view.setOnClickListener(v -> {
                            if (mBannerItemClickListener != null) {
                                mBannerItemClickListener.onItemClick(fragment, view, position, null);
                            }
                        });
                    }
                }

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
                if (mImageType == 2) {
                    if (state == ViewPager.SCROLL_STATE_SETTLING) { // 正在定位的时候
                        isLast = false;
                    } else if (state == ViewPager.SCROLL_STATE_IDLE && isLast) { // viewPager 停止滑动了
                        onStop();
                        //此处为你需要的情况，再加入当前页码判断可知道是第一页还是最后一页
                        if (mListFragmentData.size() != 0 && mCurrent == (mListFragmentData.size() - 1)) { // 最后一页
                            setCurrentItem(0);
                            LogUtil.e("滑动到第一页");
                        } else if (mListFragmentData.size() != 0 && mCurrent == 0) {// 第一页
                            setCurrentItem(mListFragmentData.size() - 1);
                            LogUtil.e("滑动到最后一页");
                        }
                    } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {// 用户拖动的时候
                        isLast = true;
                    }
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 向左滑动
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //:在按下的时候停止发送Handler消息
                onStop();
                return true;

            case MotionEvent.ACTION_MOVE:
                onStop();
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
//    public static class Builder {
//        private int mInterval = 3 * 1000;// 轮询的时间间隔，默认5s
//        private boolean mAutoLoop = true;// 是否开启轮询，默认开启
//        private List<Fragment> mListFragmentData;// fragment的集合
//        private List<Object> mListImageData;// 图片的集合
//        private int mImageType;// 1：普通的ImageView,2:fragment类型的
//        private BannerLoadListener loadListener;// 加载本地图片页面的回调
//        private BannerIndicator mIndicator; // 加载指示器
//        private BannerItemClickListener mBannerItemClickListener;// 图片点击事件的处理
//
//    }

    /**
     * @param interval 轮询的时间间隔，默认是5s
     */
    public BannerView setInterval(int interval) {
        CODE_LOOP_INTERVAL = interval;
        return this;
    }

    /**
     * 是否开启轮询
     *
     * @param autoLoop true:自动轮询，false:不轮询
     */
    public BannerView autoLoop(boolean autoLoop) {
        this.mAutoLoop = autoLoop;
        return this;
    }

    public BannerView setImageData(List<Object> listImageData) {
        mListImageData = listImageData;
        mImageType = 1;
        return this;
    }

    public BannerView setFragmentData(List<Fragment> fragmentList) {
        this.mListFragmentData = fragmentList;
        mImageType = 2;
        return this;
    }

    /**
     * 此方法，只适用于加载单独的图片去使用，因为有些图片可能要进行其他处理，例如圆角什么的，所以让使用者自己去加载处理。
     */
    public BannerView setBannerLoadListener(BannerLoadListener loadListener) {
        this.mLoadListener = loadListener;
        return this;
    }

    /**
     * 图片类型点击事件的处理
     */
    public BannerView setItemClickListener(BannerItemClickListener onItemClickListener) {
        this.mBannerItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * @return 设置指示器
     */
    public BannerView addIndicator(BannerIndicator bannerIndicator) {
        this.mIndicator = bannerIndicator;
        if (bannerIndicator != null) {
            if (mImageType == 1) {
                bannerIndicator.setViewPager(this, mListImageData.size());
            } else if (mImageType == 2) {
                bannerIndicator.setViewPager(this, mListFragmentData.size());
            }
        }
        return this;
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
