package com.android.helper.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.LogUtil;

/**
 * 圆形进度条的view
 * 1：宽度可以指定
 * 2：颜色可以指定
 * 3：总的时间可以指定
 */
public class SendProgressView extends View {

    private int height;
    private int width;
    private RectF rectF;
    private Paint paint1;
    private Paint paint2;
    private Paint paint3;
    private DisplayMetrics displayMetrics;
    private Drawable drawable;
    private float strokeWidthValue;
    private float progress = 0f;
    private int average; // 计算出平均值
    private int time;
    private Bitmap bitmap;
    private float drawableWidthValue;
    private float drawableHeightValue;
    private ValueAnimator anim;

    public SendProgressView(Context context) {
        super(context);
        initView(context, null);
    }

    public SendProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    @SuppressLint("ResourceType")
    private void initView(Context context, @Nullable AttributeSet attrs) {
        // 获取属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SendProgressView);
        // 一共几个view
        // 使用四舍五入的方式，获取需要设置的view
        drawable = array.getDrawable(R.styleable.SendProgressView_drawable);
        int strokeWidth = array.getInteger(R.styleable.SendProgressView_progress_stroke_width, 3);
        int colorInner = array.getColor(R.styleable.SendProgressView_inner_layer_color, Color.WHITE);
        int colorOuter = array.getColor(R.styleable.SendProgressView_outer_layer_color, Color.WHITE);
        // 内层的透明度
        float alphaInner = array.getFloat(R.styleable.SendProgressView_inner_alpha, 0.2f);
        // 多少时间走完整个圆圈（单位秒）
        time = array.getInteger(R.styleable.SendProgressView_time, 90);
        average = 360 / time;

        // 指定view的宽度
        int drawableWidth = array.getInteger(R.styleable.SendProgressView_drawable_width, 0);
        // 指定view的高度
        int drawableHeight = array.getInteger(R.styleable.SendProgressView_drawable_height, 0);

        if (displayMetrics == null) {
            displayMetrics = getResources().getDisplayMetrics();
        }

        // 获取圆圈的宽度
        strokeWidthValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth, displayMetrics);
        // 计算出view的精确宽度
        if (drawableWidth > 0) {
            drawableWidthValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, drawableWidth, displayMetrics);
        } else {
            if (drawable != null) {
                drawableWidthValue = drawable.getIntrinsicWidth();
            } else {
                drawableWidthValue = 0;
            }
        }
        // 计算出view的精确高度
        if (drawableHeight > 0) {
            drawableHeightValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, drawableHeight, displayMetrics);
        } else {
            if (drawable != null) {
                drawableHeightValue = drawable.getIntrinsicHeight();
            } else {
                drawableHeightValue = 0;
            }
        }

        // 外层的画笔
        paint1 = new Paint();
        paint1.setColor(colorInner); // 设置颜色
        paint1.setAlpha((int) (alphaInner * 255));   // 设置透明度
        paint1.setStyle(Paint.Style.STROKE); // 设置为实体
        paint1.setStrokeWidth(strokeWidthValue);  // 设置画笔的宽度
        // 设置线段连接处样式  Join.MITER（结合处为锐角）Join.Round(结合处为圆弧) Join.BEVEL(结合处为直线)
        paint1.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
        paint1.setAntiAlias(true);        // 防锯齿

        // 内层的画笔
        paint2 = new Paint();
        paint2.setColor(colorOuter); // 设置颜色
        paint2.setStyle(Paint.Style.STROKE); // 设置为实体
        paint2.setStrokeWidth(strokeWidthValue);  // 设置画笔的宽度
        // 设置线段连接处样式  Join.MITER（结合处为锐角）Join.Round(结合处为圆弧) Join.BEVEL(结合处为直线)
        paint2.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
        paint2.setAntiAlias(true);        // 防锯齿

        paint3 = new Paint();
        paint3.setAntiAlias(true);        // 防锯齿

        rectF = new RectF();

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        width = getMeasuredWidth();

        float width2 = strokeWidthValue / 2;

        rectF.left = width2;
        rectF.top = width2;
        rectF.right = width - width2;
        rectF.bottom = height - width2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        OuterRing(canvas);
        innerRing(canvas);
        drawPictures(canvas);
    }

    private void drawPictures(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = getBitmap();
        }
        if (bitmap != null) {

            // 获取bitmap的宽度
            int drawableWidth = bitmap.getWidth();
            // 获取bitmap的高度
            int drawableHeight = bitmap.getHeight();

            // 左侧：view的测量宽度 - bitmap的宽度  然后 /2
            int left = (width - drawableWidth) / 2;
            // 上侧：view的高度 - bitmap的高度 然后 /2
            int top = (height - drawableHeight) / 2;
            // 右侧：左侧的位置 + bitmap的宽度
            int right = left + drawableWidth;
            // 下侧：上侧的位置 + bitmap的高度
            int bottom = top + drawableHeight;

//        canvas.drawBitmap(bitmap, null, new RectF(left, top, right, bottom), paint3);
            canvas.drawBitmap(bitmap, left, top, null);
        }
    }

    /**
     * 缩放bitmap大小
     *
     * @param bitmap 指定的bitmap
     * @param width  指定的宽度
     * @param height 指定的高度
     * @return 重新缩放bitmap
     */
    public Bitmap resizeImage(Bitmap bitmap, int width, int height) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        float scaleWidth = ((float) width) / bmpWidth;
        float scaleHeight = ((float) height) / bmpHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
    }

    private Bitmap getBitmap() {
        // todo  下次可以尝试这个方式 drawable.setBounds();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            // 获取缩放的view
            Bitmap resizeBitmap = resizeImage(bitmap, (int) drawableWidthValue, (int) drawableHeightValue);
            return resizeBitmap;
        } else {
            return null;
        }
    }

    /**
     * 设置外环
     */
    private void OuterRing(Canvas canvas) {
        canvas.drawArc(rectF, -90, 360, false, paint1);
    }

    /**
     * 设置内环
     */
    private void innerRing(Canvas canvas) {
        float value = progress * average;
        canvas.drawArc(rectF, -90, value, false, paint2);
    }

    /**
     * 设置当前的进度
     */
    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    /**
     * @return 获取设置的事件
     */
    public long getTime() {
        return time;
    }

    /**
     * 开始执行动画
     */
    public void startAnimation() {
        // 创建值动画，取值的区间为 从0秒到90秒
        anim = ValueAnimator.ofFloat(0f, (time));
        // 设置的时间为 90 秒
        anim.setDuration(time * 1000);
        // 匀速动画
        anim.setInterpolator(new LinearInterpolator());
        // 动画监听回调
        anim.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            LogUtil.e("当前的进度为： " + animatedValue);
            if (animatedValue > time) {
                cancelAnimation();
                return;
            }
            setProgress(animatedValue);
        });
        anim.start();
    }

    /**
     * 取消动画
     */
    public void cancelAnimation() {
        if (anim != null) {
            anim.cancel();
        }
    }

}
