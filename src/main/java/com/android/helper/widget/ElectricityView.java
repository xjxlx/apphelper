package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.android.helper.base.BaseView;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NumberUtil;

/**
 * @author : 流星
 * @CreateDate: 2022/1/12-5:47 下午
 * @Description: 电流的view
 */
public class ElectricityView extends BaseView {

    private float mProgressWidth; // 进度条的宽度
    private final float mProgressHeight = ConvertUtil.toDp(8); // 进度条的高度
    private final float mProgressRound = ConvertUtil.toDp(5);

    private Paint mPaintProgressBackground;
    private Paint mPaintProgress;

    // 进度条的范围值
    private int mProgressEnd = 62; // 最大的进度值
    private int mProgressTarget = 0; // 目标的进度值

    private Paint mPaintRound;
    private Paint mPaintBottomRoundText;
    private float mCurrentProgress;
    private float mTopInterval; // 进度条距离顶部的高度
    private String mBottomTextValue = "";// 圆球底部的文字

    // 进度条
    private final float mBottomValueInterval = ConvertUtil.toDp(8);
    private float mPercentage; // 进度条的百分比

    // 中间圆
    private final float mCircleRadius = ConvertUtil.toDp(12);// 圆的半径
    private float mBaseLineBottomText;
    private final float mPaddingLeft = ConvertUtil.toDp(20); // view的左间距 = 圆的半径
    private final float mPaddingTop = ConvertUtil.toDp(8);// view的上方padding值，避免遮挡阴影

    // 右侧的电流值
    private Paint mPaintRightText;
    private String mRightTextValue = "";
    private final float mRightInterval = ConvertUtil.toDp(4) + mPaddingLeft;
    private float mBaseLineRightText; // 右侧文字的高度

    private ProgressListener mProgressListener;
    private boolean mScroll = true;

    public ElectricityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        // 底部进度条
        mPaintProgressBackground = new Paint();
        mPaintProgressBackground.setAntiAlias(true);
        mPaintProgressBackground.setColor(Color.parseColor("#FFF4F5F9"));

        mPaintProgress = new Paint();
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setColor(Color.parseColor("#FF3FAAE4"));

        // 右侧的文字
        mPaintRightText = new Paint();
        mPaintRightText.setAntiAlias(true);
        mPaintRightText.setColor(Color.parseColor("#FF7A8499"));
        mPaintRightText.setTextSize(ConvertUtil.toSp(13));

        // 滑动的圆球
        mPaintRound = new Paint();
        mPaintRound.setStyle(Paint.Style.FILL);
        mPaintRound.setColor(Color.parseColor("#FFFFFFFF"));
        // 绘制阴影
        mPaintRound.setShadowLayer(30, 0, 0, Color.parseColor("#FF3FAAE4"));

        // 绘制圆球的底部文字
        mPaintBottomRoundText = new Paint();
        mPaintBottomRoundText.setAntiAlias(true);
        mPaintBottomRoundText.setColor(Color.parseColor("#FF3FAAE4"));
        mPaintBottomRoundText.setTextSize(ConvertUtil.toSp(12));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获取view的宽度
        float maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 右侧的电流值

        String format = NumberUtil.dataFormat(mProgressEnd + "");
        mRightTextValue = format + "A";
        float[] rightTextSize = CustomViewUtil.getTextSize(mPaintRightText, mRightTextValue);
        float rightTextWidth = rightTextSize[0];
        float rightTextHeight = rightTextSize[1];
        // 测量最大的高度
        float maxHeight = Math.max(rightTextHeight, mProgressHeight);
        // 右侧文字的基准线
        mBaseLineRightText = CustomViewUtil.getBaseLine(mPaintRightText, mRightTextValue);

        // 进度条宽高 = 总宽度 - 右侧文字宽度 - 文字间距 - 左侧间距
        mProgressWidth = maxWidth - mPaddingLeft - rightTextWidth - mRightInterval;
        // 根据最大的范围值和当前进度条的宽度，求出每个像素占用的百分比
        mPercentage = mProgressEnd / mProgressWidth;
        // LogUtil.e("默认进度条的百分比：" + mPercentage);
        // 当前的进度条
        mCurrentProgress = mProgressTarget / mPercentage;
        // LogUtil.e("默认进度条的目标：" + mProgressTarget + "  默认的位置：" + mCurrentProgress);

        // 进度条距离顶部的高度 =（ 最大高度 - 文字高度 ）/2
        float v1 = (mProgressHeight - rightTextHeight) / 2;
        mTopInterval = Math.max(v1, mTopInterval);

        // 绘制圆球
        // 计算最大的高度
        maxHeight = Math.max(mCircleRadius * 2, maxHeight);

        // 顶部的距离 = ( 圆球的高度 - 进度条的高度) /2
        float v2 = (mCircleRadius * 2 - mProgressHeight) / 2;
        // 算出圆球的顶部距离
        mTopInterval = Math.max(v2, mTopInterval);

        // 圆球底部的文字
        mBottomTextValue = mProgressTarget + "A";
        float[] textSizeBottomRoundText = CustomViewUtil.getTextSize(mPaintBottomRoundText, mBottomTextValue);
        mBaseLineBottomText = CustomViewUtil.getBaseLine(mPaintBottomRoundText, mBottomTextValue);
        float bottomTextWidth = textSizeBottomRoundText[0];
        // 计算最大的高度 = 文字高度 + 原先的高度 + 间距
        maxHeight += (textSizeBottomRoundText[1] + mBottomValueInterval);

        maxHeight += mPaddingTop;

        widthMeasureSpec = resolveSize((int) maxWidth, widthMeasureSpec);
        heightMeasureSpec = resolveSize((int) maxHeight, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        // 当前的进度条
        mCurrentProgress = mProgressTarget / mPercentage;
        // LogUtil.e("mCurrentProgress:" + mCurrentProgress + "  mProgressTarget:" + mProgressTarget);

        // 绘制背景
        canvas.drawRoundRect(mCurrentProgress + mPaddingLeft, mTopInterval + mPaddingTop, mProgressWidth + mPaddingLeft, mProgressHeight + mTopInterval + mPaddingTop, mProgressRound, mProgressRound, mPaintProgressBackground);
        // 绘制进度
        canvas.drawRoundRect(mPaddingLeft, mTopInterval + mPaddingTop, mCurrentProgress + mPaddingLeft, mProgressHeight + mTopInterval + mPaddingTop, mProgressRound, mProgressRound, mPaintProgress);

        // 绘制右侧的电流值
        canvas.drawText(mRightTextValue, 0, mRightTextValue.length(), (mPaddingLeft + mProgressWidth + mRightInterval), (mBaseLineRightText + mTopInterval + mPaddingTop), mPaintRightText);

        // 圆心的x轴 = 进度的值 + 左侧的间距
        // 圆的X轴圆心
        float circleDx = mCurrentProgress + mPaddingLeft;
        // 圆心的y轴 =  进度条高度 /2  + 进度条top 的值
        // 圆的Y轴圆心
        float circleDY = mProgressHeight / 2 + mTopInterval + mPaddingTop;

        canvas.drawCircle(circleDx, circleDY, mCircleRadius, mPaintRound);

        // 绘制圆球下的文字
        String format = NumberUtil.dataFormat(mProgressTarget + "");
        // 从新计算文字的宽度
        float width = CustomViewUtil.getTextSize(mPaintBottomRoundText, mBottomTextValue)[0];
        mBottomTextValue = format + "A";
        float dx = (circleDx - width / 2);// 圆球的X轴圆心  - 文字的宽度/2
        float dy = (circleDY + mCircleRadius + mBottomValueInterval + mBaseLineBottomText); // dy  = 圆球的底部 + 间距 +baseLine
        canvas.drawText(mBottomTextValue, 0, mBottomTextValue.length(), dx, dy, mPaintBottomRoundText);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mScroll) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                LogUtil.e("dispatchTouchEvent ---> ACTION_DOWN");
                return mScroll;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e("onTouchEvent ---> ACTION_DOWN");
                calculate(event.getX());
                return mScroll;

            case MotionEvent.ACTION_MOVE:
                // 获取当前的x轴位置
                float x = event.getX();
                calculate(x);
                break;

            case MotionEvent.ACTION_UP: // 抬起
                if (mProgressListener != null) {
                    mProgressListener.onTouchUp(mProgressTarget);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void calculate(float x) {
        // 当前滑动的位置 = 当前的x轴位置 * 每个像素所占用的百分比
        float v = x - mPaddingLeft;
        mProgressTarget = (int) (v * mPercentage);
        if (mProgressTarget < 0) {
            mProgressTarget = 0;
        } else if (mProgressTarget > mProgressEnd) {
            mProgressTarget = mProgressEnd;
        }

        // 重新绘制
        invalidate();
    }

    /**
     * 设置电流的最大区间值
     *
     * @param maxValue 最大电流
     */
    public void setMaxIntervalScope(@IntRange(from = 0, to = 62) int maxValue) {
        mProgressEnd = maxValue;
        invalidate();
    }

    /**
     * 设置电流的当前值
     *
     * @param currentValue 电流的当前值
     */
    public void setCurrentValue(@IntRange(from = 0, to = 62) int currentValue) {
        mProgressTarget = currentValue;
        invalidate();
    }

    /**
     * 设置进度的监听
     */
    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public void setScroll(boolean scroll) {
        this.mScroll = scroll;
    }

    public interface ProgressListener {
        /**
         * 手指抬起的进度
         */
        void onTouchUp(int progress);
    }

}