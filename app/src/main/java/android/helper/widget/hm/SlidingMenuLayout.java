package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
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
 * 侧边滑动的布局，集成FrameLayout 是因为它符合布局叠加的特性，所以不用去集成ViewGroup
 */
public class SlidingMenuLayout extends FrameLayout {

    private LinearLayout mMenu;
    private RelativeLayout mContent;
    private ViewDragHelper mViewDragHelper;
    private int mMaxRight; // 右侧最多滑动的距离

    private final ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == mMenu || child == mContent;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            // 限制左滑和右划
            if (left < 0) {
                left = 0;
            }

            // 限制右侧的滑动
            if (left > mMaxRight) {
                left = mMaxRight;
            }
            return left;
        }

        // 拦截子view的触摸事件，方法的返回值应当是该childView横向或者纵向的移动的范围
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return mMaxRight;
        }

        // view位置改变时候的监听
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (changedView == mMenu) {

                // 触摸到菜单按钮的时候，让菜单按钮固定位置，不去滑动
                changedView.layout(0, 0, changedView.getMeasuredWidth(), changedView.getMeasuredHeight());
            }
        }
    };

    public SlidingMenuLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public SlidingMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mViewDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtil.e("------> onSizeChanged");
        int measuredWidth = getMeasuredWidth();
        mMaxRight = (int) (measuredWidth * 0.6);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenu = findViewWithTag("menu");
        mContent = findViewWithTag("content");
        LogUtil.e("menu:" + mMenu);
        LogUtil.e("content:" + mContent);
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
