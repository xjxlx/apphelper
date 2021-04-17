package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.android.helper.base.BaseView;
import com.android.helper.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 波浪圆的view
 */
public class WareView extends BaseView {

    // 颜色集合
    private final int[] mColors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};

    // 圆环的集合
    private final List<Point> mListData = new ArrayList<>();

    public WareView(Context context) {
        super(context);
    }

    public WareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Point point : mListData) {

            canvas.drawCircle(point.x, point.y, point.radius, point.paint);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 1:在按下或者移动的时候，去添加一个点到集合中去

                if (mListData.isEmpty()) {
                    addPoint(event.getX(), event.getY());

                    mHandler.sendEmptyMessage(1);
                } else {
                    // 上一个view
                    Point point = mListData.get(mListData.size() - 1);

                    // 避免间距过大
                    if (((Math.abs(point.x - (event.getX())) > 10)) || ((Math.abs(point.y - (event.getY())) > 10))) {
                        addPoint(event.getX(), event.getY());
                    }
                }

                break;
        }

        return true;
    }

    private void addPoint(float x, float y) {
        Point point = new Point();
        point.x = x;
        point.y = y;
        point.radius = 2;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAlpha(255);
        int index = (int) (Math.random() * mColors.length);
        paint.setColor(mColors[index]);
        paint.setAntiAlias(true);

        point.paint = paint;

        mListData.add(point);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ArrayList<Point> removeList = new ArrayList<>();

            LogUtil.e("handler在轮询");
            // 让圆圈动起来
            for (Point point : mListData) {

                point.radius += 5; // 半径增加
                point.paint.setStrokeWidth(point.radius / 3); // view的宽度随着扩散而变大
                int alpha = point.paint.getAlpha();
                alpha -= 5;
                if (alpha <= 0) {
                    alpha = 0;// 避免透明度为负数
                    // 移除不用的view
                    //  mListData.remove(point); 便利集合的时候，不能去操作集合，不然会触发并发的异常
                    removeList.add(point);
                }

                point.paint.setAlpha(alpha);// 透明度递减
            }

            // 移除不用的集合view
            mListData.removeAll(removeList);

            if (!mListData.isEmpty()) {
                // 循环便利
                mHandler.sendEmptyMessageDelayed(1, 50);
                invalidate();
            }
        }
    };

    static class Point {
        public float x;
        public float y;
        public float radius;
        public Paint paint;
    }
}
