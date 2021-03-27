package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.helper.R;
import android.helper.base.BaseViewGroup;
import android.helper.utils.LogUtil;
import android.helper.utils.ToastUtil;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

public class ScrollHelperLayout extends BaseViewGroup {

    private View readView;
    private View blueView;
    private ViewDragHelper mViewDragHelper;
    private int mChildCount;
    private View mCurrentView;// 当前的view

    private final ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        // 哪一个view可以被移动
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            //不返回true就不会被移动
            //如果这里有多个View的话，返回值改变成 return child == mDragView1;
            //那么只有MDragView1可以被拖拽，其他View不能
            return true;
        }

        // 当前view被捕获时候的回调
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            // 当前的view
            mCurrentView = capturedChild;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {

            // 限制左侧的边距
            if (left < 0) {
                left = 0;
            }
            // 限制右侧的边距
            int measuredWidth = ScrollHelperLayout.this.getMeasuredWidth();
//            for (int i = 0; i < mChildCount; i++) {
//                View childAt = getChildAt(i);
//                if (childAt == child) {
//                    // 每个view的宽度
//                    int childViewMeasuredWidth = childAt.getMeasuredWidth();
//
//                    if (left >= (measuredWidth - childViewMeasuredWidth)) {
//                        left = measuredWidth - childViewMeasuredWidth;
//                    }
//                }
//            }

            if (mCurrentView != null) {
                int childMeasuredWidth = mCurrentView.getMeasuredWidth();
                if (left >= (measuredWidth - childMeasuredWidth)) {
                    left = measuredWidth - childMeasuredWidth;
                }
            }
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return top;
        }

        // view被释放，也就是说手指离开时候的回调
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

        }

        // view滑动到边缘的时候，回调的方法
        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            LogUtil.e("edgeFlags:" + edgeFlags);
            switch (edgeFlags) {
                case ViewDragHelper.EDGE_TOP:
                    ToastUtil.show("滑动到顶部了");
                    break;

                case ViewDragHelper.EDGE_LEFT:
                    ToastUtil.show("滑动到左侧");
                    break;

            }
        }
    };

    public ScrollHelperLayout(Context context) {
        super(context);
    }

    public ScrollHelperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        readView = findViewById(R.id.v_read_view);
        blueView = findViewById(R.id.v_blue_view);

        // view的个数
        mChildCount = getChildCount();
        // 1：创建ViewDragHelper的对象
        mViewDragHelper = ViewDragHelper.create(ScrollHelperLayout.this, mCallback);

        // 设置view滑动到边缘时候的回调
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int redMeasureSpecWidth = MeasureSpec.makeMeasureSpec(readView.getLayoutParams().width, MeasureSpec.EXACTLY);
        int redMeasureSpecHeight = MeasureSpec.makeMeasureSpec(readView.getLayoutParams().height, MeasureSpec.EXACTLY);

        int blueViewMeasureSpecWidth = MeasureSpec.makeMeasureSpec(blueView.getLayoutParams().width, MeasureSpec.EXACTLY);
        int blueViewMeasureSpecHeight = MeasureSpec.makeMeasureSpec(blueView.getLayoutParams().height, MeasureSpec.EXACTLY);

        // 重新测量view的宽高
        readView.measure(redMeasureSpecWidth, redMeasureSpecHeight);
        blueView.measure(blueViewMeasureSpecWidth, blueViewMeasureSpecHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int redHeight = readView.getMeasuredHeight();
        readView.layout(0, 0, readView.getMeasuredWidth(), redHeight);
        blueView.layout(0, redHeight, blueView.getMeasuredWidth(), blueView.getMeasuredHeight() + redHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 2.1 把viewDragHelper 交给interceptTouchEvent 去处理拦截的机制
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 2.2 把viewDragHelper 交给touchEvent去使用，让viewDragHelper去实际的处理事件，但是这里必须返回为true，不然不会去执行
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

}
