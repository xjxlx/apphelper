package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

import com.android.helper.utils.LogUtil;

/**
 * 目标：打造一个能左右滑动的布局
 * 思路：
 * 1：因为是一层压着一层的左右滑动，所以布局继承frameLayout最合适，不用去再继承viewGroup
 * 2：因为要滑动，首先拿到上下两层布局的对象，在onFinishInflate和onSizeChanged方法里面都可以，这在onFinishInflate方法中去获取
 * 3：onSizeChange方法是在最后一次测量完onMeasure方法之后走入，在这里去获取view的宽高最合适
 * 4：创建滑动对象的辅助类对象，并设置返回接口的对象
 * 5：限制底部的view 不滑动，限制顶部的view滑动的距离
 * 6：兼容消耗滑动事件的冲突，
 * 7：手指松开的时候，如果滑动到了一半的距离，就滑动到最后，反之就滑动到最左侧
 * 8：滑动menuView的时候，让右侧的contLayout布局也跟着滑动
 */
public class SlidingMenuLayout extends FrameLayout {

    private final String tag = "------>:SlidingMenu";

    private LinearLayout mMenuLayout;
    private RelativeLayout mContentLayout;

    private int mMenuMeasuredWidth;
    private int mMenuMeasuredHeight;
    private int mContentMeasuredWidth;
    private int mContentMeasuredHeight;

    private ViewDragHelper mViewDragHelper;
    private float mLeftInterval;
    private float mCanScrOllHalfPosition;
    private float mStartX;
    private float mDX;

    private final ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        /*
         * 这是一个抽象类，必须去实现，也只有在这个方法返回true的时候下面的方法才会生效
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }

        /*
         * 水平拖拽的时候回调的方法
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            // 5.1 --->限制Content右侧滑动的距离
            // 5.1 --->限制Content布局左侧滑动的限制
            return getLeftInterval(left);
        }

        /*
         * view滑动的改变
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            // 5.1：固定住底部的view，不让底部的view滑动
            if (changedView == mMenuLayout) {
                changedView.layout(0, 0, changedView.getRight(), changedView.getBottom());

                LogUtil.e("dx:" + dx + "   dy:" + dy);

                // 8:让右侧的view也跟着滑动
                int l = (int) (mContentLayout.getLeft() + mDX);
                l = getLeftInterval(l);

                // todo  此处有bug
                int t = mContentLayout.getTop() + dy;
                int r = mContentLayout.getMeasuredWidth() + l;

                int b = mContentLayout.getBottom() + dy;

                LogUtil.e("l:" + l + "   t:" + t + "  r:" + r + "  b:" + b);

                mContentLayout.layout(l, t, r, b);
            }
        }

        /*
         *只要view返回的数据大于0，就会滑动
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return child.getMeasuredWidth();//只要返回大于0的值就行
        }

        /**
         *
         * 7：手指松开时候的处理
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            LogUtil.e("onViewReleased --->");

            // 获取内容布局的左侧间距
            int left = mContentLayout.getLeft();

            LogUtil.e("left:" + left + "  mCanScrOllHalfPosition:" + mCanScrOllHalfPosition);
            if (left >= mCanScrOllHalfPosition) {
                // 直接滑动到最后面
                LogUtil.e("直接滑动到最后面");
                mViewDragHelper.settleCapturedViewAt((int) mLeftInterval, mContentLayout.getTop());
                invalidate();
            } else {
                // 直接滑动到最左侧
                mViewDragHelper.settleCapturedViewAt(0, mContentLayout.getTop());
                invalidate();
                LogUtil.e("直接滑动到最左侧");
            }
        }
    };

    public SlidingMenuLayout(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public SlidingMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LogUtil.e(tag, "SlidingMenuLayout");
        // 创建滑动的对象
        mViewDragHelper = ViewDragHelper.create(SlidingMenuLayout.this, mCallback);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtil.e(tag, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtil.e(tag, "onLayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.e(tag, "onDraw");
    }

    /**
     * 此方法在onMeasure方法之后走入，在这里能获取到完整的view宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtil.e(tag, "onSizeChanged");
        mMenuMeasuredWidth = mMenuLayout.getMeasuredWidth();
        mMenuMeasuredHeight = mMenuLayout.getMeasuredHeight();
        mContentMeasuredWidth = mContentLayout.getMeasuredWidth();
        mContentMeasuredHeight = mContentLayout.getMeasuredHeight();
        LogUtil.e("menu的宽：" + mMenuMeasuredWidth + " menu的高：" + mMenuMeasuredHeight);
        LogUtil.e("content的宽：" + mContentMeasuredWidth + " content的高：" + mContentMeasuredHeight);

        // 左侧最大的距离
        mLeftInterval = mContentMeasuredWidth * 0.6f;
        // 可滑动的间距的一半的位置
        mCanScrOllHalfPosition = mLeftInterval / 2;

        LogUtil.e("mLeftInterval:" + mLeftInterval + "  mCanScrOllHalfPosition:" + mCanScrOllHalfPosition);
    }

    /**
     * 此方法在xml布局映射完成后获取，在这里可以完美获取到view的对象
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtil.e(tag, "onFinishInflate");
        mMenuLayout = findViewWithTag("menu");
        mContentLayout = findViewWithTag("content");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float eventX = event.getX();
                // 获取偏移量
                mDX = eventX - mStartX;
                // 把后获取的dx值赋值给前面的dx值
                mStartX = eventX;
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        // 如果动画正在进行中，就进行view的绘制
        if ((mViewDragHelper != null) && (mViewDragHelper.continueSettling(true))) {
            invalidate();
        }
    }

    private int getLeftInterval(int left) {
        // 限制右侧的边距
        if (left > mLeftInterval) {
            left = (int) mLeftInterval;
        }
        // 5.1 --->限制Content布局左侧滑动的限制
        if (left <= 0) {
            left = 0;
        }

        return left;
    }
}
