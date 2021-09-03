package com.android.helper.widget.banner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
 * 一：如果是fragment的话
 * 1：需要调用{@link BannerView#setFragmentData(List)} 去设置数据
 * 2：需要代用{@link BannerView#show(Activity)}方法去开启轮播
 * 二：如果是普通的view的话
 * 1：调用 {@link BannerView#setImageData(List)} 去设置数据
 * 2：调用{@link BannerView#setBannerLoadListener(BannerLoadListener)}去给imageView设置数据
 * 3：调用{@link BannerView#show(Activity)}方法去开启轮播
 * 三：其他的方法，都是公用的方法，可以随意使用。、
 * 四：注意
 * 1：如果父类是NestedScrollView包裹的话，一定要给NestedScrollView的布局上加入： android:fillViewport="true"
 * 让ScrollView去允许子view自控扩展高度
 */
public class BannerView<T> extends ViewPager implements BaseLifecycleObserver {

    private final int CODE_WHAT_LOOP = 1000;// 轮询的code值
    private int CODE_LOOP_INTERVAL = 3 * 1000;// 轮询的时间间隔，默认5s
    private boolean mAutoLoop = true;// 是否开启轮询，默认开启
    private List<Fragment> mListFragmentData;// fragment的集合
    private List<T> mListImageData;// 图片的集合
    private int mImageType;// 1：普通的ImageView,2:fragment类型的
    private BannerLoadListener<T> mLoadListener;// 加载本地图片页面的回调
    private BannerItemClickListener<T> mBannerItemClickListener;// 点击事件的处理
    private BannerIndicator mIndicator;
    private int mMaxWidth, mMaxHeight;
    private final Map<Integer, Integer> mMapHeight = new HashMap<>(); // 用来存储每个item的高度
    private int mCurrent;// 当前的position
    private boolean isLast = true; //滑动是否可用
    private boolean mIsParentIntercept = false;// 父类是否拦截的标记
    private float mStartX; // 开始滑动的x轴位置

    public BannerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (isInEditMode()) { // 预览模式
            // 假如还没有数据，就用指示的高度去预览
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            // 如果是wrap_content模式的话，就显示高度为0
            if (mode == MeasureSpec.AT_MOST) {
                mMaxHeight = 0;
            } else {
                mMaxHeight = getDefaultSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
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
                        mMaxHeight = heightForMap;
                    } else {
                        // 获取当前的view高度
                        View childAt = getChildAt(position);
                        if (childAt != null) {
                            // 测量view的大小
                            childAt.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED));
                            mMaxHeight = childAt.getMeasuredHeight();
                            if (mMaxHeight > 0) {
                                mMapHeight.put(position, mMaxHeight);
                            }
                        }
                    }
                }
            } else if (mImageType == 2) { // fragment模式
                if ((mListFragmentData != null) && (mListFragmentData.size() > 0)) {
                    // fragment类型的数据，不参与数据的保存操作，因为页面的复杂性，肯定会导致测量不精确，而且fragment数据比较少，多次测量也还可以承受
                    Fragment fragment = mListFragmentData.get(mCurrent);
                    if (fragment != null) {
                        View view = fragment.getView();
                        if (view != null) {
                            // 测量view的大小
                            view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED));
                            int height = view.getMeasuredHeight();
                            if (height > 0) {
                                mMaxHeight = height + getPaddingBottom() + getPaddingTop();
                            }
                        }
                    }
                }
            }
        }

        if (mMaxWidth <= 0) {
            mMaxWidth = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        }

        // LogUtil.e("------>width:" + mMaxWidth + "  height:" + mMaxHeight);
        // 重新设置高度的模式
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY);
        // 重新设置宽度的模式
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initView() {
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
                if (mIndicator != null) {
                    mIndicator.setViewPager(this, mListFragmentData.size());
                }
                // 设置当前默认的位置是在最中间的位置
                setCurrentItem(0);
            }
        } else if (mImageType == 1) {
            if ((mListImageData != null) && (mListImageData.size() > 0)) {
                BannerAdapter<T> bannerAdapter = new BannerAdapter<>(mListImageData);
                bannerAdapter.setParentView(this);

                if (mLoadListener != null) {
                    bannerAdapter.setBannerLoadListener(mLoadListener);
                }
                if (mBannerItemClickListener != null) {
                    bannerAdapter.setItemClickListener(mBannerItemClickListener);
                }
                setAdapter(bannerAdapter);

                // size 的长度
                int size = mListImageData.size();
                setOffscreenPageLimit(size);
                // 添加指示器
                if (mIndicator != null) {
                    mIndicator.setViewPager(this, size);
                }

                addIndicator(mIndicator);
                // 设置当前默认的位置是在最中间的位置
                if (size == 1) {
                    setCurrentItem(CommonConstants.BANNER_LENGTH / 2);
                } else {
                    setCurrentItem(CommonConstants.BANNER_LENGTH / size);
                }
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
            if (mImageType == 1) {
                if (mListImageData.size() != 1) {
                    mHandler.sendEmptyMessageDelayed(CODE_WHAT_LOOP, CODE_LOOP_INTERVAL);
                }
            } else if (mImageType == 2) {
                if (mListFragmentData.size() != 1) {
                    mHandler.sendEmptyMessageDelayed(CODE_WHAT_LOOP, CODE_LOOP_INTERVAL);
                }
            }
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
                LogUtil.e("----->current---onPageSelected:" + getCurrentItem());
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
                }
                // LogUtil.e("当前选中的position：" + mCurrent);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isLeft = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                onStop();
                break;

            case MotionEvent.ACTION_MOVE:
                onStop();
                float endX = ev.getX();

                float dx = endX - mStartX;

                if (dx > 0) {
                    LogUtil.e("向右滑动 dx :" + dx);
                } else {
                    LogUtil.e("向左滑动 dx: " + dx);
                    isLeft = true;
                }
                mStartX = endX;
                if (mImageType == 2) {
                    if (isLeft) {    // 向左滑动
                        if (mCurrent == (mListFragmentData.size() - 1)) {
                            if (mIsParentIntercept) {
                                // 请求父类不要拦截当前的事件
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    } else {  // 向右滑动
                        if (mCurrent == 0) {
                            if (mIsParentIntercept) {
                                // 请求父类不要拦截当前的事件
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //:在抬起的时候继续发送消息
                sendMessage();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * @param interval 轮询的时间间隔，默认是5s
     */
    public BannerView<T> setInterval(int interval) {
        CODE_LOOP_INTERVAL = interval;
        return this;
    }

    /**
     * 是否开启轮询
     *
     * @param autoLoop true:自动轮询，false:不轮询
     */
    public BannerView<T> autoLoop(boolean autoLoop) {
        this.mAutoLoop = autoLoop;
        return this;
    }

    public BannerView<T> setImageData(List<T> listImageData) {
        mListImageData = listImageData;
        mImageType = 1;
        return this;
    }

    public BannerView<T> setFragmentData(List<Fragment> fragmentList) {
        this.mListFragmentData = fragmentList;
        mImageType = 2;
        return this;
    }

    /**
     * 此方法，只适用于加载单独的图片去使用，因为有些图片可能要进行其他处理，例如圆角什么的，所以让使用者自己去加载处理。
     */
    public BannerView<T> setBannerLoadListener(BannerLoadListener<T> loadListener) {
        this.mLoadListener = loadListener;
        return this;
    }

    /**
     * 图片类型点击事件的处理
     */
    public BannerView<T> setItemClickListener(BannerItemClickListener<T> onItemClickListener) {
        this.mBannerItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * @return 设置指示器
     */
    public BannerView<T> addIndicator(BannerIndicator bannerIndicator) {
        this.mIndicator = bannerIndicator;
        return this;
    }

    /**
     * 设置父类不拦截当前的view动作
     *
     * @param parentIntercept true:不拦截，false:拦截，默认是拦截
     */
    public BannerView<T> setParentNoIntercept(boolean parentIntercept) {
        mIsParentIntercept = parentIntercept;
        return this;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);
            LogUtil.e("----->current---handleMessage:" + getCurrentItem());

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
