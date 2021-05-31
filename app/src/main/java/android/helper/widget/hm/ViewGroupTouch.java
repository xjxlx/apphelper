package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.helper.utils.LogUtil;

public class ViewGroupTouch extends RelativeLayout {
    public final String Tag = "ViewGroup";
    private Rect mRect;
    private Paint mPaint;
    private View mChildView;
    private float mStartX;
    private float mStartY;
    private float mDx;
    private float mDy;
    private int left;
    private int top;
    private int right;
    private int bottom;

    public ViewGroupTouch(@NonNull Context context) {
        super(context);
    }

    public ViewGroupTouch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mRect = new Rect(200, 400, 800, 1200);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);

        // 自动调用onDraw方法
        setWillNotDraw(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 0) {
            View childAt = getChildAt(0);
            if (childAt != null) {
                mChildView = childAt;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "dispatchTouchEvent--->down");
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "dispatchTouchEvent--->move");

                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "dispatchTouchEvent--->up");
                break;
        }
        return super.dispatchTouchEvent(event);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                LogUtil.e(Tag, "onInterceptTouchEvent--->down");
//
//                mStartX = event.getX();
//                mStartY = event.getY();
//
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                LogUtil.e(Tag, "onInterceptTouchEvent--->move");
//
//                float eventX = event.getX();
//                float eventY = event.getY();
//                mDx = eventX - mStartX;
//                mDy = eventY - mStartY;
//
//                left = mChildView.getLeft();
//                top = mChildView.getTop();
//                right = mChildView.getRight();
//                bottom = mChildView.getBottom();
//
//                LogUtil.e("I-left:" + left + " I-top:" + top + " I-right:" + right + " I-bottom:" + bottom + " I-dx:" + mDx + "  I-dy:" + mDy);
//
//                mStartX = eventX;
//                mStartY = eventY;
//
//                LogUtil.e("left:" + this.left + "  dx:" + mDx + "   ==" + (this.left + mDx) + "  rect:" + mRect.left + "   result:" + ((this.left + mDx) < mRect.left));
//                if ((this.left + mDx) < mRect.left) {
//                    LogUtil.e("超出了左侧的边界，禁止先下传递！");
//                    // 禁止向下传递
//                    return true;
//                } else {
//                    LogUtil.e("没有超出左侧的边界，继续向下传递！");
//                    // 继续向下传递
//                    return false;
//                }
//
////                break;
//
//            case MotionEvent.ACTION_UP:
//                LogUtil.e(Tag, "onInterceptTouchEvent--->up");
//                break;
//        }
//
//        return true;
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "onInterceptTouchEvent--->down");
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "onInterceptTouchEvent--->move");

                LogUtil.e("I-left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom + " dx:" + mDx + "  dy:" + mDy);

                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "onInterceptTouchEvent--->up");
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "onTouchEvent--->down");

                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "onTouchEvent--->move");

                LogUtil.e("left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom + " dx:" + mDx + "  dy:" + mDy);

//                if (mChildView != null) {
//                    mChildView.layout((int) (left + mDx), (int) (top + mDy), (int) (right + mDx), (int) (bottom + mDy));
//                }

                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "onTouchEvent--->up");
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mRect, mPaint);
    }

}
