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

    private int measuredWidth, measuredHeight;

    public LeftImageView(Context context) {
        super(context);
    }

    public LeftImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取view的高度
        measuredHeight = resolveSize(heightMeasureSpec, MeasureSpec.getSize(heightMeasureSpec));
        // 获取设置的bitmap
        Bitmap bitmapForImageView = BitmapUtil.getBitmapForImageView(this);
        if (bitmapForImageView != null) {
            int width = bitmapForImageView.getWidth();
            int height = bitmapForImageView.getHeight();

            float scaleBitmap = (float) width / height;
            //  LogUtil.e("view的高度：" + measuredHeight + "   bitmap的宽：" + width + "   bitmap的高：" + height + "  获取比例：" + scaleBitmap);
            // view的高度就设置为view本身的高度，view的宽度要进行等比例的缩放
            // 宽 / 高 = 比例   ，宽  高 * 比例，这样算出来的宽度能保持比例
            measuredWidth = (int) (measuredHeight * scaleBitmap);
            // 重新设置宽高
            setMeasuredDimension(measuredWidth / 2, measuredHeight);
            //  LogUtil.e("重新设置的宽高为：" + measuredWidth + "   高度：" + measuredHeight);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //   super.onDraw(canvas);

        // LogUtil.e("mea:" + measuredWidth);
        // 右侧除以2，等于说是指显示宽度的一半
        Rect src = new Rect(0, 0, measuredWidth, measuredHeight);
        // 左侧除以2，等于说是从view宽度的一半开始显示
        Rect des = new Rect(0, 0, measuredWidth, measuredHeight);

        // 缩放bitmap
        Bitmap bitmap = BitmapUtil.getBitmapForScale(BitmapUtil.getBitmapForImageView(this), measuredWidth, measuredHeight);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, src, des, null);
        }
    }

}
