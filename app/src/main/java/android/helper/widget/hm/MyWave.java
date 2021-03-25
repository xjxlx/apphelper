package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 水波纹效果
 *
 * @author Kevin
 * @date 2015-12-16
 */
public class MyWave extends View {

    // 颜色集合
    private int[] mColors = new int[]{Color.RED, Color.BLUE, Color.YELLOW,
            Color.GREEN};
    private float cx;
    private float cy;
    private Paint mPaint;

    public MyWave(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MyWave(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(50);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
//        mPaint.setAntiAlias(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                cx = event.getX();
                cy = event.getY();

                invalidate();
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(cx, cy, 50, mPaint);
    }

    // 圆环对象封装
    class Wave {
        public int cx;
        public int cy;
        public int radius;
        public Paint paint;
    }

}
