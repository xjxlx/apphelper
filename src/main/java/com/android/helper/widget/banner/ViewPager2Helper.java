package com.android.helper.widget.banner;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;

public class ViewPager2Helper implements BaseLifecycleObserver {
    private ViewPager2 mViewPager2;
    private int mItemCount;
    private final int CODE_WHAT = 1000;
    private final int CODE_INTERVAL = 3 * 1000;
    private boolean isLoop;

    private final ViewPager2.OnPageChangeCallback mCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);

            if (mViewPager2 != null && mItemCount > 1) {
                int currentItem = mViewPager2.getCurrentItem();
                LogUtil.e("onPageScrollStateChanged: mCurrent  --->" + currentItem);

                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    onStop();
                    if (currentItem == 0) {
                        mViewPager2.setCurrentItem(mItemCount - 2, false);
                        LogUtil.e("onPageScrollStateChanged: mCurrent  ---> 跳转倒数第二个");
                    } else if (currentItem == mItemCount - 1) {
                        mViewPager2.setCurrentItem(1, false);
                        LogUtil.e("onPageScrollStateChanged: mCurrent  ---> 跳转第一个");
                    }
                }
            }
        }
    };

    /**
     * 开始轮播
     *
     * @param viewPager2 viewPager2的对象
     */
    public void startLoop(ViewPager2 viewPager2) {
        this.mViewPager2 = viewPager2;
        if (viewPager2 != null) {
            RecyclerView.Adapter adapter = viewPager2.getAdapter();
            if (adapter != null) {
                mItemCount = adapter.getItemCount();

                viewPager2.registerOnPageChangeCallback(mCallback);

                onStart();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mViewPager2 != null) {
                onStop();

                int currentItem = mViewPager2.getCurrentItem();
                LogUtil.e("handler:--->" + currentItem);
                if (msg.what == CODE_WHAT) {
                    if (currentItem == 0) {
                        mViewPager2.setCurrentItem(mItemCount - 2, false);
                        LogUtil.e("handler:---> 当前的角标为 0 ，跳转到 倒数第二个");
                    } else if (currentItem == mItemCount - 1) {
                        mViewPager2.setCurrentItem(1, false);
                        LogUtil.e("handler:---> 当前的角标为  倒数第一个，跳转到 第一个");
                    } else {
                        onStop();
                        LogUtil.e("handler:---> ++ " + currentItem);
                        mViewPager2.setCurrentItem(++currentItem);
                    }

                    mEndTime = System.currentTimeMillis();

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

    private long mEndTime;

    @Override
    public void onStart() {
        if (mHandler != null) {
            long startTime = System.currentTimeMillis();
            long mIntervalTime = startTime - mEndTime;
            if (mIntervalTime > CODE_INTERVAL) {
                mHandler.sendEmptyMessageDelayed(CODE_WHAT, CODE_INTERVAL);
            }
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
