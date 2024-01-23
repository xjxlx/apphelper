package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.CustomViewUtil;

public class TextViewGradient extends View {

    private final Paint mPaint = new Paint();
    private String mContent;
    private float mContentWidth;
    private float mContentHeight;
    private int[] mIntsColor;
    private float[] mIntsPositions;
    private LinearGradient linearGradient;
    private float mBaseline;
    private float mContentX;

    public TextViewGradient(Context context) {
        super(context);
        initView(context, null);
    }

    public TextViewGradient(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewGradient);
        // 文字的大小
        float textSize = typedArray.getDimension(R.styleable.TextViewGradient_tvg_text_size, 5);
        mPaint.setTextSize(textSize);
        // 文字的内容
        String string = typedArray.getString(R.styleable.TextViewGradient_tvg_text);
        if (!TextUtils.isEmpty(string)) {
            mContent = string;
        }
        typedArray.recycle();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(mContent)) {
            // 获取textView的宽高
            if (mContentHeight <= 0 || mContentWidth <= 0) {
                float[] textSize = CustomViewUtil.getTextSize(mPaint, mContent);
                mContentWidth = textSize[0];
                mContentHeight = textSize[1];
            }
            // 设置权重
            if (mIntsPositions == null) {
                mIntsPositions = new float[]{0F, 1F};
            }
            if (mIntsColor != null) {
                if (linearGradient == null) {
                    linearGradient = new LinearGradient(0, 0, mContentWidth, mContentHeight, mIntsColor, mIntsPositions, Shader.TileMode.CLAMP);
                }
                mPaint.setShader(linearGradient);
            }
            // 获取基线
            if (mBaseline <= 0) {
                mBaseline = CustomViewUtil.getBaseLine(mPaint, mContent);
            }
            if (mContentX <= 0) {
                mContentX = (int) ((getMeasuredWidth() - mContentWidth) / 2);
            }
            canvas.drawText(mContent, mContentX, mBaseline, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!TextUtils.isEmpty(mContent)) {
            if (mContentHeight <= 0 || mContentWidth <= 0) {
                float[] textSize = CustomViewUtil.getTextSize(mPaint, mContent);
                mContentWidth = textSize[0];
                mContentHeight = textSize[1];
            }
            int i1 = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
            int i2 = resolveSize((int) mContentHeight, heightMeasureSpec);
            setMeasuredDimension(i1, i2);
        }
    }

    public void setColors(int[] colors) {
        if (colors != null) {
            mIntsColor = colors;
            invalidate();
        }
    }

    public void setPositions(float[] positions) {
        if (positions != null) {
            mIntsPositions = positions;
            invalidate();
        }
    }

    public void setFont(Context context, String font) {
        if (!TextUtils.isEmpty(font)) {
            AssetManager assets = context.getAssets();
            if (assets != null) {
                Typeface fromAsset = Typeface.createFromAsset(assets, font);
                mPaint.setTypeface(fromAsset);
                invalidate();
            }
        }
    }

    public void setContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            mContent = content;
            invalidate();
        }
    }

    public void setStroke(Paint.Style style, int color, int strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(color);
        mPaint.setStyle(style);
        invalidate();
    }
}
