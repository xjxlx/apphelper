package android.helper.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.utils.LogUtil;

/**
 * 自定义放射动画
 */
public class RadialGradientView extends View {

    private int COLOR_GREEN = 0xff153925;
    private int COLOR_BLUER = 0xFF35D2FF;
    private Paint mPaint = new Paint();
    private int mWidth;
    private int mHeight;
    private float progress;// 设置自定义view，让他不停的做动画效果
    private int mLeftMultiple;// 边距的角标
    private int mTopMultiple;
    private int mRightMultiple;
    private int mBottomMultiple;
    private int[] mIntArray; // 边距的数组
    private float mLeft, mRight, mTop, mBottom;

    // 环形渐变渲染
    Shader mRadialGradient = null;

    public RadialGradientView(Context context) {
        super(context);
        initView(context, null);
    }

    public RadialGradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_GREEN); // 默认使用绿色的背景
        mPaint.setDither(true); // 防止抖动
        mPaint.setAntiAlias(true);// 抗锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 自动根据模式去测量需要的值
        // resolveSize： 根据传入的size  和 测量模式，去精准的测量view的宽高
        // resolveSize(int size, int measureSpec)
        // size:一般用MeasureSpec.getSize() 去获取默认的size

        int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        int height = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);

        // 这里必须设置，如果不设置，就会 导致默认的宽高设置成最大的值
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.save();

        // 设置默认的边距，这个地方是为了适配车辆不一样党课大小所特意设置的
        mLeftMultiple = 10;
        mTopMultiple = 2;
        mRightMultiple = 12;
        mBottomMultiple = 30;

        mLeft = mWidth / mLeftMultiple * progress;
        mTop = mHeight / mTopMultiple * (progress);
        mRight = mWidth - (mWidth / mRightMultiple) * (progress);
        mBottom = mHeight - (mHeight / mBottomMultiple) * (progress);

        // 设置边缘模糊
        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(85f, BlurMaskFilter.Blur.NORMAL);
        mPaint.setMaskFilter(blurMaskFilter);

        LogUtil.e("progress:" + progress);
        RectF rect = new RectF(mLeft, mTop, mRight, mBottom);
        canvas.drawOval(rect, mPaint);

//        canvas.restore();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void startAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 0.8f, 1f);
        animator.setDuration(2000);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

}
