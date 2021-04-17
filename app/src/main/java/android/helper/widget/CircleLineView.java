package android.helper.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.helper.R;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.LogUtil;

public class CircleLineView extends View {

    private Context mContext;
    private Paint mPaintRect = new Paint();
    private Paint mPaintText = new Paint();
    private Paint mPaintAnimation = new Paint();
    private String mContent = "长按录制";
    private int mTextX;
    private int mTextY;

    @SuppressLint("DrawAllocation")
    private RectF rect;
    private float radius;
    private float baseLine;
    private float mContentHeight;
    private float mAnimationEndX;
    private int measuredHeight;
    private int measuredWidth;
    private int[] mColors = new int[]{Color.CYAN, Color.RED};

    private float[] mPosition = new float[]{0f, 1f};
    private ValueAnimator animator;
    private float dp_16;
    private Path ccwPath;

    public CircleLineView(Context context) {
        super(context);
        initView(context, null);
    }

    public CircleLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        mPaintRect.setColor(Color.BLUE);
        mPaintRect.setStrokeCap(Paint.Cap.ROUND);

        mPaintText.setTextSize(ConvertUtil.toSp(16));
        dp_16 = ConvertUtil.toDp(16);
        if (radius <= 0) {
            radius = ConvertUtil.toDp(24.5f);
        }

        getEndX();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (measuredHeight <= 0) {
            measuredHeight = getMeasuredHeight();
        }

        if (measuredWidth <= 0) {
            measuredWidth = getMeasuredWidth();
        }

        // 绘制背景
        canvas.drawRoundRect(0, 0, measuredWidth, measuredHeight, radius, radius, mPaintRect);

        LinearGradient gradient = new LinearGradient(0, measuredHeight, mAnimationEndX, measuredHeight, mColors, mPosition, Shader.TileMode.CLAMP);
        mPaintAnimation.setShader(gradient);

        RectF rect = new RectF(0, 0, mAnimationEndX, measuredHeight);
        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
        ccwPath = new Path();
        ccwPath.addRoundRect(rect, radii, Path.Direction.CCW);
        canvas.drawPath(ccwPath, mPaintAnimation);

        // 绘制渐变动画

//        canvas.drawRoundRect(dp_16, 0, mAnimationEndX + dp_16, measuredHeight, radius, radius, mPaintAnimation);
//        canvas.drawRect(radius,0,mAnimationEndX + radius, measuredHeight,mPaintAnimation);

        if (mTextY <= 0) {
            if (baseLine <= 0) {
                baseLine = CustomViewUtil.getBaseLine(mPaintText, mContent);
            }
            int mMeasuredHeight = getMeasuredHeight();
            float v = (mMeasuredHeight - mContentHeight) / 2;
            mTextY = (int) (v + baseLine);
        }
        // 绘制文字
        canvas.drawText(mContent, mTextX, mTextY, mPaintText);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        switchTextStatus(false);
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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        int height = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switchTextStatus(true);
                if (animator != null) {
                    animator.start();
                }

                // 计算移动的结束X轴坐标
                LogUtil.e("ACTION_DOWN");

                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if (animator != null) {
                    animator.cancel();
                }
                switchTextStatus(false);
                LogUtil.e("ACTION_UP");
                mAnimationEndX = 0;
                ccwPath.reset();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void getEndX() {
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5 * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();

                // 每一份值占总体宽度的多少
                if (animatedValue > 0) {
                    mAnimationEndX = measuredWidth * animatedValue;
                    LogUtil.e("animatedValue : " + animatedValue + "  measuredWidth:" + measuredWidth + "   mAnimationEndX:" + mAnimationEndX);
                    invalidate();
                }
            }
        });
    }

}
