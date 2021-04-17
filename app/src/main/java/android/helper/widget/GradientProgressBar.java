package android.helper.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.helper.R;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.ValueAnimationUtil;

public class GradientProgressBar extends ProgressBar {

    private final Paint mPaintText = new Paint();
    private Context mContext;
    private int mTextX;
    private int mTextY;
    private String mContent = "长按录制";
    private ValueAnimator animator;
    private float mAnimationEndX;
    private float mContentHeight;
    private float baseLine;
    private int measuredWidth;
    private int percentage;
    private boolean isTool;// 是否开始计算进度的开关
    private ProgressTouchListener mListener;
    private ValueAnimationUtil utils;
    private boolean isCanTouch = true;//是否可以按下

    public GradientProgressBar(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public GradientProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(ConvertUtil.toSp(16));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制文字
        if (mTextY <= 0) {
            if (baseLine <= 0) {
                baseLine = CustomViewUtil.getBaseLine(mPaintText, mContent);
            }
            int mMeasuredHeight = getMeasuredHeight();
            float v = (mMeasuredHeight - mContentHeight) / 2;
            mTextY = (int) (v + baseLine);
        }
        canvas.drawText(mContent, mTextX, mTextY, mPaintText);

        // 修改进度
        setProgress(percentage);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measuredWidth = getMeasuredWidth();
        switchTextStatus(false);
        getEndX();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean canTouch = isCanTouch();
        if (!canTouch) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 计算移动的结束X轴坐标
                LogUtil.e("ACTION_DOWN");

                // 点击的反馈，因为这里被拦截了，所以，无法传递到click的事件中去，所以这里要做一下控制
                if (mListener != null) {
                    mListener.onDown();
                }

                // 先计算倒计时的事件，倒计时的时间计算完成了之后，就开始计算进度条的动画时间
                utils = new ValueAnimationUtil();
                utils.setAnimationIntFlashBack(3, 3, 0).setValueListener(new ValueAnimationUtil.ValueAnimationListener() {
                    @Override
                    public void onValueChange(Object value) {
                        if (mListener != null) {
                            mListener.onValueChange(value);
                        }
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFinish() {
                        if (mListener != null) {
                            mListener.onCountdownFinish();
                        }

                        // 动画结束后，在去执行另外一组动画
                        if (animator != null) {
                            switchTextStatus(true);
                            animator.start();
                            invalidate();
                        }

                    }
                }).start();

                return true;
            case MotionEvent.ACTION_UP:
                if (mListener != null) {
                    mListener.onUp();
                }
                if (animator != null) {
                    animator.cancel();
                }

                if (utils != null) {
                    utils.cancel();
                }

                switchTextStatus(false);
                LogUtil.e("ACTION_UP");
                mAnimationEndX = 0;
                percentage = 0;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void switchTextStatus(boolean touch) {
        int measuredWidth = getMeasuredWidth();
        if (touch) {
            mContent = "录制中";
            mPaintText.setColor(ContextCompat.getColor(mContext, R.color.white_16));
            float[] textSize = CustomViewUtil.getTextSize(mPaintText, mContent);
            float v = textSize[0];
            mTextX = (int) ((measuredWidth - v)) / 2;
            mContentHeight = textSize[1];
        } else {
            mContent = "长按录制";
            mPaintText.setColor(ContextCompat.getColor(mContext, R.color.white));
            float[] textSize = CustomViewUtil.getTextSize(mPaintText, mContent);
            float v = textSize[0];
            mTextX = (int) ((measuredWidth - v)) / 2;
            mContentHeight = textSize[1];
        }
        invalidate();
    }

    private void getEndX() {
        animator = ValueAnimator.ofFloat(0, 100);
        animator.setDuration(5 * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            percentage = (int) animatedValue;

            if (percentage >= 100) {
                if (mListener != null) {
                    mListener.onFinish();
                }
            }

            // 每一份值占总体宽度的多少
            if (animatedValue > 0) {
                mAnimationEndX = measuredWidth * animatedValue;
                //   LogUtil.e("animatedValue : " + animatedValue + "  measuredWidth:" + measuredWidth + "   mAnimationEndX:" + mAnimationEndX);
                invalidate();
            }
        });
    }

    public boolean isCanTouch() {
        return isCanTouch;
    }

    public void setCanTouch(boolean canTouch) {
        isCanTouch = canTouch;
    }

    public void setProgressTouchListener(ProgressTouchListener listener) {
        mListener = listener;
    }

    public interface ProgressTouchListener {
        void onDown();

        void onUp();

        void onValueChange(Object value);

        void onCountdownFinish();

        void onFinish();
    }

}
