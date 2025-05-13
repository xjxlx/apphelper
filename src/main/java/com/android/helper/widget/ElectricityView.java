package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import com.android.common.base.BaseView;
import com.android.common.utils.ConvertUtil;
import com.android.common.utils.LogUtil;
import com.android.helper.utils.CustomViewUtil;
import java.math.BigDecimal;

/**
 * @author : 流星 @CreateDate: 2022/1/12-5:47 下午 @Description: 电流的view
 */
public class ElectricityView extends BaseView {

  private final float mProgressHeight = ConvertUtil.dp(getContext(), 8); // 进度条的高度
  private final float mProgressRound = ConvertUtil.dp(getContext(), 5);
  // 进度条
  private final float mBottomValueInterval = ConvertUtil.dp(getContext(), 8);
  // 中间圆
  private final float mCircleRadius = ConvertUtil.dp(getContext(), 12); // 圆的半径
  private final float mPaddingLeft = ConvertUtil.dp(getContext(), 20); // view的左间距 = 圆的半径
  private final float mPaddingTop = ConvertUtil.dp(getContext(), 8); // view的上方padding值，避免遮挡阴影
  private final float mRightInterval = ConvertUtil.dp(getContext(), 4) + mPaddingLeft;
  private float mProgressWidth; // 进度条的宽度
  private Paint mPaintProgressBackground;
  private Paint mPaintProgress;
  // 进度条的范围值
  private int mProgressStart = 5; // 最小的进度值
  private int mProgressEnd = 62; // 最大的进度值
  private int mProgressTarget = 0; // 目标的进度值
  private Paint mPaintRound;
  private Paint mPaintBottomRoundText;
  private float mCurrentProgress;
  private float mTopInterval; // 进度条距离顶部的高度
  private String mBottomTextValue = ""; // 圆球底部的文字
  private float mPercentage; // 进度条的百分比
  private float mBaseLineBottomText;
  // 右侧的电流值
  private Paint mPaintRightText;
  private String mRightTextValue = "";
  private float mBaseLineRightText; // 右侧文字的高度

  private ProgressListener mProgressListener;
  private boolean mScroll = true; // 是否可以默认拖动这个view
  private float mLeftBorder; // 左侧滑动的边界
  private float mRightBorder; // 右侧滑动的边界

  public ElectricityView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView(context, attrs);
  }

  @Override
  public void initView(Context context, AttributeSet attrs) {
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
    mPaintRightText.setTextSize(ConvertUtil.sp(getContext(), 13));
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
    mPaintBottomRoundText.setTextSize(ConvertUtil.sp(getContext(), 12));
  }

  @SuppressLint("DrawAllocation")
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    // 获取view的宽度
    float maxWidth = MeasureSpec.getSize(widthMeasureSpec);
    // 右侧的电流值
    String format = dataFormat(mProgressEnd + "");
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
    mPercentage = (mProgressEnd - mProgressStart) / mProgressWidth;
    LogUtil.e("默认进度条的百分比：" + mPercentage);
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
    float[] textSizeBottomRoundText =
        CustomViewUtil.getTextSize(mPaintBottomRoundText, mBottomTextValue);
    mBaseLineBottomText = CustomViewUtil.getBaseLine(mPaintBottomRoundText, mBottomTextValue);
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
    if (mProgressTarget > 0) {
      mCurrentProgress = mProgressTarget / mPercentage;
    }
    // 绘制背景
    canvas.drawRoundRect(
        mCurrentProgress + mPaddingLeft,
        mTopInterval + mPaddingTop,
        mProgressWidth + mPaddingLeft,
        mProgressHeight + mTopInterval + mPaddingTop,
        mProgressRound,
        mProgressRound,
        mPaintProgressBackground);
    // 绘制进度
    canvas.drawRoundRect(
        mPaddingLeft,
        mTopInterval + mPaddingTop,
        mCurrentProgress + mPaddingLeft,
        mProgressHeight + mTopInterval + mPaddingTop,
        mProgressRound,
        mProgressRound,
        mPaintProgress);
    // 绘制右侧的电流值
    canvas.drawText(
        mRightTextValue,
        0,
        mRightTextValue.length(),
        (mPaddingLeft + mProgressWidth + mRightInterval),
        (mBaseLineRightText + mTopInterval + mPaddingTop),
        mPaintRightText);
    // 圆心的x轴 = 进度的值 + 左侧的间距
    // 圆的X轴圆心
    float circleDx = mCurrentProgress + mPaddingLeft;
    // 圆心的y轴 = 进度条高度 /2 + 进度条top 的值
    // 圆的Y轴圆心
    float circleDY = mProgressHeight / 2 + mTopInterval + mPaddingTop;
    canvas.drawCircle(circleDx, circleDY, mCircleRadius, mPaintRound);
    // 绘制圆球下的文字
    int bottomTextValue = 0;
    if (mProgressTarget + mProgressStart > mProgressEnd) {
      bottomTextValue = mProgressEnd;
    } else {
      bottomTextValue = mProgressTarget + mProgressStart;
    }
    String format = dataFormat(bottomTextValue + "");
    // 从新计算文字的宽度
    float width = CustomViewUtil.getTextSize(mPaintBottomRoundText, mBottomTextValue)[0];
    mBottomTextValue = format + "A";
    LogUtil.e("⭐️⭐️⭐️ mBottomTextValue：" + mBottomTextValue);
    float dx = (circleDx - width / 2); // 圆球的X轴圆心 - 文字的宽度/2
    float dy =
        (circleDY
            + mCircleRadius
            + mBottomValueInterval
            + mBaseLineBottomText); // dy = 圆球的底部 + 间距 +baseLine
    canvas.drawText(mBottomTextValue, 0, mBottomTextValue.length(), dx, dy, mPaintBottomRoundText);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    // 如果设置了不可以拖动这个view，则把他给动态的禁用掉
    if (!mScroll) {
      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        LogUtil.e("dispatchTouchEvent ---> 父类消耗掉这个事件，禁止往下面去传递");
        return true;
      }
    }
    // 限制范围区域
    mLeftBorder = mPaddingLeft;
    // 右侧的范围 = 限定值 / 百分比
    mRightBorder = mProgressEnd / mPercentage;
    // 如果小于这个区域，或者大于这个区域，则自己消耗掉这个事件，不继续往下面传递
    float rawX = event.getRawX();
    float x = event.getX();
    LogUtil.e("rawX:" + rawX + " x1:" + x);
    int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
      LogUtil.e("⭐️⭐️⭐️ dispatchTouchEvent ---> ACTION_MOVE");
      LogUtil.e(
          "left:"
              + mLeftBorder
              + "  right:"
              + mRightBorder
              + "  x: "
              + x
              + "   tra:"
              + mProgressTarget);
      if (x < mLeftBorder || x > (mRightBorder + mLeftBorder)) {
        LogUtil.e("停止执行！");
        return true;
      }
    }
    return super.dispatchTouchEvent(event);
  }

  private void calculate(int x) {
    // 当前滑动的位置 = 当前的x轴位置 * 每个像素所占用的百分比
    int v = (int) (x - mPaddingLeft);
    // 在滑动的时候，有时候会多几个像素，可能会导致数据变大，超出范围值，
    mProgressTarget = (int) (v * mPercentage);
    if (mProgressTarget + mProgressStart > mProgressEnd) {
      mProgressTarget = mProgressEnd - mProgressStart;
    }
    // 重新绘制
    invalidate();
    if (mProgressListener != null) {
      int bottomTextValue = 0;
      if (mProgressTarget + mProgressStart > mProgressEnd) {
        bottomTextValue = mProgressEnd;
      } else {
        bottomTextValue = mProgressTarget + mProgressStart;
      }
      String format = dataFormat(bottomTextValue + "");
      mProgressListener.onMove(format + "A");
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        LogUtil.e("⭐️⭐️⭐️ onTouchEvent ---> ACTION_DOWN ");
        calculate((int) event.getX());
        return mScroll;
      case MotionEvent.ACTION_MOVE:
        LogUtil.e("⭐️⭐️⭐️ onTouchEvent ---> MOVE");
        // 获取当前的x轴位置
        calculate((int) event.getX());
        break;
      case MotionEvent.ACTION_UP: // 抬起
        if (mProgressListener != null) {
          mProgressListener.onTouchUp(mProgressTarget + mProgressStart);
        }
        break;
    }
    return super.onTouchEvent(event);
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
   * 设置最小的进度值
   *
   * @param minValue 最小的电流值
   */
  public void setMinIntervalScope(int minValue) {
    mProgressStart = minValue;
    invalidate();
  }

  /**
   * 设置电流的当前值
   *
   * @param currentValue 电流的当前值
   */
  public void setCurrentValue(@IntRange(from = 0, to = 62) int currentValue) {
    // 中间值的区间
    int interval = mProgressEnd - mProgressStart;
    // 限定范围
    if (currentValue > interval) {
      mProgressTarget = currentValue - mProgressStart;
    } else {
      mProgressTarget = currentValue;
    }
    invalidate();
  }

  /** 设置进度的监听 */
  public void setProgressListener(ProgressListener progressListener) {
    mProgressListener = progressListener;
  }

  /**
   * 是否可以默认的拖动这个view
   *
   * @param scroll true:可以，false:不可以， 默认可以拖动
   */
  public void setScroll(boolean scroll) {
    this.mScroll = scroll;
  }

  private String dataFormat(String value) {
    String result = "";
    if (!TextUtils.isEmpty(value)) {
      // 转换为大整形运算
      BigDecimal decimal = new BigDecimal(value);
      result = decimal.stripTrailingZeros().toPlainString();
    }
    return result;
  }

  public interface ProgressListener {
    /** 手指抬起的进度 */
    void onTouchUp(int progress);

    void onMove(String progress);
  }
}
