package com.android.helper.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.LogUtil;

/**
 * @author : 流星
 * @CreateDate: 2022/1/7-5:23 下午
 * @Description: 充电的进度条
 */
public class ChargingProgressView extends View {

    private int mMaxWidth, mMaxHeight;

    private float mProgressWidth;// 进度条的宽度
    private final int mProgressHeight = (int) ConvertUtil.toDp(60); // 进度条的高度

    private final float mAngle = ConvertUtil.toDp(16);
    private final float mIntervalLayer = ConvertUtil.toDp(4);// 外层和内层圆形的间距

    private final float mRightRectWidth = ConvertUtil.toDp(6);// 右侧view的宽高
    private final float mRightRectHeight = ConvertUtil.toDp(15);// 右侧view的宽高

    private Paint mPaintBackground; // 底层进度条
    private RectF mRectFBackground;
    private Paint mPaintRight; // 右侧进度条
    private RectF mRectFRight;
    private RectF mRectFOuterLayer; // 外层矩形
    private Paint mPaintRoundOuterLayer;
    private final float[] mAngleArray = new float[]{mAngle, mAngle, 0, 0, 0, 0, mAngle, mAngle};

    private RectF mRectFNerLayer;   // 内层矩形
    private Paint mPaintRoundNerLayer;

    private float mPercentage = 0.5f;// 进度条的百分比
    private float mProgress = 0F;// 进度条的进度
    private Path mPath_w;
    private Path mPath_n;

    public ChargingProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 底层的Paint
        mPaintBackground = new Paint();
        mPaintBackground.setColor(Color.parseColor("#FFF4F4F4"));
        mPaintBackground.setStyle(Paint.Style.FILL);
        mRectFBackground = new RectF();
        mRectFBackground.left = 0;
        mRectFBackground.top = 0;

        // 右侧Paint
        mPaintRight = new Paint();
        mPaintRight.setColor(Color.parseColor("#FFF4F4F4"));
        mPaintRight.setStyle(Paint.Style.FILL);
        mRectFRight = new RectF();

        mPath_w = new Path();
        mPath_n = new Path();

        // 外层Paint
        mPaintRoundOuterLayer = new Paint();
        mPaintRoundOuterLayer.setColor(Color.parseColor("#FF09B6F7"));
        mPaintRoundOuterLayer.setStyle(Paint.Style.FILL);
        mRectFOuterLayer = new RectF();
        mRectFOuterLayer.left = 0;
        mRectFOuterLayer.top = 0;

        // 内层Paint
        mPaintRoundNerLayer = new Paint();
        mPaintRoundNerLayer.setColor(Color.parseColor("#FF2793DF"));
        mPaintRoundNerLayer.setStyle(Paint.Style.FILL);
        mRectFNerLayer = new RectF();
        mRectFNerLayer.left = mRectFOuterLayer.left + mIntervalLayer;
        mRectFNerLayer.top = mRectFOuterLayer.top + mIntervalLayer;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMaxWidth = MeasureSpec.getSize(widthMeasureSpec);
        LogUtil.e("size:" + mMaxWidth);

        // 进度条的宽度 =  view的总宽度 - 右侧矩形的宽度
        mProgressWidth = mMaxWidth - mRightRectWidth;

        // 底层 = 整个宽度 - 右侧矩形的宽度
        mRectFBackground.right = mMaxWidth - mRightRectWidth;
        mRectFBackground.bottom = mProgressHeight;

        // 右侧
        mRectFRight.left = mRectFBackground.right;
        mRectFRight.top = (mProgressHeight - mRightRectHeight) / 2;
        mRectFRight.right = mRectFBackground.right + mRightRectWidth;
        mRectFRight.bottom = mRectFRight.top + mRightRectHeight;

        // 外层的矩形宽度 = 进度条的宽度 * 进度的百分比
        if (mPercentage >= 1) {
            mRectFOuterLayer.right = mProgress;
            mRectFOuterLayer.bottom = mMaxHeight;
        } else {
            // 使用路径去绘制
            mPath_w.reset();
            mPath_w.addRoundRect(0, 0, mProgress, mMaxHeight, mAngleArray, Path.Direction.CW);
        }

        // 当前的进度
        mProgress = mProgressWidth * mPercentage;

        // 内层
        if (mPercentage >= 1) {
            mRectFNerLayer.right = mRectFOuterLayer.right - mIntervalLayer;
            mRectFNerLayer.bottom = mRectFOuterLayer.bottom - mIntervalLayer;
        } else {
            // 使用路径去绘制
            mPath_n.reset();
            mPath_n.addRoundRect(0, 0, mRectFOuterLayer.right - mIntervalLayer, mRectFOuterLayer.bottom - mIntervalLayer, mAngleArray, Path.Direction.CW);
        }

        // 最大的高度
        mMaxHeight = mProgressHeight;

        // 重新测量
        widthMeasureSpec = resolveSize(mMaxWidth, widthMeasureSpec);
        heightMeasureSpec = resolveSize(mMaxHeight, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        // 绘制底层进度条
        canvas.drawRoundRect(mRectFBackground, mAngle, mAngle, mPaintBackground);
        // 绘制右侧矩形
        canvas.drawRect(mRectFRight, mPaintRight);

        // 绘制外层圆角矩形
        if (mPercentage >= 1) {
            canvas.drawRoundRect(mRectFOuterLayer, mAngle, mAngle, mPaintRoundOuterLayer);
        } else {
            // 使用路径绘制
            canvas.drawPath(mPath_w, mPaintRoundOuterLayer);
        }

        // 绘制内层
        if (mPercentage >= 1) {
            canvas.drawRoundRect(mRectFNerLayer, mAngle, mAngle, mPaintRoundNerLayer);
        } else {
            // 使用路径绘制
            canvas.drawPath(mPath_n, mPaintRoundNerLayer);
        }

    }
}
