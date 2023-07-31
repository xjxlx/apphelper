package com.android.helper.widget.banner;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.common.utils.LogUtil;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;

/**
 * 这个ViewPager2的工具类，是为了做页面的无限轮播使用的，可以使用图片的轮播，也可以使用Fragment的轮播
 * <p>
 * 一：使用方法：
 * 1：创建Builder的对象，设置需要的参数，例如viewPager 和 indicator
 * 2：调用 {@link ViewPager2Util#show(Fragment)}方法去展示轮播图
 * </p>
 *
 * <p>
 * 二：缺点：
 * 1:这个方法只适合Fragment 去使用，如果是单独的图片，有很多更好的选择可以去使用
 * 2：如果设置无限循环的话，必须在集合第0个角标添加一个集合最后一条数据，在集合最后的位置，增加一个第0个的数据
 * 3：第0个和最后一条数据，一定不能是单利的，否则会报异常。
 * </P>
 */
public class ViewPager2Util implements BaseLifecycleObserver {
    private int mItemCount;
    private final int CODE_WHAT = 1000;
    private final int CODE_INTERVAL = 3 * 1000;
    private int mCurrent;

    private ViewPager2 mViewPager2;
    private ViewPager2Indicator mIndicator;
    private boolean autoLoop;

    private final ViewPager2.OnPageChangeCallback mCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            mCurrent = position;
            if (mIndicator != null) {
                if (mCurrent == mItemCount - 2 || mCurrent == 0) {
                    mIndicator.onPageSelected(mItemCount - 3);
                } else if (mCurrent == mItemCount - 1 || mCurrent == 1) {
                    mIndicator.onPageSelected(0);
                } else {
                    mIndicator.onPageSelected(position - 1);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);

            if (mViewPager2 != null && mItemCount > 1) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) { // 拖动的时候
                    onStop();
                    if (mCurrent == 0) {
                        mViewPager2.setCurrentItem(mItemCount - 2, false);
                    } else if (mCurrent == mItemCount - 1) {
                        mViewPager2.setCurrentItem(1, false);
                    }
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) { // 停下的时候
                    if (mCurrent == 0) {
                        mViewPager2.setCurrentItem(mItemCount - 2, false);
                    } else if (mCurrent == mItemCount - 1) {

                        mViewPager2.setCurrentItem(1, false);
                    }
                    onStart();
                }
            }
        }
    };

    private ViewPager2Util() {
    }

    public ViewPager2Util(Builder builder) {
        this.mViewPager2 = builder.viewPager2;
        this.mIndicator = builder.indicator;
        this.autoLoop = builder.autoLoop;
    }

    /**
     * 开始轮播
     */
    public ViewPager2Util show(FragmentActivity activity) {
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);
        }
        startLoop();
        return this;
    }

    public ViewPager2Util show(Fragment fragment) {
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
        }
        startLoop();
        return this;
    }

    private void startLoop() {
        if (mViewPager2 != null) {
            RecyclerView.Adapter<?> adapter = mViewPager2.getAdapter();
            if (adapter != null) {
                mItemCount = adapter.getItemCount();
                if (mItemCount > 1) {
                    // 设置缓存页数
                    mViewPager2.setOffscreenPageLimit(mItemCount);

                    // 设置监听
                    mViewPager2.registerOnPageChangeCallback(mCallback);

                    // 设置默认的页数
                    mViewPager2.setCurrentItem(1, false);

                    // 设置指示器
                    if (mIndicator != null) {
                        mIndicator.setViewPager(mViewPager2, this, mItemCount);
                    }

                    // 开始轮播
                    onStart();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mViewPager2 != null) {

                if (autoLoop) {
                    LogUtil.e("handler:--->" + mCurrent);
                    if (msg.what == CODE_WHAT) {
                        mViewPager2.setCurrentItem(++mCurrent);
                    }
                    onStart();
                }
            }
        }
    };

    /**
     * 控制可见和不可见
     */
    public void isVisibility(boolean isVisibility) {
        if (isVisibility) {
            autoLoop = true;
            onStart();
        } else {
            onStop();
        }
    }

    public static class Builder {
        private ViewPager2 viewPager2;
        private ViewPager2Indicator indicator;
        private boolean autoLoop = true;// 自动播放

        public Builder setViewPager2(ViewPager2 viewPager2) {
            this.viewPager2 = viewPager2;
            return this;
        }

        public Builder setIndicator(ViewPager2Indicator indicator) {
            this.indicator = indicator;
            return this;
        }

        /**
         * @param autoLoop true:自动播放，false:停止自动播放
         * @return 是否自动播放
         */
        public Builder autoLoop(boolean autoLoop) {
            this.autoLoop = autoLoop;
            return this;
        }

        public ViewPager2Util Build() {
            return new ViewPager2Util(this);
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        if (mHandler != null) {
            // 先停止，后发送，避免造成快速轮转
            onStop();
            if (autoLoop) {
                mHandler.sendEmptyMessageDelayed(CODE_WHAT, CODE_INTERVAL);
            }
        }
    }

    @Override
    public void onResume() {
        LogUtil.e("onResume");
        onStart();
    }

    @Override
    public void onPause() {
        LogUtil.e("onPause");
        onStop();
    }

    @Override
    public void onStop() {
        if (mHandler != null) {
            mHandler.removeMessages(CODE_WHAT);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler = null;
        }

        if (mViewPager2 != null) {
            mViewPager2.unregisterOnPageChangeCallback(mCallback);
        }
    }
}
