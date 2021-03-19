package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.helper.utils.LogUtil;
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

        for (Point point : mList) {
            canvas.drawCircle(point.x, point.y, 30, point.getPaint());
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

    private void addPoint(float x, float y) {

        Point point = new Point();
        point.setX(x);
        point.setY(y);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);// 空心
        paint.setAlpha(255);
        Random random = new Random();
        int round = random.nextInt(mColors.length);
        LogUtil.e("round:" + round);

        paint.setColor(round);
        paint.setStrokeWidth(20);

        point.setPaint(paint);
        point.setRadius(0);

        mList.add(point);

        refreshView();

        invalidate();
    }

    private void refreshView() {
        for (Point point : mList) {
            point.radius += 3;
        }

    }

    public static class Point {
        float x; // X轴
        float y; // Y轴
        float radius; // 圆心
        Paint paint; // 因为要设置不同的颜色，不同的透明度，所以每一次都要初始化一次

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
