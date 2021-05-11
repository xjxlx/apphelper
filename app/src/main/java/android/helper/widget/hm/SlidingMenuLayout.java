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
 */
public class SlidingMenuLayout extends FrameLayout {

    private final String tag = "------>:SlidingMenu";

    private LinearLayout mMenu;
    private RelativeLayout mContent;

    private int mMenuMeasuredWidth;
    private int mMenuMeasuredHeight;
    private int mContentMeasuredWidth;
    private int mContentMeasuredHeight;

    private ViewDragHelper mViewDragHelper;

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
            if (left > mLeftInterval) {
                left = (int) mLeftInterval;
            }
            // 5.1 --->限制Content布局左侧滑动的限制
            if (left <= 0) {
                left = 0;
            }
            return left;
        }

        /*
         * view滑动的改变
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            // 5.1：固定住底部的view，不让底部的view滑动
            if (changedView == mMenu) {
                changedView.layout(0, 0, changedView.getRight(), changedView.getBottom());
            }
        }
    };
    private float mLeftInterval;

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
        mMenuMeasuredWidth = mMenu.getMeasuredWidth();
        mMenuMeasuredHeight = mMenu.getMeasuredHeight();
        mContentMeasuredWidth = mContent.getMeasuredWidth();
        mContentMeasuredHeight = mContent.getMeasuredHeight();
        LogUtil.e("menu的宽：" + mMenuMeasuredWidth + " menu的高：" + mMenuMeasuredHeight);
        LogUtil.e("content的宽：" + mContentMeasuredWidth + " content的高：" + mContentMeasuredHeight);

        // 左侧最大的距离
        mLeftInterval = mContentMeasuredWidth * 0.6f;
    }

    /**
     * 此方法在xml布局映射完成后获取，在这里可以完美获取到view的对象
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtil.e(tag, "onFinishInflate");
        mMenu = findViewWithTag("menu");
        mContent = findViewWithTag("content");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}
