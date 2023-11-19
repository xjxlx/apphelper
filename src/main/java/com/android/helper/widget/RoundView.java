package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.android.helper.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义任意角度的圆角矩形
 */
public class RoundView extends androidx.appcompat.widget.AppCompatImageView {

    private final Paint mPaint = new Paint();
    private final PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    // 四个角的x,y半径
    private final float[] mRadiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    public Bitmap mBitmap;
    float mRoundRadius = 0; // 圆角的度数
    /**
     * 圆角的类型  1:圆形  2：圆角
     */
    private int mRoundType;
    /**
     * 圆角的角度 0：四个角全用 1：左上角 2：右上角  3：左下角  4：右下角
     */
    private int mRoundAngle;
    private RectF mDstRectF;
    private Path mPath;
    private int measuredWidth;
    private int measuredHeight;
    private Matrix matrix;
    private int mRoundDiameter; // 直径

    public RoundView(Context context) {
        super(context);
        initView(context, null);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        // 禁用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundView);
            // 圆角的类型  1:圆形  2：圆角
            mRoundType = typedArray.getInt(R.styleable.RoundView_rv_roundType, 0);
            // 圆角的角度 1：四个角全用 2：左上角 3：右上角  4：左下角  5：右下角
            mRoundAngle = typedArray.getInt(R.styleable.RoundView_rv_angle, 0);
            // 圆角的度数
            mRoundRadius = typedArray.getDimension(R.styleable.RoundView_rv_radius, 0);
            typedArray.recycle();
        }
        mPaint.setColor(Color.WHITE); // 设置背景颜色
        mPaint.setAntiAlias(true);//设置抗锯齿
        mPaint.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取view的宽高
        if (mBitmap != null) {
            measuredWidth = mBitmap.getWidth();
            measuredHeight = mBitmap.getHeight();
        } else {
            measuredWidth = getMeasuredWidth();
            measuredHeight = getMeasuredHeight();
        }

        /* 如果类型是圆形，则强制改变view的宽高一致，以小值为准 */
        if ((measuredWidth > 0) && (measuredHeight > 0)) {
            if (mRoundType == 1) { // 圆形图片
                // 求出圆形图片的直径
                float mRadius = Math.min(measuredWidth, measuredHeight);
                // 四舍五入的计算，数据只能多，不能少
                mRoundDiameter = Math.round(mRadius);
                setMeasuredDimension(mRoundDiameter, mRoundDiameter);
            } else if (mRoundType == 2) { // 圆角图片
                setMeasuredDimension(measuredWidth, measuredHeight);
            }
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);  // 此处必须禁用，否则就会绘制两次图片
        if (matrix == null) {
            matrix = new Matrix();
        }
        // 获取bitmap
        mBitmap = getBitmap();
        if (mBitmap == null) {
            return;
        }
        // 新建矩形图像
        mDstRectF = new RectF(0, 0, measuredWidth, measuredHeight);
        // 新建图层
        int saveLayer = canvas.saveLayer(mDstRectF, mPaint);
        if (mRoundType == 1) {  // 设置圆形
            if (mRoundDiameter > 0) { // 直径大于0的时候才会绘制
                // 绘制圆形
                mPaint.setColor(Color.WHITE);// 设置白色的底色
                // CX: 设置x轴的圆心   CY: 设置Y轴的圆心      radius： 半径
                int radius = mRoundDiameter / 2;
                // 绘制圆形
                canvas.drawCircle(radius, radius, radius, mPaint);
                // 设置交集的模式
                mPaint.setXfermode(mXfermode);
                // 绘制图形
                canvas.drawBitmap(mBitmap, null, mDstRectF, mPaint);
                // 此处有两个做法，一个是只使用dst作为背景，src 传入null，让整个bitmap都显示在dst的矩形上，
                // 另外一个办法就是使用矩阵去绘制图形，这里需要传入矩阵的宽高缩放比例
                //   matrix.postScale(2.1f, 2.1f);
                //  canvas.drawBitmap(mBitmap, matrix, mPaint);
            }

        } else if (mRoundType == 2) { // 设置圆角矩形
            if (mRoundAngle == 0) {// 四个角全都绘制
                // 绘制圆角矩形
                canvas.drawRoundRect(mDstRectF, mRoundRadius, mRoundRadius, mPaint);
                // 绘制交集模式
                mPaint.setXfermode(mXfermode);
                // 绘制图像
                canvas.drawBitmap(mBitmap, null, mDstRectF, mPaint);
            } else {
                // 左上角
                if ((mRoundAngle & FlagType.FLAG_LEFT_TOP) == FlagType.FLAG_LEFT_TOP) {
                    mRadiusArray[0] = mRoundRadius;
                    mRadiusArray[1] = mRoundRadius;
                }
                if ((mRoundAngle & FlagType.FLAG_RIGHT_TOP) == FlagType.FLAG_RIGHT_TOP) {
                    mRadiusArray[2] = mRoundRadius;
                    mRadiusArray[3] = mRoundRadius;
                }
                if ((mRoundAngle & FlagType.FLAG_RIGHT_BOTTOM) == FlagType.FLAG_RIGHT_BOTTOM) {
                    mRadiusArray[4] = mRoundRadius;
                    mRadiusArray[5] = mRoundRadius;
                }
                if ((mRoundAngle & FlagType.FLAG_LEFT_BOTTOM) == FlagType.FLAG_LEFT_BOTTOM) {
                    mRadiusArray[6] = mRoundRadius;
                    mRadiusArray[7] = mRoundRadius;
                }
                if (mPath == null) {
                    mPath = new Path();
                }
                // 绘制自定义角度的圆形
                mPath.addRoundRect(mDstRectF, mRadiusArray, Path.Direction.CW);
                canvas.drawPath(mPath, mPaint);
                // 设置交集的模式
                mPaint.setXfermode(mXfermode);
                // 设置图片
                canvas.drawBitmap(mBitmap, null, mDstRectF, mPaint);
            }
            // 此处有两个做法，一个是只使用dst作为背景，src 传入null，让整个bitmap都显示在dst的矩形上，
            // 另外一个办法就是使用矩阵去绘制图形，这里需要传入矩阵的宽高缩放比例
            //   matrix.postScale(2.1f, 2.1f);
            //  canvas.drawBitmap(mBitmap, matrix, mPaint);
        }
        // 画笔的交叉模式置空
        mPaint.setXfermode(null);
        // 合并图层
        canvas.restoreToCount(saveLayer);
    }

    /**
     * @return 获取设置的图片内容
     */
    private Bitmap getBitmap() {
        Bitmap bitmap = null;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof ColorDrawable) {
                // 获取view的宽高
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                int color = ((ColorDrawable) drawable).getColor();
                c.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            } else {
                // 获取应该还会有其他类型的图片需要处理，待定
            }
            // 重新绘制
            requestLayout();
        }
        return bitmap;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FlagType.FLAG_ALL, FlagType.FLAG_LEFT_TOP, FlagType.FLAG_LEFT_BOTTOM, FlagType.FLAG_LEFT_TOP_BOTTOM, FlagType.FLAG_RIGHT_TOP, FlagType.FLAG_RIGHT_BOTTOM, FlagType.FLAG_RIGHT_TOP_BOTTOM})
    public @interface FlagType {
        int FLAG_ALL = 0;
        int FLAG_LEFT_TOP = 1 << 1; // 2
        int FLAG_LEFT_BOTTOM = 1 << 2; // 4
        int FLAG_LEFT_TOP_BOTTOM = (FLAG_LEFT_TOP | FLAG_LEFT_BOTTOM); // 6
        int FLAG_RIGHT_TOP = 1 << 3; // 8
        int FLAG_RIGHT_BOTTOM = 1 << 4; //16
        int FLAG_RIGHT_TOP_BOTTOM = (FLAG_RIGHT_TOP | FLAG_RIGHT_BOTTOM); // 24
    }

}
