package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.helper.base.BaseView;
import android.helper.utils.LogUtil;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

/**
 * 波浪圆的view
 */
public class WareView extends BaseView {

    // 颜色集合
    private final int[] mColors = new int[]{Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};

    private float cx;
    private float cy;
    private Paint mPain;
    private float mRadius; // 半径

    public WareView(Context context) {
        super(context);
    }

    public WareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        mRadius = 0;

        mPain = new Paint();
        mPain.setStyle(Paint.Style.STROKE);
        mPain.setColor(Color.RED);
        mPain.setStrokeWidth(30);
        mPain.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((cx > 0) && (cy > 0)) {
            canvas.drawCircle(cx, cy, mRadius, mPain);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                cx = event.getX();
                cy = event.getY();

                // 重新初始化消息
                initView(null, null);

                mHandler.sendEmptyMessage(1);
                break;
        }
        return super.onTouchEvent(event);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 想让圆环发生动态的改变
            LogUtil.e("持续发送消息---->");

            // 清空之前的handler,避免不停的轮询，导致数据异常
            removeMessages(1);

            // 宽度变大
            mRadius += 5;

            // view的宽度也渐渐变大
            mPain.setStrokeWidth(mRadius / 3);

            // 颜色变淡
            int alpha = mPain.getAlpha();
            alpha -= 5;
            if (alpha <= 0) {
                alpha = 0;
            }

            mPain.setAlpha(alpha);
            if (alpha <= 0) {
                mHandler.removeMessages(1);
            } else {
                invalidate();
                mHandler.sendEmptyMessageDelayed(1, 50);
            }
        }
    };

}
