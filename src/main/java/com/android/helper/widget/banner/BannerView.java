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
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义轮播图，可以实现自动滚动
 * 使用方式：
 * 二：如果是普通的view的话
 * 1：调用 {@link BannerView#setImageData(List)} 去设置数据
 * 2：调用{@link BannerView#setBannerLoadListener(BannerLoadListener)}去给imageView设置数据
 * 3：调用{@link BannerView#show(Activity)}方法去开启轮播
 * 三：其他的方法，都是公用的方法，可以随意使用。、
 * 四：注意
 * 1：如果父类是NestedScrollView包裹的话，一定要给NestedScrollView的布局上加入： android:fillViewport="true"
 * 让ScrollView去允许子view自控扩展高度
 */
public class BannerView extends ViewPager implements BaseLifecycleObserver {
    private String TAG = "";

    private final int CODE_WHAT_LOOP = 1000;// 轮询的code值
    private int CODE_LOOP_INTERVAL = 3 * 1000;// 轮询的时间间隔，默认5s
    private boolean mAutoLoop = true;// 是否开启轮询，默认开启
    private List mListImageData;// 图片的集合
    private BannerLoadListener<?> mLoadListener;// 加载本地图片页面的回调
    private BannerItemClickListener<?> mBannerItemClickListener;// 点击事件的处理
    private BannerIndicator mIndicator;
    private int mMaxWidth, mMaxHeight;
    private int mCurrent;// 当前的position
    private boolean isLast = true; //滑动是否可用
    private boolean mIsParentIntercept = false;// 父类是否拦截的标记
    private int mStartX, mStartY; // 开始滑动的x轴位置
    private BannerAdapter mBannerAdapter;
    private boolean isVisibility; // view是否可见，只有布局显示完全了，才会去设置为可见
    private boolean isSetAdapter; // 是否已经设置了adapter

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
            if ((mListImageData != null) && (mListImageData.size() > 0)) {
                int currentItem = getCurrentItem();
                // 求出当前item是第几列
                int position = currentItem % mListImageData.size();
                // 获取当前的view高度
                View childAt = getChildAt(position);
                if (childAt != null) {
                    // 测量view的大小
                    if (childAt instanceof ViewGroup) {
                        ViewGroup group = (ViewGroup) childAt;
                        View child = group.getChildAt(0);
                        if (child != null) {
                            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED));
                            mMaxHeight = child.getMeasuredHeight();
                        }
                    }
                }
            }
        }

        if (mMaxWidth <= 0) {
            mMaxWidth = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        }

        if (mMaxHeight <= 0) {
            mMaxHeight = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
        }

        // 重新设置高度的模式
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.EXACTLY);
        // 重新设置宽度的模式
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);

        LogUtil.e(TAG, "mMaxWidth:" + mMaxWidth + "  mMaxHeight:" + mMaxHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initView() {
        TAG = getId() + "";
        // 设置按下的手势操作
        addPageListener();
    }

    /**
     * 开始播放轮播图
     *
     * @param fragment fragment类型的上下文
     */
    public void show(Fragment fragment) {
        // 感知fragment的生命周期
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);
        }
        seBannerAdapter();
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

            seBannerAdapter();
        }
    }

    /**
     * 设置adapter，内部使用
     */
    private void seBannerAdapter() {
        if (isVisibility) {
            LogUtil.e(TAG, "---: setAdapter");
            if ((mListImageData != null) && (mListImageData.size() > 0)) {
                mBannerAdapter = new BannerAdapter(mListImageData);
                mBannerAdapter.setParentView(this);

                if (mLoadListener != null) {
                    mBannerAdapter.setBannerLoadListener(mLoadListener);
                }
                if (mBannerItemClickListener != null) {
                    mBannerAdapter.setItemClickListener(mBannerItemClickListener);
                }

                // 预加载的数量
                setOffscreenPageLimit(mListImageData.size());
                // 设置adapter
                setAdapter(mBannerAdapter);

                // 添加指示器
                if (mIndicator != null) {
                    mIndicator.setViewPager(BannerView.this, mListImageData.size());
                }

                isSetAdapter = true;

                // 发送轮询
                mHandler.sendEmptyMessage(CODE_WHAT_LOOP);
            }
        }
    }

    /**
     * 开始发送消息
     */
    private void sendMessage() {
        // 发送轮询
        if ((mHandler != null) && mAutoLoop && (mListImageData != null)) {
            if (mListImageData.size() <= 1) {
                return;
            }
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
                //  LogUtil.e(TAG, "----->current---onPageSelected: current: " + getCurrentItem() + "  position:" + position);
                // 处理点击事件
                mCurrent = position;

                // 只有数据大于1的时候，才会去执行indicator的选中
                if (mListImageData.size() > 1) {

                    // 0 -> size -2  (0/3)--->:3
                    if ((mCurrent == 0) || (mCurrent == mListImageData.size() - 2)) {
                        // 因为indicator 是从0开始的，所以要减掉1
                        selectorIndicator(mListImageData.size() - 3);
                    } else if ((mCurrent == 1) || (mCurrent == mListImageData.size() - 1)) {
                        // 1 -> size -1 (1/4) --->:1
                        // 因为indicator 是从0开始的，所以要减掉1
                        selectorIndicator(0);
                    } else {
                        // 因为indicator 是从0开始的，所以要减掉1
                        selectorIndicator(mCurrent - 1);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //  LogUtil.e(TAG, "----->current---onPageScrollStateChanged: current: " + getCurrentItem() + "  mCurrent:" + mCurrent);

                if (mListImageData != null && mListImageData.size() > 1) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        if (mCurrent == 0) { //
                            onStop();
                            /*
                             * 1：如果是第0个数据，那么就转移到集合中倒数第二个数据，并停止动画，第0条数据和倒数第二个数据相同
                             * 2：如果到了这里，就需要先停止之前的轮询数据
                             * 3：在设置了转移数据之后，再次去重新开启轮询
                             */
                            setCurrentItem(mListImageData.size() - 2, false);

                            // 再次去轮询
                            onStart();

                        } else if (mCurrent == mListImageData.size() - 1) {
                            onStop();

                            /*
                             * 1：如果是倒数第一个数据，那么就转移到第一条数据，第一条数据和倒数第一条数据相同，转移到第一个数据
                             * 2：如果到了这里，就需要先停止之前的轮询数据
                             * 3：在设置了转移数据之后，再次去重新开启轮询
                             */
                            setCurrentItem(1, false);

                            // 再次去轮询
                            onStart();
                        }
                    }
                }
            }
        });
    }

    /**
     * indicator的当前选中
     *
     * @param position 当前选中的角标
     */
    private void selectorIndicator(int position) {
        if (mIndicator != null) {
            LogUtil.e(TAG, "indicator当前的选中为：" + position);
            mIndicator.onPageSelected(position);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //:进行判断：如果是第一页或者是最后一页的话，就不要拦截，其他的都拦截
        //:如果是上下滑动的话，就需要拦截
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStop();
                mStartX = (int) ev.getX();
                mStartY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                onStop();
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                //:得到左右上下的偏移量
                int dx = x - mStartX;
                int dy = y - mStartY;

                if (dx > 0) {
                    // LogUtil.e("向右滑动 dx :" + dx);
                } else {
                    //  LogUtil.e("向左滑动 dx: " + dx);
                }

                //:判断
                if (Math.abs(dx) > Math.abs(dy)) {
                    //:如果左右的偏量大于上下的偏移量的话，那木就能确定是左右滑动
                    //:获得当前的页面
                    int currentItem = getCurrentItem();
                    //:如果是第一个，或者最后一的话，不需要拦截
                    if (mListImageData != null) {
                        if ((currentItem == 0) || (currentItem == mListImageData.size() - 1)) {
                            LogUtil.e(TAG, "current:" + currentItem + "  请求父类不要拦截我");
                            getParent().requestDisallowInterceptTouchEvent(true);//:请求父类以及祖宗类要去拦截
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                sendMessage();
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

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

    public <T> BannerView setImageData(List<T> listImageData) {
        if (listImageData != null) {
            if (listImageData.size() > 1) {
                mListImageData = new ArrayList<>();
                mListImageData.add(listImageData.get(listImageData.size() - 1));
                mListImageData.addAll(listImageData);
                mListImageData.add(listImageData.get(0));
            } else {
                mListImageData = listImageData;
            }
        }
        return this;
    }

    /**
     * 此方法，只适用于加载单独的图片去使用，因为有些图片可能要进行其他处理，例如圆角什么的，所以让使用者自己去加载处理。
     */
    public <T> BannerView setBannerLoadListener(BannerLoadListener<T> loadListener) {
        this.mLoadListener = loadListener;
        return this;
    }

    /**
     * 图片类型点击事件的处理
     */
    public <T> BannerView setItemClickListener(BannerItemClickListener<T> onItemClickListener) {
        this.mBannerItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * @return 设置指示器
     */
    public BannerView addIndicator(BannerIndicator bannerIndicator) {
        this.mIndicator = bannerIndicator;
        return this;
    }

    /**
     * 设置父类不拦截当前的view动作
     *
     * @param parentIntercept true:不拦截，false:拦截，默认是拦截
     */
    public BannerView setParentNoIntercept(boolean parentIntercept) {
        mIsParentIntercept = parentIntercept;
        return this;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);
            LogUtil.e(TAG, "----->Banner---mHandler:" + getCurrentItem() + "    mCurrent：" + mCurrent);

            //:移除掉所有的回调和message的消息，如果传入null的话
            onStop();

            if (mListImageData != null) {
                if (msg.what == CODE_WHAT_LOOP) {
                    if (mListImageData.size() <= 1) {  // 如果数据小于1，则停止
                        setCurrentItem(0);

                    } else { // 数据大于1
                        // 自动轮询下一个数据
                        setCurrentItem(++mCurrent);
                        // 自动轮播下一个
                        onStart();
                    }
                }
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
        }
    }

    @Override
    public void onDestroy() {

        onStop();
        if (mListImageData != null) {
            mListImageData.clear();
            mListImageData = null;
        }
        if (mIndicator != null) {
            mIndicator = null;
        }
        if (mBannerAdapter != null) {
            mBannerAdapter = null;
        }
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            onStart();
        } else {
            onStop();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtil.e(TAG, "onSizeChanged: w:" + w + " h:" + h + "  oldw:" + oldw + "  olh:" + oldh);

        if (getVisibility() == View.VISIBLE) {
            isVisibility = true;
        }
        if (!isSetAdapter) {
            seBannerAdapter();
        }
    }
}
