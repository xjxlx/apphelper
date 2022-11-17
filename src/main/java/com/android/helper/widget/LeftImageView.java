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
 * 只显示左侧图像的imageView
 */
public class LeftImageView extends androidx.appcompat.widget.AppCompatImageView {

    public LeftImageView(Context context) {
        super(context);
    }

    public LeftImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //   super.onDraw(canvas);

        // 缩放bitmap
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        Bitmap bitmapForImageView = BitmapUtil.getBitmapForImageView(this);

        Bitmap bitmap = BitmapUtil.getBitmapForScale(bitmapForImageView, measuredWidth, measuredHeight);

        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // src : 剪裁的部分
            Rect src = new Rect(0, 0, width, height);
            // des ：绘制的位置
            int dx = width / 2;
            Rect des = new Rect(dx, 0, width + dx, height);
            canvas.drawBitmap(bitmap, src, des, null);
        }
    }

}
