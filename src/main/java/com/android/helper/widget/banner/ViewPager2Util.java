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

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;

/**
 * 这个ViewPager2的工具类，是为了做页面的无限轮播使用的，可以使用图片的轮播，也可以使用Fragment的轮播
 * 一：使用方法：
 * 1:开启轮播，调用方法{@link ViewPager2Util#startLoop(Fragment, ViewPager2)}
 * 2：添加指示器，调用方法{@link ViewPager2Util#addIndicator(ViewPager2Indicator)}
 * <p>
 * 二：缺点：
 * 1:这个方法只适合
 */
public class ViewPager2Util implements BaseLifecycleObserver {
    private ViewPager2 mViewPager2;
    private int mItemCount;
    private final int CODE_WHAT = 1000;
    private final int CODE_INTERVAL = 3 * 1000;
    private int mCurrent;
    private ViewPager2Indicator mIndicator;

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
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {  // 拖动的时候
                    onStop();
                    if (mCurrent == 0) {
                        mViewPager2.setCurrentItem(mItemCount - 2, false);
                    } else if (mCurrent == mItemCount - 1) {
                        mViewPager2.setCurrentItem(1, false);
                    }
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {  // 停下的时候
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

    /**
     * 开始轮播
     *
     * @param viewPager2 viewPager2的对象
     */
    public void startLoop(FragmentActivity activity, ViewPager2 viewPager2) {
        this.mViewPager2 = viewPager2;
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);
        }
        loop();
    }

    public void startLoop(Fragment fragment, ViewPager2 viewPager2) {
        this.mViewPager2 = viewPager2;
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
        }
        loop();
    }

    private void loop() {
        if (mViewPager2 != null) {
            RecyclerView.Adapter adapter = mViewPager2.getAdapter();
            if (adapter != null) {
                mItemCount = adapter.getItemCount();
                if (mItemCount > 1) {
                    // 设置缓存页数
                    mViewPager2.setOffscreenPageLimit(mItemCount);

                    // 设置监听
                    mViewPager2.registerOnPageChangeCallback(mCallback);

                    // 设置默认的页数
                    mViewPager2.setCurrentItem(1, false);

                    // 开始轮播
                    onStart();
                }
            }
        }
    }

    public void addIndicator(ViewPager2Indicator bannerIndicator) {
        this.mIndicator = bannerIndicator;
        if (mIndicator != null) {
            mIndicator.setViewPager(mViewPager2, this, mItemCount);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mViewPager2 != null) {
                onStop();

                LogUtil.e("handler:--->" + mCurrent);
                if (msg.what == CODE_WHAT) {
                    mViewPager2.setCurrentItem(++mCurrent);
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
            onStart();
        } else {
            onStop();
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(CODE_WHAT, CODE_INTERVAL);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

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
