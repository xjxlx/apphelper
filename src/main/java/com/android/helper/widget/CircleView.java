package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.android.helper.base.BaseView;
import com.android.helper.utils.LogUtil;

/**
 * @author : 流星
 * @CreateDate: 2021/12/25-15:02
 * @Description:
 */
public class CircleView extends BaseView {

    private Paint paint;
    private String text = "50";
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mWidth;
    private int mHeight;

    public CircleView(Context context) {
        super(context);
        initView(context, null);
    }

    public CircleView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {

        // 实例化画笔对象
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN); // 给画笔设置颜色
        paint.setTextSize(100);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 计算文字的宽高

        Rect rect = new Rect(); // 文字所在区域的矩形
        paint.getTextBounds(text, 0, text.length(), rect);
        mWidth = rect.width();
        mHeight = rect.height();
        LogUtil.e("width:" + mWidth + "  height:" + mHeight);

        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();

        setMeasuredDimension(mWidth * 10, mHeight * 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
          /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
//        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 200, paint);

        if (mMeasuredWidth > 0 && mMeasuredHeight > 0) {
            canvas.drawText(text, mMeasuredWidth / 2 -mWidth/2, mMeasuredHeight / 2, paint);
        }
    }
}
