package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.android.common.utils.LogUtil;
import com.android.helper.R;
import com.android.helper.utils.BitmapUtil;
import com.android.helper.utils.ScreenUtil;

/**
 * 只显示右侧图像的imageView
 */
public class RightImageView extends androidx.appcompat.widget.AppCompatImageView {
    private int orientation = 2;

    public RightImageView(Context context) {
        super(context);
    }

    public RightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RightImageView);
            // 1: 已宽度为主，2：以高度为主
            orientation = typedArray.getInt(R.styleable.RightImageView_right_orientation, 2);
            typedArray.recycle();
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        Rect src = null;
        Rect des = null;
        Bitmap scaleBitmap = null;

        // 得到view的宽高
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        // 得到图片的宽高
        Bitmap forImageView = BitmapUtil.getBitmapForImageView(this);
        if (forImageView != null) {
            int bitmapWidth = forImageView.getWidth();
            int bitmapHeight = forImageView.getHeight();
            int screenWidth = ScreenUtil.getScreenWidth(getContext());

            // LogUtil.e("measuredWidth: " + measuredWidth + " measuredHeight: " +
            // measuredHeight + " bitmapWidth： " + bitmapWidth + " bitmapHeight： " +
            // bitmapHeight);

            if (orientation == 1) {
                // 图片 / 屏幕宽度
                float ratio = (float) bitmapWidth / bitmapHeight;
                // 比例：宽 / 高 --> 高 = 宽 / 比例
                int realHeight = (int) (measuredWidth * 2 / ratio);
                LogUtil.e("realHeight: " + realHeight);
                int interval = (measuredHeight - realHeight) / 2;
                scaleBitmap = BitmapUtil.getBitmapForScale(forImageView, measuredWidth, realHeight);
                src = new Rect(measuredWidth / 2, 0, measuredWidth, realHeight);
                des = new Rect(0, interval, measuredWidth, realHeight + interval);
                canvas.drawBitmap(scaleBitmap, src, des, null);

            } else if (orientation == 2) {
                // 图片高度和view高度的最大比值
                float i = (float) bitmapHeight / measuredHeight;
                // 得到最大图片的宽度
                float bitmapMaxWidth = bitmapWidth / i;
                // view的宽度 * 2 - 图片的最大值， /2 得到两边的间距
                float interval = (measuredWidth * 2 - bitmapMaxWidth) / 2;

                // 计算view高度和图片高度的比值
                float ratio = (float) measuredHeight / bitmapHeight;
                // 得到图片的真实宽度
                int realWidth = (int) (bitmapWidth / ratio);
                // LogUtil.e("realWidth: " + realWidth + " realHeight: " + measuredHeight);

                // 缩放bitmap
                scaleBitmap = BitmapUtil.getBitmapForScale(forImageView, realWidth, measuredHeight);

                // x轴的偏移
                int dx = realWidth / 2;
                // 计算开始的偏移
                // LogUtil.e("realWidth: " + realWidth + " measuredWidth: " + measuredWidth);

                src = new Rect(dx, 0, realWidth, measuredHeight);
                des = new Rect(0, 0, (int) (measuredWidth - interval), measuredHeight);
                canvas.drawBitmap(scaleBitmap, src, des, null);
            }
        }
    }

}
