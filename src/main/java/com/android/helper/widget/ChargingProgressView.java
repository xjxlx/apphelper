package com.android.helper.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.ResourceUtil;

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

    private float mPercentage = 0.35f;// 进度条的百分比 todo  如果电量值过小怎么办
    private float mProgress = 0F;// 进度条的进度
    private Path mPath_w;
    private Path mPath_n;

    private float mPercentageStart = 0.4f; // 区间的开始值
    private float mPercentageEnd = 0.9f;   // 区间的结束值
    private Paint mPaintSection;
    private RectF mRectFSection;

    // 绘制闪电符号
    private Bitmap mBitmap;
    private Rect mRectSrc;
    private RectF mRectDsc;
    private Paint mPaintBitmap;

    // 最佳进度值
    private boolean mShowOptimum = true; // 是否展示最佳的电量值
    private float mPercentageOptimum = 0.9f;//最佳电量值
    private Paint mPaintOptimum;
    private float mOptimumPosition;
    private String OptimumContent = "";// 最佳的文字值
    private float[] mTextSize;
    private float mOptimumTextInterval = ConvertUtil.toDp(13);// 最佳值和进度条的间隔高度
    private float mOptimumTextHeight; // 最佳电量直的高度 =  文字本身高度 + 间距

    private float mTopInterval = 0;// 上侧最大的高度

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

        // 内层Paint
        mPaintRoundNerLayer = new Paint();
        mPaintRoundNerLayer.setColor(Color.parseColor("#FF2793DF"));
        mPaintRoundNerLayer.setStyle(Paint.Style.FILL);
        mRectFNerLayer = new RectF();

        // 绘制闪电标记
        mBitmap = ResourceUtil.getBitmap(R.mipmap.icon_custom_charge_center);
        mRectSrc = new Rect();
        mRectDsc = new RectF();
        mPaintBitmap = new Paint();
        mPaintBitmap.setStyle(Paint.Style.FILL);

        // 区间
        mPaintSection = new Paint();
        mPaintSection.setColor(Color.parseColor("#2BFF9C26"));
        mPaintSection.setStyle(Paint.Style.FILL);
        mRectFSection = new RectF();

        // 最佳电量值
        mPaintOptimum = new Paint();
        mPaintOptimum.setColor(Color.parseColor("#FF9AF5C1"));
        mPaintOptimum.setStyle(Paint.Style.FILL);
        mPaintOptimum.setStrokeWidth(ConvertUtil.toDp(1));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 最大的宽度
        mMaxWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 最大的高度
        mMaxHeight = mProgressHeight;

        LogUtil.e("size:" + mMaxWidth);

        // 进度条的宽度 =  view的总宽度 - 右侧矩形的宽度
        mProgressWidth = mMaxWidth - mRightRectWidth;

        // 最佳电量直
        if (mShowOptimum) {
            if (mPercentageOptimum > 0) {
                mOptimumPosition = mProgressWidth * mPercentageOptimum;
                // 最佳的数据值
                OptimumContent = (mPercentageOptimum * 100) + "%最佳";
                mTextSize = CustomViewUtil.getTextSize(mPaintOptimum, OptimumContent);
            }
        }

        // 最佳值的高度间隔
        if ((mTextSize != null) && (mTextSize[1] > 0)) {
            mOptimumTextHeight = mTextSize[1] + mOptimumTextInterval;

            if (mTopInterval < mOptimumTextHeight) {
                mTopInterval = mOptimumTextHeight;
            }
            mMaxHeight += mTopInterval;
        }

        // 底层 = 整个宽度 - 右侧矩形的宽度
        mRectFBackground.left = 0;
        mRectFBackground.top = mTopInterval;
        mRectFBackground.right = mMaxWidth - mRightRectWidth;
        mRectFBackground.bottom = mProgressHeight + mTopInterval;

        // 右侧
        mRectFRight.left = mRectFBackground.right;
        mRectFRight.top = ((mProgressHeight - mRightRectHeight) / 2) + mTopInterval;
        mRectFRight.right = mRectFBackground.right + mRightRectWidth;
        mRectFRight.bottom = mRectFRight.top + mRightRectHeight;

        // 当前的进度
        mProgress = mProgressWidth * mPercentage;

        // 外层的矩形宽度 = 进度条的宽度 * 进度的百分比
        if (mPercentage >= 1) {
            mRectFOuterLayer.left = 0;
            mRectFOuterLayer.top = mTopInterval;
            mRectFOuterLayer.right = mProgress;
            mRectFOuterLayer.bottom = mMaxHeight;
        } else {
            if (mPercentage > 0) {
                // 使用路径去绘制
                mPath_w.reset();
                mPath_w.addRoundRect(0, mTopInterval, mProgress, mMaxHeight, mAngleArray, Path.Direction.CW);
            }
        }

        // 内层
        if (mPercentage >= 1) {
            mRectFNerLayer.left = mRectFOuterLayer.left + mIntervalLayer;
            mRectFNerLayer.top = mRectFOuterLayer.top + mIntervalLayer;
            mRectFNerLayer.right = mRectFOuterLayer.right - mIntervalLayer;
            mRectFNerLayer.bottom = mRectFOuterLayer.bottom - mIntervalLayer;
        } else {
            if (mPercentage > 0) {
                // 使用路径去绘制
                mPath_n.reset();
                mPath_n.addRoundRect(mIntervalLayer, mTopInterval + mIntervalLayer, mProgress - mIntervalLayer, mMaxHeight - mIntervalLayer, mAngleArray, Path.Direction.CW);
            }
        }

        // 闪电符号
        if (mBitmap != null) {
            int bitmapWidth = mBitmap.getWidth();
            int bitmapHeight = mBitmap.getHeight();

            // src:bitmap的区域，dst:本次绘制的区域，把src放进dst中
            mRectDsc.left = ((mProgress - bitmapWidth) / 2);// left：( mProgress  - bitmap的宽 )/2
            // src：DSC :top:(整体高低 - bitmap高度)/ /2 + 顶部的高度
            // src：DSC :top:(整体高低 - 顶部的高度 - （进度条高度 -bitmap高度）/2
            mRectDsc.top = (int) ((mMaxHeight - mTopInterval - (mProgressHeight - bitmapHeight) / 2));
            mRectDsc.right = mRectDsc.left + bitmapWidth;
            mRectDsc.bottom = mRectDsc.top + bitmapHeight;

            mRectSrc.left = 0;
            mRectSrc.top = 0;
            mRectSrc.right = bitmapWidth;
            mRectSrc.bottom = (int) bitmapHeight;
        }

        // 区间
        float sectionStart = mProgressWidth * mPercentageStart;
        float sectionEnd = mProgressWidth * mPercentageEnd;
        mRectFSection.left = sectionStart;
        mRectFSection.top = mTopInterval;
        mRectFSection.right = sectionEnd;
        mRectFSection.bottom = mMaxHeight;

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

        // 区间
        canvas.drawRect(mRectFSection, mPaintSection);

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mRectSrc, mRectDsc, mPaintRoundNerLayer);
        }

        // 绘制最佳的进度 mOptimumPosition
        if (mOptimumPosition > 0) {
            canvas.drawLine(mOptimumPosition, mTopInterval, mOptimumPosition, mMaxHeight, mPaintOptimum);
        }

    }
}
