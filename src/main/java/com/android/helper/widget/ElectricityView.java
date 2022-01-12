package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.ResourceUtil;

/**
 * @author : 流星
 * @CreateDate: 2022/1/12-5:47 下午
 * @Description: 电流的view
 */
public class ElectricityView extends View {

    private float mMaxWidth, mMaxHeight;
    private float mProgressWidth; // 进度条的宽度
    private float mProgressHeight = ConvertUtil.toDp(8); // 进度条的高度
    private float mProgressRound = ConvertUtil.toDp(5);

    private Paint mPaintProgressBackground;
    private Paint mPaintProgress;

    // 进度条的范围值
    private float mProgressStart = 0f;
    private float mProgressEnd = 63f;
    private float mProgressCurrent = 20f; // 当前的进度值

    // 右侧的电流值
    private Paint mPaintRightText;
    private String mRightTextValue = "";
    private float[] mRightTextSize;
    private float mRightTextWidth;
    private float mRightInterval = ConvertUtil.toDp(8);
    private float mBaseLineRightText; // 右侧文字的高度

    // 左下角的文字
    private Paint mPaintLeftBottomText;
    private String mLeftBottomTextValue = "设置电流值";
    private float mLeftBottomInterval = ConvertUtil.toDp(8);

    private Paint mPaintRound;
    private Paint mPaintBottomRoundText;
    private float mBaseLineLeftBottom;
    private Bitmap mBitmap;
    private Rect mRectSrc;
    private RectF mRectFDst;
    private float mCurrentProgress;
    private float mTopInterval; // 进度条距离顶部的高度
    private String mBottomTextValue = "";// 圆球底部的文字
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mBottomTextWidth;

    private float mBottomValueInterval = ConvertUtil.toDp(8);

    public ElectricityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
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

        // 左下角文字
        mPaintLeftBottomText = new Paint();
        mPaintLeftBottomText.setAntiAlias(true);
        mPaintLeftBottomText.setColor(Color.parseColor("#FFB2B8C6"));
        mPaintLeftBottomText.setTextSize(ConvertUtil.toSp(11));

        // 滑动的圆球
        mPaintRound = new Paint();
        mPaintRound.setStyle(Paint.Style.FILL);

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

        // 宽高
        mMaxWidth = MeasureSpec.getSize(widthMeasureSpec);

        // 根据最大的范围值和当前进度条的宽度，求出每个像素占用的百分比
        float percentage = mProgressEnd / mProgressWidth;
        // 当前的进度条
        mCurrentProgress = mProgressCurrent / percentage;

        // 右侧的电流值
        mRightTextValue = mProgressEnd + "A";
        mRightTextSize = CustomViewUtil.getTextSize(mPaintRightText, mRightTextValue);
        mRightTextWidth = mRightTextSize[0];
        float rightTextHeight = mRightTextSize[1];
        mMaxHeight = Math.max(rightTextHeight, mProgressHeight);
        mBaseLineRightText = CustomViewUtil.getBaseLine(mPaintRightText, mRightTextValue);

        // 进度条宽高
        mProgressWidth = mMaxWidth - mRightTextWidth - mRightInterval;

        // 左下角的文字
        float[] LeftBottomTextSize = CustomViewUtil.getTextSize(mPaintLeftBottomText, mLeftBottomTextValue);
        float leftBottomTextHeight = LeftBottomTextSize[1];
        mMaxHeight += (leftBottomTextHeight + mLeftBottomInterval);
        mBaseLineLeftBottom = CustomViewUtil.getBaseLine(mPaintLeftBottomText, mLeftBottomTextValue);

        // 绘制圆球
        mBitmap = ResourceUtil.getBitmap(R.mipmap.icon_eu7_control_progress_thumb);
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        if (mBitmapHeight > mMaxHeight) {
            mMaxHeight = mBitmapHeight;
        }
        mRectSrc = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
        mRectFDst = new RectF();
        if (mBitmapWidth > 0) {
            float bitmapWidthFloat = (float) mBitmapWidth;
            mRectFDst.left = mCurrentProgress - bitmapWidthFloat / 2; // 当前进度条的值 + 图片的宽度/2
            mRectFDst.top = 0;
            mRectFDst.right = mRectFDst.left + mBitmapWidth;
            mRectFDst.bottom = mRectFDst.top + mBitmapHeight;
        }

        // 距离顶部的高度
        mTopInterval = (mBitmapHeight - mProgressHeight) / 2;

        // 圆球底部的文字
        mBottomTextValue = mProgressCurrent + "A";
        float[] textSizeBottomRoundText = CustomViewUtil.getTextSize(mPaintBottomRoundText, mBottomTextValue);
        float bottomRoundTextHeight = textSizeBottomRoundText[1];
        mBottomTextWidth = textSizeBottomRoundText[0];
        float bottomHeight = bottomRoundTextHeight + mBitmapHeight;
        if (mMaxHeight < bottomHeight) {
            mMaxHeight = bottomHeight;
        }

        widthMeasureSpec = resolveSize((int) mMaxWidth, widthMeasureSpec);
        heightMeasureSpec = resolveSize((int) mMaxHeight, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        // 绘制背景
        canvas.drawRoundRect(mCurrentProgress, mTopInterval, mProgressWidth, mProgressHeight + mTopInterval, mProgressRound, mProgressRound, mPaintProgressBackground);
        // 绘制进度
        canvas.drawRoundRect(0, mTopInterval, mCurrentProgress, mProgressHeight + mTopInterval, mProgressRound, mProgressRound, mPaintProgress);

        // 绘制右侧的电流值
        canvas.drawText(mRightTextValue, 0, mRightTextValue.length(), (mProgressWidth + mRightInterval), (mBaseLineRightText + mTopInterval), mPaintRightText);

        // 绘制左下角的文字
        canvas.drawText(mLeftBottomTextValue, 0, mLeftBottomTextValue.length(), 0, (mProgressHeight + mLeftBottomInterval + mBaseLineLeftBottom + mTopInterval), mPaintLeftBottomText);

        // 绘制圆球
        canvas.drawBitmap(mBitmap, mRectSrc, mRectFDst, mPaintRound);

        // 绘制圆球下的文字
        float dx = (mBitmapWidth - mBottomTextWidth) / 2 + mRectFDst.left;// 圆球的宽 - 文字的宽 /2  + 圆球的左侧
        float dy = mRectFDst.bottom + mBottomValueInterval; // dy  = 圆球的底部 + 间距
        canvas.drawText(mBottomTextValue, 0, mBottomTextValue.length(), dx, dy, mPaintBottomRoundText);
    }
}
