package com.android.helper.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NumberUtil;
import com.android.helper.utils.ResourceUtil;

/**
 * @author : 流星
 * @CreateDate: 2022/1/7-5:23 下午
 * @Description: 充电的进度条
 */
public class ChargingProgressView extends View {

    private int mMaxWidth, mMaxHeight;

    private float mLineWidth = ConvertUtil.toDp(1);

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

    private float mPercentage = 0.351f;// 进度条的百分比 todo  如果电量值过小怎么办
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
    private float[] mOptimumTextSize;
    private final float mOptimumTextInterval = ConvertUtil.toDp(13);// 最佳值和进度条的间隔高度

    private float mTopInterval = 0;// 上侧最大的高度
    private float mBottomInterval = 0;// 下方的最大高度

    // 当前电量的进度
    private Paint mPaintCharging;
    private String mCurrentChargingText = "";// 当前电量的进度条
    private final float mCurrentChargingTextInterval = ConvertUtil.toDp(8);
    private float[] mCurrentChargingTextSize;

    // 充电剩余时间 #FF7A8499
    private Paint mPaintChargingRemainingTimeText;
    private final String mRemainingTimeText = "4小时20分";
    private final float mRemainingTimeTextInterval = ConvertUtil.toDp(8);
    private float[] mRemainingTimeTextSize;

    // 底部SOC
    private Paint mPaintSoc;
    private Bitmap mBitmapSoc;
    private final String mSocText = "目标SOC";
    private final float mSocLeftInterval = ConvertUtil.toDp(4f);
    private final float mSocTextTopInterval = ConvertUtil.toDp(8.5f);
    private final float mSocBitmapTopInterval = ConvertUtil.toDp(11.5f);
    private float[] mSocTextSize;

    // 底部的滑动条
    private float mBottomScrollProgress = 0.5f; // 默认的区间值
    private Paint mPaintBottomScrollLine;
    private float mBottomScrollProgressValue = 0;
    private Paint mPaintScrollRound;// 滑动的圆
    private float mScrollValue;// 滑动的值
    private Paint mPaintSocText; // SOC的进度的画笔
    private String mSocCurrentText = ""; // 当前滑动进度的值

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
        mPaintOptimum.setStrokeWidth(mLineWidth);
        mPaintOptimum.setTextSize(ConvertUtil.toSp(10.5f)); // 设置值的单位是像素

        // 绘制当前的电量进度
        mPaintCharging = new Paint();
        mPaintCharging.setColor(Color.parseColor("#FF333A4A"));
        mPaintCharging.setTextSize(ConvertUtil.toSp(18f));

        // 充电剩余时间
        mPaintChargingRemainingTimeText = new Paint();
        mPaintChargingRemainingTimeText.setColor(Color.parseColor("#FF7A8499"));
        mPaintChargingRemainingTimeText.setTextSize(ConvertUtil.toSp(13f));

        // 目标Soc
        mPaintSoc = new Paint();
        mPaintSoc.setColor(Color.parseColor("#FF7A8499"));
        mPaintSoc.setTextSize(ConvertUtil.toSp(10.5f));
        mBitmapSoc = ResourceUtil.getBitmap(R.mipmap.icon_charging_soc);

        // 滑动的区间值
        mPaintBottomScrollLine = new Paint();
        mPaintBottomScrollLine.setColor(Color.parseColor("#FFFF9C26"));
        mPaintBottomScrollLine.setTextSize(ConvertUtil.toSp(1f));
        mPaintBottomScrollLine.setStrokeWidth(mLineWidth);
        mPaintScrollRound = new Paint();
        mPaintScrollRound.setColor(Color.parseColor("#FFF4F4F4"));
        mPaintScrollRound.setStyle(Paint.Style.FILL);
        mPaintScrollRound.setMaskFilter(new BlurMaskFilter(ConvertUtil.toDp(1), BlurMaskFilter.Blur.SOLID)); // 阴影
        mPaintSocText = new Paint();
        mPaintSocText.setColor(Color.parseColor("#FF3E485A"));
        mPaintSocText.setTextSize(ConvertUtil.toDp(10.5f));
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
                mOptimumTextSize = CustomViewUtil.getTextSize(mPaintOptimum, OptimumContent);
            }
        }

        // 最佳值的高度间隔
        if ((mOptimumTextSize != null) && (mOptimumTextSize[1] > 0)) {
            // 最佳电量直的高度 =  文字本身高度 + 间距
            float optimumTextHeight = mOptimumTextSize[1] + mOptimumTextInterval;

            if (mTopInterval < optimumTextHeight) {
                mTopInterval = optimumTextHeight;
            }
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

        // 当前的进度 = 进度条的宽度 * 进度的百分比
        mProgress = mProgressWidth * mPercentage;

        // 绘制进度条的圆角矩形
        if (mPercentage >= 1) {
            // 外层矩形
            mRectFOuterLayer.left = 0;
            mRectFOuterLayer.top = mTopInterval;
            mRectFOuterLayer.right = mProgress;
            mRectFOuterLayer.bottom = mProgressHeight + mRectFOuterLayer.top;
            // 内层矩形
            mRectFNerLayer.left = mRectFOuterLayer.left + mIntervalLayer;
            mRectFNerLayer.top = mRectFOuterLayer.top + mIntervalLayer;
            mRectFNerLayer.right = mRectFOuterLayer.right - mIntervalLayer;
            mRectFNerLayer.bottom = mRectFOuterLayer.bottom - mIntervalLayer;

        } else {
            if (mPercentage > 0) {
                // 使用路径去绘制外层圆角矩形
                mPath_w.reset();
                mPath_w.addRoundRect(0, mTopInterval, mProgress, mProgressHeight + mTopInterval, mAngleArray, Path.Direction.CW);

                // 使用路径去绘制内层圆角矩形
                mPath_n.reset();
                mPath_n.addRoundRect(mIntervalLayer, mTopInterval + mIntervalLayer, mProgress - mIntervalLayer, mProgressHeight + mTopInterval - mIntervalLayer, mAngleArray, Path.Direction.CW);
            }
        }

        // 闪电符号
        if (mBitmap != null) {
            int bitmapWidth = mBitmap.getWidth();
            int bitmapHeight = mBitmap.getHeight();

            // src:bitmap的区域，dst:本次绘制的区域，把src放进dst中
            mRectDsc.left = ((mProgress - bitmapWidth) / 2);// left：( mProgress  - bitmap的宽 )/2
            // top = (进度条高度 - bitmap高度 )/2 + 顶部高度
            mRectDsc.top = (int) ((mProgressHeight - bitmapHeight) / 2 + mTopInterval);
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
        mRectFSection.bottom = mTopInterval + mProgressHeight; // 上方的距离高度 + 进度条的高度

        // 当前电量的进度值
        String multiply = NumberUtil.multiply(mPercentage + "", 100 + "");
        mCurrentChargingText = multiply + "%";
        mCurrentChargingTextSize = CustomViewUtil.getTextSize(mPaintCharging, mCurrentChargingText);

        // 比较当前的间距和总间距的大小
        float chargingInterval = mCurrentChargingTextSize[1] + mCurrentChargingTextInterval;
        if (mTopInterval < chargingInterval) {
            mTopInterval = chargingInterval;
        }

        // 当前剩余的充电时间
        mRemainingTimeTextSize = CustomViewUtil.getTextSize(mPaintChargingRemainingTimeText, mRemainingTimeText);
        float remainingTimeTextInterval = mRemainingTimeTextSize[1] + mRemainingTimeTextInterval;
        if (mTopInterval < remainingTimeTextInterval) {
            mTopInterval = remainingTimeTextInterval;
        }

        // SOC的图标
        if (mBitmapSoc != null) {
            int height = mBitmapSoc.getHeight();
            float socBitmapBottomInterval = height + mSocBitmapTopInterval;
            // 累加高度
            if (mBottomInterval < socBitmapBottomInterval) {
                mBottomInterval = socBitmapBottomInterval;
            }
        }

        // SOC的文字
        if (!TextUtils.isEmpty(mSocText)) {
            mSocTextSize = CustomViewUtil.getTextSize(mPaintSoc, mSocText);
            if (mSocTextSize != null) {
                float socTextInterval = mSocTextSize[1] + mSocTextTopInterval;
                if (mBottomInterval < socTextInterval) {
                    mBottomInterval = socTextInterval;
                }
            }
        }

        // 滑动的进度
        if (mBottomScrollProgress > 0) {
            mBottomScrollProgressValue = mBottomScrollProgress * mProgressWidth;
        }

        // 叠加当前的高度
        mMaxHeight += mTopInterval;
        mMaxHeight += mBottomInterval;

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

        // 绘制当前进度的进度条
        if (mPercentage >= 1) {
            // 绘制外层圆角矩形
            canvas.drawRoundRect(mRectFOuterLayer, mAngle, mAngle, mPaintRoundOuterLayer);
            // 绘制圆角矩形
            canvas.drawRoundRect(mRectFNerLayer, mAngle, mAngle, mPaintRoundNerLayer);
        } else {
            if (mPercentage > 0) {
                // 使用路径绘制外层圆角矩形
                canvas.drawPath(mPath_w, mPaintRoundOuterLayer);
                // 使用路径绘制内层圆角矩形
                canvas.drawPath(mPath_n, mPaintRoundNerLayer);
            }
        }

        // 区间
        canvas.drawRect(mRectFSection, mPaintSection);

        // 绘制闪电图标
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mRectSrc, mRectDsc, mPaintRoundNerLayer);
        }

        // 绘制最佳的进度 mOptimumPosition
        if (mShowOptimum) {
            if (mOptimumPosition > 0) {
                canvas.drawLine(mOptimumPosition, mTopInterval, mOptimumPosition, mMaxHeight - mBottomInterval, mPaintOptimum);
                // 绘制最佳值文字
                float dx = (mOptimumPosition - (mOptimumTextSize[0] / 2)); // 最佳值的x轴 - (文字的高度 /2)
                float dy = mMaxHeight - mOptimumTextInterval - mProgressHeight - mBottomInterval;// 总的高度 - 最佳值的间距 - 进度条的高度 - 底部高度
                canvas.drawText(OptimumContent, 0, OptimumContent.length(), dx, dy, mPaintOptimum);
            }
        }

        // 绘制当前电量的进度
        if (mCurrentChargingTextSize != null) {
            float dx = (mProgressWidth - mCurrentChargingTextSize[0]) / 2; // dx = (进度条宽度 - 文字宽度)/2
            float dy = mMaxHeight - mProgressHeight - mCurrentChargingTextInterval - mBottomInterval; // dy = 总高度 - 进度条的高度 - 间距 - 底部间距
            canvas.drawText(mCurrentChargingText, 0, mCurrentChargingText.length(), dx, dy, mPaintCharging);
        }

        // 绘制剩余的充电时间
        if (!TextUtils.isEmpty(mRemainingTimeText) && mRemainingTimeTextSize != null) {
            float dx = 0;
            float dy = mMaxHeight - mProgressHeight - mRemainingTimeTextInterval - mBottomInterval; // dy = 总高度 - 进度条 - 间距 - 底部间距
            canvas.drawText(mRemainingTimeText, 0, mRemainingTimeText.length(), dx, dy, mPaintChargingRemainingTimeText);
        }

        // 绘制SOC
        if (mBitmapSoc != null) {
            // 绘制图标
            float dy = mTopInterval + mProgressHeight + mSocBitmapTopInterval; // 上方高度 + 进度条高度 + 间距
            canvas.drawBitmap(mBitmapSoc, 0, dy, mPaintSoc);

            // 绘制SOC文字
            if (!TextUtils.isEmpty(mSocText)) {
                float dx = mBitmapSoc.getWidth() + mSocLeftInterval; // dx = bitmap宽度 + 间距
                float baseLine = CustomViewUtil.getBaseLine(mPaintSoc, mSocText);

                // 因为此处要计算基准线，特别麻烦。所以换成意外一种方式去计算 = dy + 图片高度  -  文字高度 + 基准线
                float dy2 = dy + mBitmapSoc.getHeight() - mSocTextSize[1] + baseLine;
                canvas.drawText(mSocText, dx, dy2, mPaintSoc);
            }
        }

        // 绘制滑动的区间值
        if (mBottomScrollProgressValue > 0) {
            mScrollValue = mBottomScrollProgressValue;
            // 绘制线
            canvas.drawLine(mScrollValue, mTopInterval, mScrollValue, mProgressHeight + mTopInterval, mPaintBottomScrollLine);
            // 绘制阴影
            float circleX, circleY;
            // 参数1：圆中心的X轴位置 = 当前进度的值
            circleX = mScrollValue;
            // 参数2：圆中心的Y轴位置 = 顶部间距 + 进度条高度 + 半径的高度
            circleY = mTopInterval + mProgressHeight + mRemainingTimeTextInterval;
            // 参数1：圆中心的X轴位置
            // 参数2：圆中心的Y轴位置
            // 参数3：圆的半径
            canvas.drawCircle(circleX, circleY, mRemainingTimeTextInterval, mPaintScrollRound);

            // 绘制SOC进度 todo
//            canvas.drawText();
        }
    }

}
