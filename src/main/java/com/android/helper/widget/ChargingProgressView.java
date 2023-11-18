package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.android.common.base.BaseView;
import com.android.common.utils.LogUtil;
import com.android.common.utils.LogWriteUtil;
import com.android.helper.R;
import com.android.helper.common.CommonConstants;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.NumberUtil;

import java.math.BigDecimal;

/**
 * @author : 流星
 * @CreateDate: 2022/1/7-5:23 下午
 * @Description: 充电的进度条
 */
public class ChargingProgressView extends BaseView {

    private final float mLineWidth = ConvertUtil.toDp(1);
    private final float mAngle = ConvertUtil.toDp(10);
    private final float mIntervalLayer = ConvertUtil.toDp(4);// 外层和内层圆形的间距
    private final float mRightRectWidth = ConvertUtil.toDp(6);// 右侧view的宽高
    private final float mRightRectHeight = ConvertUtil.toDp(15);// 右侧view的宽高
    private final float[] mAngleArray = new float[]{mAngle, mAngle, 0, 0, 0, 0, mAngle, mAngle};
    private final float[] mAngleArrayRight = new float[]{0, 0, mAngle, mAngle, mAngle, mAngle, 0, 0};
    private final float[] mAngleArrayLeftRight = new float[]{mAngle, mAngle, mAngle, mAngle, mAngle, mAngle, mAngle, mAngle};
    private final int mProgressHeight = (int) ConvertUtil.toDp(60); // 进度条的高度
    private final float mOptimumTextInterval = ConvertUtil.toDp(13);// 最佳值和进度条的间隔高度
    private final float mCurrentChargingTextInterval = ConvertUtil.toDp(8);
    private final float mRemainingTimeTextInterval = ConvertUtil.toDp(8); // 运动球的半径
    private final float mScrollTextInterval = ConvertUtil.toDp(6);
    // private final String mSocText = "目标SOC";
    private final float mSocLeftInterval = ConvertUtil.toDp(4f);
    private final float mSocTextTopInterval = ConvertUtil.toDp(8.5f);
    private final float mSocBitmapTopInterval = ConvertUtil.toDp(11.5f);
    private final float mPaddingRight = mRemainingTimeTextInterval;// 右侧的间距
    private final float mPaddingBottom = ConvertUtil.toDp(5); // 底部的间距
    private LogWriteUtil mWriteUtil = new LogWriteUtil(CommonConstants.FILE_CHARGING_CENTER_NAME + ".txt");
    private int mMaxWidth, mMaxHeight;
    private Paint mPaintBackground; // 底层进度条
    private RectF mRectFBackground;
    private Paint mPaintRight; // 右侧进度条
    private RectF mRectFRight;
    private RectF mRectFOuterLayer; // 外层矩形
    private Paint mPaintRoundOuterLayer;
    private float mProgressWidth;// 进度条的宽度
    private RectF mRectFNerLayer; // 内层矩形
    private Paint mPaintRoundNerLayer;
    private float mPercentage = 0f;// 进度条的百分比
    private float mProgress = 0F;// 进度条的进度
    private Path mPath_w;
    private Path mPath_n;
    // 绘制闪电符号
    private Bitmap mBitmap;
    private Rect mRectSrc;
    private RectF mRectDsc;
    // 区间值的百分比
    private float mPercentageStart = 0f; // 区间的开始值
    private float mPercentageEnd = 0f; // 区间的结束值
    private Paint mPaintSection;
    private RectF mRectFSection;
    private Path mPath_qj;
    // 最佳进度值
    private boolean mShowOptimum = true; // 是否展示最佳的电量值
    private float mPercentageOptimum = 0f;// 最佳电量值 ---> 固定值
    private Paint mPaintOptimum;
    private float mOptimumPosition;
    private String OptimumContent = "";// 最佳的文字值
    private float[] mOptimumTextSize;
    private float mTopInterval = 0;// 上侧最大的高度
    private float mBottomInterval = 0;// 下方的最大高度
    // 当前电量的进度
    private Paint mPaintCharging;
    private String mCurrentChargingText = "";// 当前电量的进度条
    private float[] mCurrentChargingTextSize; // 当前进度的
    // 充电剩余时间
    private Paint mPaintChargingRemainingTimeText;
    private String mRemainingTimeText = ""; // 临时的充电时间
    private float[] mRemainingTimeTextSize;
    // 底部的滑动条
    private float mBottomScrollProgress = 0.6f; // 默认的区间值
    private Paint mPaintBottomScrollLine;
    private float mBottomScrollProgressValue = 0; // 区间滑动的进度值
    private Paint mPaintScrollRound;// 滑动的圆
    private String mSocCurrentText = ""; // 当前滑动进度的值
    private Paint mPaintScrollValue;
    // 底部SOC
    private Paint mPaintSoc;
    private Bitmap mBitmapSoc;
    private float[] mSocTextSize;
    private float mScrollTextHeight;
    private float mStartBorder;
    private float mEndBorder;
    private float mScrollTextWidth; // 底部滑动文字的宽度
    private boolean isCharging = false;// 是否在充电中，控制闪电符号是否显示
    private float mLeft = 0;
    private float mRight = 0;
    private ProgressListener mProgressListener;
    private boolean mScroll = true;// 是否可以触摸
    private String mMultiply;
    private String mDivideAndRemainder;

    public ChargingProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        // 底层白色进度条的Paint
        mPaintBackground = new Paint();
        mPaintBackground.setColor(Color.parseColor("#FFF4F4F4"));
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setAntiAlias(true);
        mRectFBackground = new RectF();

        // 右侧矩形Paint
        mPaintRight = new Paint();
        mPaintRight.setColor(Color.parseColor("#FFF4F4F4"));
        mPaintRight.setStyle(Paint.Style.FILL);
        mPaintRight.setAntiAlias(true);
        mRectFRight = new RectF();

        // 外层和内层的路径
        mPath_w = new Path();
        mPath_n = new Path();

        // 外层Paint
        mPaintRoundOuterLayer = new Paint();
        mPaintRoundOuterLayer.setColor(Color.parseColor("#FF09B6F7"));
        mPaintRoundOuterLayer.setStyle(Paint.Style.FILL);
        mPaintRoundOuterLayer.setAntiAlias(true);
        mRectFOuterLayer = new RectF();

        // 内层Paint
        mPaintRoundNerLayer = new Paint();
        mPaintRoundNerLayer.setColor(Color.parseColor("#FF2793DF"));
        mPaintRoundNerLayer.setStyle(Paint.Style.FILL);
        mPaintRoundNerLayer.setAntiAlias(true);
        mRectFNerLayer = new RectF();

        // 绘制闪电标记
        mBitmap = getBitmap(context, R.mipmap.icon_custom_charge_center);
        mRectSrc = new Rect();
        mRectDsc = new RectF();
        Paint paintBitmap = new Paint();
        paintBitmap.setAntiAlias(true);
        paintBitmap.setStyle(Paint.Style.FILL);

        // 区间
        mPath_qj = new Path();
        mPaintSection = new Paint();
        mPaintSection.setColor(Color.parseColor("#2BFF9C26"));
        mPaintSection.setStyle(Paint.Style.FILL);
        mPaintSection.setAntiAlias(true);
        mRectFSection = new RectF();

        // 滑动的线
        mPaintBottomScrollLine = new Paint();
        mPaintBottomScrollLine.setColor(Color.parseColor("#FFFF9C26"));
        mPaintBottomScrollLine.setTextSize(ConvertUtil.toSp(1f));
        mPaintBottomScrollLine.setStrokeWidth(mLineWidth);
        mPaintBottomScrollLine.setAntiAlias(true);

        // 区间滑动的球
        mPaintScrollRound = new Paint();
        mPaintScrollRound.setColor(Color.parseColor("#FFF4F4F4"));
        mPaintScrollRound.setStyle(Paint.Style.FILL);
        // mPaintScrollRound.setMaskFilter(new BlurMaskFilter(ConvertUtil.toDp(1),
        // BlurMaskFilter.Blur.SOLID)); // 阴影
        mPaintScrollRound.setShadowLayer(20, 0, 0, Color.parseColor("#FFC9C9C9"));
        mPaintScrollRound.setAntiAlias(true);

        // 最佳电量值
        mPaintOptimum = new Paint();
        mPaintOptimum.setColor(Color.parseColor("#FF9AF5C1"));
        mPaintOptimum.setStyle(Paint.Style.FILL);
        mPaintOptimum.setStrokeWidth(mLineWidth);
        mPaintOptimum.setTextSize(ConvertUtil.toSp(10.5f)); // 设置值的单位是像素
        mPaintOptimum.setAntiAlias(true);

        // 绘制当前的电量进度
        mPaintCharging = new Paint();
        mPaintCharging.setColor(Color.parseColor("#FFFFFFFF"));
        mPaintCharging.setTextSize(ConvertUtil.toSp(18f));
        mPaintCharging.setAntiAlias(true);

        // 充电剩余时间
        mPaintChargingRemainingTimeText = new Paint();
        mPaintChargingRemainingTimeText.setColor(Color.parseColor("#FF7A8499"));
        mPaintChargingRemainingTimeText.setTextSize(ConvertUtil.toSp(13f));
        mPaintChargingRemainingTimeText.setAntiAlias(true);

        // 目标Soc
        mPaintSoc = new Paint();
        mPaintSoc.setColor(Color.parseColor("#FF7A8499"));
        mPaintSoc.setTextSize(ConvertUtil.toSp(10.5f));
        mPaintSoc.setAntiAlias(true);
        mBitmapSoc = getBitmap(context, R.mipmap.icon_charging_soc);

        // SOC的进度的画笔
        Paint paintSocText = new Paint();
        paintSocText.setColor(Color.parseColor("#FF3E485A"));
        paintSocText.setTextSize(ConvertUtil.toDp(10.5f));
        paintSocText.setAntiAlias(true);

        mPaintScrollValue = new Paint();
        mPaintScrollValue.setColor(Color.parseColor("#FF3E485A"));
        mPaintScrollValue.setTextSize(ConvertUtil.toDp(10.5f));
        mPaintScrollValue.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasur

        // 最大的宽度
        mMaxWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 最大的高度
        mMaxHeight = mProgressHeight;

        // 进度条的宽度 = view的总宽度 - 右侧矩形的宽度 - 右侧的间距
        mProgressWidth = mMaxWidth - mRightRectWidth - mPaddingRight;

        // 滑动区域的限制
        mStartBorder = mProgressWidth * mPercentageStart;// 开始的边界
        mEndBorder = mProgressWidth * mPercentageEnd; // 结束的边界

        // 当前的进度 = 进度条的宽度 * 进度的百分比
        mProgress = mProgressWidth * mPercentage;

        // 底层 = 整个宽度 - 右侧矩形的宽度
        mRectFBackground.left = 0;
        mRectFBackground.top = mTopInterval;
        mRectFBackground.right = mMaxWidth - mRightRectWidth - mPaddingRight;
        mRectFBackground.bottom = mProgressHeight + mTopInterval;

        // 右侧
        mRectFRight.left = mRectFBackground.right;
        mRectFRight.top = ((mProgressHeight - mRightRectHeight) / 2) + mTopInterval;
        mRectFRight.right = mRectFBackground.right + mRightRectWidth;
        mRectFRight.bottom = mRectFRight.top + mRightRectHeight;

        // 绘制进度条的圆角矩形
        if (mPercentage > 0) {
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
                // 使用路径去绘制外层圆角矩形
                mPath_w.reset();
                mPath_w.addRoundRect(0, mTopInterval, mProgress, mProgressHeight + mTopInterval, mAngleArray, Path.Direction.CW);

                // 使用路径去绘制内层圆角矩形
                mPath_n.reset();
                mPath_n.addRoundRect(mIntervalLayer, mTopInterval + mIntervalLayer, mProgress - mIntervalLayer, mProgressHeight + mTopInterval - mIntervalLayer, mAngleArray, Path.Direction.CW);
            }

            // 闪电符号
            if (mBitmap != null) {
                int bitmapWidth = mBitmap.getWidth();
                int bitmapHeight = mBitmap.getHeight();

                // 闪电的宽度大于进度条的宽度才去显示，否则会导致图标挤到一块，显示异常
                if (bitmapWidth < mProgress) {
                    // src:bitmap的区域，dst:本次绘制的区域，把src放进dst中
                    mRectDsc.left = ((mProgress - bitmapWidth) / 2);// left：( mProgress - bitmap的宽 )/2
                    // top = (进度条高度 - bitmap高度 )/2 + 顶部高度
                    mRectDsc.top = (int) ((mProgressHeight - bitmapHeight) / 2 + mTopInterval);
                    mRectDsc.right = mRectDsc.left + bitmapWidth;
                    mRectDsc.bottom = mRectDsc.top + bitmapHeight;

                    mRectSrc.left = 0;
                    mRectSrc.top = 0;
                    mRectSrc.right = bitmapWidth;
                    mRectSrc.bottom = bitmapHeight;
                }
            }
        }

        // 当前电量的进度值
        String multiply = NumberUtil.multiply(String.valueOf(mPercentage), String.valueOf(100));
        // log("当前的进度为：" + mPercentage + " 计算后的值：" + multiply);
        mCurrentChargingText = multiply + "%";
        mCurrentChargingTextSize = CustomViewUtil.getTextSize(mPaintCharging, mCurrentChargingText);

        // 当前剩余的充电时间
        if (!TextUtils.isEmpty(mRemainingTimeText)) {
            mRemainingTimeTextSize = CustomViewUtil.getTextSize(mPaintChargingRemainingTimeText, mRemainingTimeText);
            float remainingTimeTextInterval = mRemainingTimeTextSize[1] + mRemainingTimeTextInterval;
            if (mTopInterval < remainingTimeTextInterval) {
                mTopInterval = remainingTimeTextInterval;
            }
        }

        // 区间的滑块

        // 右侧的默认值
        if (mRight <= 0) {
            mRight = mProgressWidth * mPercentageStart;
        } else {
            mRight = mProgressWidth * mBottomScrollProgress; // 右侧的精确值
        }

        // 进度条小于 开始的默认值
        if (mProgress >= mRight) {
            mLeft = mRight;
        } else if (mProgress < mRight) { // 进度小于默认的开始值，则left = 当前进度
            mLeft = mProgress;
        }

        // log("left:" + mLeft + " right:" + mRight + " mProgress:" + mProgress);

        mRectFSection.left = mLeft;
        mRectFSection.top = mTopInterval;
        mRectFSection.right = mRight;
        mRectFSection.bottom = mTopInterval + mProgressHeight; // 上方的距离高度 + 进度条的高度

        // 滑动的进度
        if (mBottomScrollProgress > 0) {
            mBottomScrollProgressValue = mBottomScrollProgress * mProgressWidth;
            String multiply1 = NumberUtil.multiply(mBottomScrollProgress + "", 100 + "");
            mSocCurrentText = multiply1 + "%";

            float[] textSize = CustomViewUtil.getTextSize(mPaintScrollValue, mSocCurrentText);
            if (textSize != null) {
                mScrollTextWidth = textSize[0];
                mScrollTextHeight = textSize[1];
            }
        }

        // 最佳电量直
        if (mShowOptimum) {
            if (mPercentageOptimum > 0) {
                mOptimumPosition = mProgressWidth * mPercentageOptimum;
                // 最佳的数据值
                String formatZj = NumberUtil.multiply(String.valueOf(mPercentageOptimum), String.valueOf(100));
                OptimumContent = formatZj + "%最佳";
                mOptimumTextSize = CustomViewUtil.getTextSize(mPaintOptimum, OptimumContent);
            }
        }

        // 最佳值的高度间隔
        if ((mOptimumTextSize != null) && (mOptimumTextSize[1] > 0)) {
            // 最佳电量直的高度 = 文字本身高度 + 间距
            float optimumTextHeight = mOptimumTextSize[1] + mOptimumTextInterval;
            if (mTopInterval < optimumTextHeight) {
                mTopInterval = optimumTextHeight;
            }
        }

        // 比较当前的间距和总间距的大小
        float chargingInterval = mCurrentChargingTextSize[1] + mCurrentChargingTextInterval;
        if (mTopInterval < chargingInterval) {
            mTopInterval = chargingInterval;
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
        // if (!TextUtils.isEmpty(mSocText)) {
        // mSocTextSize = CustomViewUtil.getTextSize(mPaintSoc, mSocText);
        // if (mSocTextSize != null) {
        // float socTextInterval = mSocTextSize[1] + mSocTextTopInterval;
        // if (mBottomInterval < socTextInterval) {
        // mBottomInterval = socTextInterval;
        // }
        // }
        // }

        // 叠加当前的高度
        mMaxHeight += mTopInterval;
        mMaxHeight += mBottomInterval + mPaddingBottom;

        // 重新测量
        widthMeasureSpec = resolveSize(mMaxWidth, widthMeasureSpec);
        heightMeasureSpec = resolveSize(mMaxHeight, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制底层进度条
        canvas.drawRoundRect(mRectFBackground, mAngle, mAngle, mPaintBackground);

        // 绘制右侧矩形
        // canvas.drawRect(mRectFRight, mPaintRight);

        // log("mPercentage:" + mPercentage);
        // 绘制当前进度的进度条
        if (mPercentage >= 1) {
            // 绘制外层圆角矩形
            canvas.drawRoundRect(mRectFOuterLayer, mAngle, mAngle, mPaintRoundOuterLayer);
            // 绘制圆角矩形
            canvas.drawRoundRect(mRectFNerLayer, mAngle, mAngle, mPaintRoundNerLayer);

            // 绘制右侧矩形 ---> 蓝色
            mPaintRight.setColor(Color.parseColor("#FF09B6F7"));
        } else {
            if (mPercentage > 0) {
                // 使用路径绘制外层圆角矩形
                canvas.drawPath(mPath_w, mPaintRoundOuterLayer);
                // 使用路径绘制内层圆角矩形
                canvas.drawPath(mPath_n, mPaintRoundNerLayer);

                // 绘制右侧矩形 ---> 灰色
                mPaintRight.setColor(Color.parseColor("#FFF4F4F4"));
            }
        }

        // 绘制闪电图标
        // if (isCharging) {
        // if (mBitmap != null) {
        // canvas.drawBitmap(mBitmap, mRectSrc, mRectDsc, mPaintRoundNerLayer);
        // }
        // }

        // 绘制当前电量的进度
        if (mPercentage >= 0) {
            // 重新计算当前的电量
            String multiply = NumberUtil.multiply(String.valueOf(mPercentage), String.valueOf(100));
            mCurrentChargingText = multiply + "%";
            mCurrentChargingTextSize = CustomViewUtil.getTextSize(mPaintCharging, mCurrentChargingText);

            if (mCurrentChargingTextSize != null) {

                // float dx = (mProgressWidth - mCurrentChargingTextSize[0]) / 2; // dx = (进度条宽度
                // - 文字宽度)/2
                // float dy = mMaxHeight - mProgressHeight - mCurrentChargingTextInterval -
                // mBottomInterval; // dy = 总高度
                // - 进度条的高度 - 间距 - 底部间距

                // x轴 = (进度条的宽度 - 文字的宽度) / 2
                float dx = ((mProgress - mCurrentChargingTextSize[0]) / 2);
                if (dx < 0) {
                    dx = 0;
                }

                float baseLine = CustomViewUtil.getBaseLine(mPaintCharging, mCurrentChargingText);
                float dy = (int) (mTopInterval + ((mProgressHeight - mCurrentChargingTextSize[1]) / 2) + baseLine);

                canvas.drawText(mCurrentChargingText, 0, mCurrentChargingText.length(), dx, dy, mPaintCharging);
            }
        }

        // 绘制剩余的充电时间
        if (!TextUtils.isEmpty(mRemainingTimeText) && mRemainingTimeTextSize != null) {
            float dx = 0;
            float dy = mMaxHeight - mProgressHeight - mRemainingTimeTextInterval - mBottomInterval; // dy = 总高度 - 进度条 - 间距 -
            // 底部间距
            canvas.drawText(mRemainingTimeText, 0, mRemainingTimeText.length(), dx, dy, mPaintChargingRemainingTimeText);
        }

        // 区间
        float mQjRight = mBottomScrollProgress * mProgressWidth; // 区间右侧的值
        if (mQjRight < mProgressWidth) {
            if (mPercentage > 0) {
                // 如果有正常的进度，则去绘制矩形
                canvas.drawRect(mRectFSection, mPaintSection);
            } else {
                // 电量的进度为空，去绘制路径 = 从头开始，到开始位置
                mPath_qj.reset();
                mPath_qj.addRoundRect(mProgress, mTopInterval, mQjRight, mTopInterval + mProgressHeight, mAngleArray, Path.Direction.CW);

                canvas.drawPath(mPath_qj, mPaintSection);
            }

            // 绘制右侧 ---> 显示灰色
            if (mPercentage < 1) {
                mPaintRight.setColor(Color.parseColor("#FFF4F4F4"));
            }
        } else {
            // 进度大于0
            mPath_qj.reset();
            if (mPercentage > 0) {
                mPath_qj.addRoundRect(mProgress, mTopInterval, mProgressWidth, mTopInterval + mProgressHeight, mAngleArrayRight, Path.Direction.CW);
            } else {
                mPath_qj.addRoundRect(mProgress, mTopInterval, mProgressWidth, mTopInterval + mProgressHeight, mAngleArrayLeftRight, Path.Direction.CW);
            }

            // 使用路径绘制内层圆角矩形
            canvas.drawPath(mPath_qj, mPaintSection);

            if (mPercentage < 1) {
                // 绘制右侧矩形 ---> 显示黄色
                mPaintRight.setColor(Color.parseColor("#2BFF9C26"));
            }
        }

        // 绘制右侧方块
        canvas.drawRect(mRectFRight, mPaintRight);

        // 绘制滑动的区间值

        if (mBottomScrollProgressValue > 0) {
            // 滑动的进度值
            float scrollValue = mBottomScrollProgressValue;

            // 改版 区间的线，永远显示
            // if (mProgress < scrollValue) {
            // }
            // 绘制线
            canvas.drawLine(scrollValue, mTopInterval, scrollValue, mProgressHeight + mTopInterval, mPaintBottomScrollLine);

            // 绘制阴影
            float circleX, circleY;
            // 参数1：圆中心的X轴位置 = 当前进度的值
            circleX = scrollValue;
            // 参数2：圆中心的Y轴位置 = 顶部间距 + 进度条高度 + 半径的高度
            circleY = mTopInterval + mProgressHeight + mRemainingTimeTextInterval + 10;
            // 参数1：圆中心的X轴位置
            // 参数2：圆中心的Y轴位置
            // 参数3：圆的半径
            canvas.drawCircle(circleX, circleY, mRemainingTimeTextInterval, mPaintScrollRound);
            // log("绘制了圆形：！");

            // 绘制SOC进度 改版---> 进度值改到左侧
            float dx = circleX - mScrollTextWidth - (mRemainingTimeTextInterval * 2) - mScrollTextInterval; // dx = 圆角的dx轴 -
            // 文字宽度 - 直径 - 距离
            float dy = (circleY + mScrollTextHeight / 2); // dy = 圆角的y轴 + 文字的高度 /2 +
            canvas.drawText(mSocCurrentText, 0, mSocCurrentText.length(), dx, dy, mPaintScrollValue);
        } else {
            mWriteUtil.write("绘制滑动的区间值：小于0 ，不执行逻辑！");
        }

        // 绘制最佳的进度
        if (mShowOptimum && (mPercentageOptimum > 0)) {
            // 改版：最佳进度值永远显示
            // if (mProgress < mOptimumPosition) {
            // }
            // canvas.drawLine(mOptimumPosition, mTopInterval, mOptimumPosition, mMaxHeight
            // - mBottomInterval, mPaintOptimum);
            canvas.drawLine(mOptimumPosition, mTopInterval, mOptimumPosition, mProgressHeight + mTopInterval, mPaintOptimum);

            // 绘制最佳值文字
            float dx = (mOptimumPosition - (mOptimumTextSize[0] / 2)); // 最佳值的x轴 - (文字的高度 /2)
            float dy = mMaxHeight - mOptimumTextInterval - mProgressHeight - mBottomInterval;// 总的高度 - 最佳值的间距 - 进度条的高度 - 底部高度
            canvas.drawText(OptimumContent, 0, OptimumContent.length(), dx, dy, mPaintOptimum);
        }

        // 绘制SOC
        // if (mBitmapSoc != null) {
        // // 绘制图标
        // float dy = mTopInterval + mProgressHeight + mSocBitmapTopInterval; // 上方高度 +
        // 进度条高度 + 间距
        // canvas.drawBitmap(mBitmapSoc, 0, dy, mPaintSoc);
        //
        // // 绘制SOC文字
        // if (!TextUtils.isEmpty(mSocText)) {
        // float dx = mBitmapSoc.getWidth() + mSocLeftInterval; // dx = bitmap宽度 + 间距
        // float baseLine = CustomViewUtil.getBaseLine(mPaintSoc, mSocText);
        //
        // // 因为此处要计算基准线，特别麻烦。所以换成意外一种方式去计算 = dy + 图片高度 - 文字高度 + 基准线
        // float dy2 = dy + mBitmapSoc.getHeight() - mSocTextSize[1] + baseLine;
        // canvas.drawText(mSocText, dx, dy2, mPaintSoc);
        // }
        // }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroll) {
                    return false;
                }
                log("ACTION_DOWN ---> dispatchTouchEvent --->");
                // 如果是在区域内，则返回为true,否则返回false
                float currentX = event.getX(); // 当前的X轴的值
                boolean b = (currentX > mStartBorder) && (currentX < mEndBorder);
                if (b) {
                    // 改变画笔的颜色并刷新
                    mPaintScrollRound.setColor(Color.parseColor("#FFFF9C26"));

                    // 在点击的时候，也进行数据的计算
                    calculate(event.getX());
                }
                return b;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();

                if (x < mStartBorder || x > mEndBorder) {
                    log("ACTION_MOVE ---> dispatchTouchEvent ---> 自己消耗！");
                    return true;
                } else {
                    log("ACTION_MOVE ---> dispatchTouchEvent ---> 交给父类去处理！");
                    return super.dispatchTouchEvent(event);
                }

            case MotionEvent.ACTION_UP:
                mPaintScrollRound.setColor(Color.parseColor("#FFF4F4F4"));
                requestLayout();
                invalidate();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 开始和结束的边界
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log("ACTION_DOWN");
                return mScroll;

            case MotionEvent.ACTION_MOVE:
                log("ACTION_DOWN ----> onTouchEvent");
                // 移动的时候，改变滑动的位置，反推出当前的进度值
                float x = event.getX();
                calculate(x);
                break;
            case MotionEvent.ACTION_UP: // 手指抬起的操作
                if (mProgressListener != null) {
                    mProgressListener.onTouchUp(mMultiply);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void calculate(float x) {
        // 获取两位的小数，作为进度值使用
        String format1 = NumberUtil.dataFormatDigits((x / mProgressWidth) + "", BigDecimal.ROUND_HALF_UP, 2);
        // 换算成整数，用来取余数
        float v1 = Float.parseFloat(format1);
        int v2 = (int) (v1 * 100);
        mDivideAndRemainder = NumberUtil.divideAndRemainder(v2, 5);

        LogUtil.e("format: " + format1 + "  v2:" + v2 + "   mDivideAndRemainder:" + mDivideAndRemainder);
        /**
         * 改版 ：被5整除才可以
         */
        if (TextUtils.equals(mDivideAndRemainder, "0")) {

            // 余数为0的时候去执行
            mBottomScrollProgress = v1;
            // 重新获取竖线的left值
            mBottomScrollProgressValue = mBottomScrollProgress * mProgressWidth;
            // 文字的滑动数值
            mMultiply = NumberUtil.multiply(mBottomScrollProgress + "", 100 + "");
            // 文字的值
            mSocCurrentText = mMultiply + "%";

            // 区间的结束值 = 滑动的百分比值 * 进度条的总宽度
            if (mProgress < mProgressWidth * mBottomScrollProgress) {
                mRectFSection.right = mProgressWidth * mBottomScrollProgress;
            } else {
                mRectFSection.right = mProgress;
            }

            // 重新绘制
            requestLayout();
            invalidate();

            if (mProgressListener != null) {
                mProgressListener.onMove(mMultiply);
            }
        }
    }

    /**
     * 设置当前的电量百分比
     *
     * @param chargingPercentage 当前电量进度的百分比
     */
    public void setPercentage(float chargingPercentage) {
        mPercentage = chargingPercentage;
        log("接收到的电量：" + mPercentage);
        requestLayout();
    }

    /**
     * 设置剩余的充电时间
     *
     * @param remainingChargeTime 剩余充电时间
     */
    public void setRemainingChargeTime(String remainingChargeTime) {
        mRemainingTimeText = remainingChargeTime;
        requestLayout();
        invalidate();
    }

    /**
     * 设置最佳的soc值
     *
     * @param optimumPercentage 最佳的SOC值
     */
    public void setOptimumValue(float optimumPercentage) {
        mPercentageOptimum = optimumPercentage;
        requestLayout();
        invalidate();
    }

    /**
     * 设置滑动区间值的百分比
     *
     * @param startPercentage 开始的区间值
     * @param endPercentage   结束的区间值
     */
    public void setInterval(float startPercentage, float endPercentage) {
        mPercentageStart = startPercentage;
        mPercentageEnd = endPercentage;
        requestLayout();
        invalidate();
    }

    /**
     * 设置是否在充电中
     *
     * @param isCharging true:充电，false:停止充电
     */
    public void setCharging(boolean isCharging) {
        this.isCharging = isCharging;
        requestLayout();
        invalidate();
    }

    /**
     * view 是否可以操作
     *
     * @param scroll true:可以操作，false:不可以操作 ,默认可以操作
     */
    public void setScroll(boolean scroll) {
        mScroll = scroll;
    }

    /**
     * 设置当前的SOC值
     *
     * @param socValue 当前的soc值
     */
    public void setCurrentSoc(@FloatRange(from = 0.6f, to = 1.0f) float socValue) {
        if (socValue >= 0.6) {
            this.mBottomScrollProgress = socValue;
            mWriteUtil.write("充电中心--->接收到正常的SOC值！");
        } else {
            this.mBottomScrollProgress = 0.6f;
            mWriteUtil.write("充电中心--->接收到异常的SOC值: " + socValue + "，默认设置成0.6");
        }
        requestLayout();
        invalidate();
    }

    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public interface ProgressListener {
        /**
         * @param progress 手指抬起时候，当前的百分比
         */
        void onTouchUp(String progress);

        void onMove(String progress);
    }

}
