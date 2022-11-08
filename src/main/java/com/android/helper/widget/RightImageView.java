package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.android.helper.utils.BitmapUtil;

/**
 * 只显示右侧图像的imageView
 */
public class RightImageView extends androidx.appcompat.widget.AppCompatImageView {

    public RightImageView(Context context) {
        super(context);
    }

    public RightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        Bitmap forImageView = BitmapUtil.getBitmapForImageView(this);

        Bitmap bitmap = BitmapUtil.getBitmapForScale(forImageView, measuredWidth, measuredHeight);

        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int dx = width / 2;

            Rect src = new Rect(0, 0, width, height);
            Rect des = new Rect(-dx, 0, width - dx, height);

            canvas.drawBitmap(bitmap, src, des, null);
        }
    }

}
