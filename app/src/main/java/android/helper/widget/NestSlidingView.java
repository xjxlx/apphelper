package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.helper.R;
import android.helper.utils.LogUtil;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 嵌套滑动的view，目的是打造成一个能伸缩滑动的view，
 * 里面包含三个view，顶部的view，中间的view和底部的view
 * 其中顶部的view，固定不动，中间的view能被底部的view所遮盖，底部的view能滑动到顶部view的位置
 * 中间的view能在底部view滑动 的时候跟着小范围的滑动
 */
public class NestSlidingView extends ViewGroup {

    private VelocityTracker velocityTracker;
    private Scroller mScroller;
    private View mButton;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ViewTag.Top, ViewTag.Middle, ViewTag.Bottom})
    public @interface ViewTag {
        String Top = "top_view";
        String Middle = "middle_view";
        String Bottom = "bottom_view";
    }

    private View mTopView;
    private View mMiddleView;
    private View mBottomView;
    private int mDownX;
    private int mDownY;

    public NestSlidingView(Context context) {
        super(context);
        initView(context, null);
    }

    public NestSlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        // 速度追踪器
        velocityTracker = VelocityTracker.obtain();
        // 滑动的类
        mScroller = new Scroller(getContext());
    }

    // 当所有的view都实例化完成之后的调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtil.e("---->onFinishInflate");
        // 找到对应的三个view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                Object tag = childAt.getTag();
                if (tag != null) {
                    if (tag.equals(ViewTag.Top)) {
                        mTopView = childAt;
                        LogUtil.e("找到了topView：" + mTopView);
                    } else if (tag.equals(ViewTag.Middle)) {
                        mMiddleView = childAt;

                        mButton = mMiddleView.findViewById(R.id.btn_test2);

                        LogUtil.e("找到了MiddleView：" + mMiddleView);
                    } else if (tag.equals(ViewTag.Bottom)) {
                        mBottomView = childAt;
                        LogUtil.e("找到了BottomView：" + mBottomView);
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtil.e("---> onMeasure");

        // 测量出宽度
        int width = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));

        // 测量出顶部宽度
        int topViewHeight = getViewHeight(mTopView, widthMeasureSpec, heightMeasureSpec);
        // 测量出中间高度
        int middleViewHeight = getViewHeight(mMiddleView, widthMeasureSpec, heightMeasureSpec);
        // 测量出底部高度
        int bottomViewHeight = getViewHeight(mBottomView, widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(width, (topViewHeight + middleViewHeight + bottomViewHeight));

        // measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtil.e("---> onLayout");
        int mHeight = 0;

        // 顶部的view区域
        int mTopHeight = mTopView.getMeasuredHeight();
        mHeight += mTopHeight;
        mTopView.layout(l, 0, r, mHeight);

        // 中间view的区域
        int mMiddleHeight = mMiddleView.getMeasuredHeight();

        int measuredWidth = mMiddleView.getMeasuredWidth();
        int i = (r - measuredWidth) / 2;

        mHeight += mMiddleHeight;
        mMiddleView.layout(i, mTopHeight, (measuredWidth + i), mHeight);

        //  底部view的区域
        int mBottomHeight = mBottomView.getMeasuredHeight();
        mBottomView.layout(l, mHeight, r, (mHeight + mBottomHeight));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // 如果view没有停止滑动的话，就立刻停止view的滑动
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int mX = (int) event.getX();
                int mY = (int) event.getY();

                int dX = mX - mDownX;
                int dY = mY - mDownY;

                // 滑动viewGroup中的子view
                // scrollBy(-dX, -dY);

                // 滑动整个view
                // ((View) getParent()).scrollBy(-dX, -dY);

                // 滑动固定的view
                // ViewParent parent = mButton.getParent();
                // ViewParent parent1 = parent.getParent();
                // ((View) parent1).scrollBy(-dX, -dY);

                LogUtil.e("滑动的dy:" + dY);

                mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), -dX, -dY, 3000);
                invalidate();

                mDownX = mX;
                mDownY = mY;

                break;
        }
        return true;
    }

    /**
     * @param view                    指定的view
     * @param parentWidthMeasureSpec  viewGroup的测量宽度
     * @param parentHeightMeasureSpec viewGroup的测量高度
     * @return 在ViewGroup中获取单个view的高度
     */
    private int getViewHeight(View view, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        int height = 0;
        if (view != null) {
            //  测量子view的宽高
            measureChild(view, parentWidthMeasureSpec, parentHeightMeasureSpec);
            // 获取顶部view的高度
            height = view.getMeasuredHeight();
        }
        return height;
    }

    //调用这个方法进行滚动，这里我们只滚动竖直方向
    public void scrollTo2(int y) {
        //参数1和参数2分别为滚动的起始点在水平、竖直方向的滚动偏移量
        //参数3和参数4为在水平和竖直方向上滚动的距离
        float x = getX();
        float y1 = getY();
        mScroller.startScroll((int) x, (int) y1, 0, y, 3000);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            // 滑动固定的view

            //  scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            // 滑动固定的view
            ViewParent parent = mButton.getParent();
            ViewParent parent1 = parent.getParent();
            ((View) parent1).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            invalidate();
        }

//        postInvalidate(); //允许在非主线程中出发重绘，它的出现就是简化我们在非UI线程更新view的步骤
//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.e("---> onDraw");
    }

    public void testRefesh() {
        invalidate();
    }
}
