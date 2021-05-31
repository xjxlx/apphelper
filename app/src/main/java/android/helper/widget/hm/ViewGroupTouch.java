package android.helper.widget.hm;

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
    public final String Tag = getClass().getSimpleName();
    private Rect mRect;
    private Paint mPaint;
    private View mChildView;
    private float mStartX;
    private float mStartY;
    private float mDx;
    private float mDy;

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "dispatchTouchEvent--->down");
                return true;
//                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "dispatchTouchEvent--->move");
                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "dispatchTouchEvent--->up");
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "onInterceptTouchEvent--->down");
                return true;
//                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "onInterceptTouchEvent--->move");

//                if (mChildView != null) {
//                    int left = mChildView.getLeft();
//                    int top = mChildView.getTop();
//                    int right = mChildView.getRight();
//                    int bottom = mChildView.getBottom();
//
//                    boolean isMove = false;
//
//                    if (left + mDx < mRect.left) {
//                        ToastUtil.show("停止移动");
//                        isMove = false;
//                    } else {
//                        isMove = true;
//                    }
//
//                    LogUtil.e(" isMove:" + isMove);
//                    return isMove;
//                }

                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "onInterceptTouchEvent--->up");
                return false;
//                break;
        }

        return super.onInterceptTouchEvent(ev);
//        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.e("left:" + mRect.left + " top:" + mRect.top + " right:" + mRect.right + " bottom:" + mRect.bottom);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e(Tag, "onTouchEvent--->down");

                mStartX = event.getX();
                mStartY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                LogUtil.e(Tag, "onTouchEvent--->move");

                float x = event.getX();
                float y = event.getY();

                // 获取偏移值
                mDx = x - mStartX;
                mDy = y - mStartY;

                // 把后面的值赋值给开始的值
                mStartX = x;
                mStartY = y;

                LogUtil.e("------>dx:" + mDx + "   dy:" + mDy);
                if (mChildView != null) {
                    int left = mChildView.getLeft();
                    int top = mChildView.getTop();
                    int right = mChildView.getRight();
                    int bottom = mChildView.getBottom();

                    LogUtil.e("left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom);

                    mChildView.layout(
                            (int) (left + mDx),
                            (int) (top + mDy),
                            (int) (right + mDx),
                            (int) (bottom + mDy)
                    );
                }
                break;

            case MotionEvent.ACTION_UP:
                LogUtil.e(Tag, "onTouchEvent--->up");
                break;
        }
//        return super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mRect, mPaint);
    }

}
