package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 波浪圆的view
 */
public class WareView extends View {

    // 颜色集合
    private final int[] mColors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};

    /**
     * 封装点击或者移动时候的按下位置
     */
    private final List<Point> mList = new ArrayList<>();

    public WareView(Context context) {
        super(context);
        initView(context, null);
    }

    public WareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制所有的圆形
        for (Point point : mList) {
            canvas.drawCircle(point.getX(), point.getY(), 30, point.getPaint());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                addPoint(event.getX(), event.getY());
                break;
        }
        return true;
    }

    /**
     * 添加一个新的圆形
     *
     * @param x 按下的X轴
     * @param y 按下的Y轴
     */
    private void addPoint(float x, float y) {

        Point point = new Point();
        point.setX(x);
        point.setY(y);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);// 空心
        paint.setAlpha(255);
        Random random = new Random();
        int round = random.nextInt(mColors.length);
        paint.setColor(mColors[round]);
        paint.setStrokeWidth(30);

        point.setPaint(paint);
        point.setPaint(paint);
        point.setRadius(0);

        mList.add(point);

        refreshView();

        invalidate();
    }

    private void refreshView() {

    }

    public static class Point {
        private float x; // X轴
        private float y; // Y轴
        private float radius; // 圆心
        private Paint paint; // 因为要设置不同的颜色，不同的透明度，所以每一次都要初始化一次

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }
    }
}
