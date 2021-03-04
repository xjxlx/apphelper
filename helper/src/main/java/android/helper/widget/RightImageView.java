package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.helper.utils.BitmapUtil;
import android.helper.utils.LogUtil;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * 只显示右侧图像的imageView
 */
public class RightImageView extends androidx.appcompat.widget.AppCompatImageView {

    private int measuredWidth, measuredHeight;
    private Bitmap bitmap;

    public RightImageView(Context context) {
        super(context);
    }

    public RightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 高度就是view最大的高度
        measuredHeight = resolveSize(heightMeasureSpec, MeasureSpec.getSize(heightMeasureSpec));

        Bitmap bitmapForImageView = BitmapUtil.getBitmapForImageView(this);
        if (bitmapForImageView != null) {
            int width = bitmapForImageView.getWidth();
            int height = bitmapForImageView.getHeight();

            // 求出bitmap的宽高
            float scaleBitmap = (float) width / height;

            // 宽 / 高 = 比例  ，宽度 = 高度 * 比例
            measuredWidth = (int) (measuredHeight * scaleBitmap);

            LogUtil.e("measuredHeight:" + measuredHeight + "   width:" + width + "   height:" + height);
            LogUtil.e("measuredWidth:" + measuredWidth + "   measuredHeight:" + measuredHeight + "   scaleBitmap:" + scaleBitmap);

            setMeasuredDimension(measuredWidth / 2, measuredHeight);
        }

        bitmap = BitmapUtil.getBitmapForMatrixScale(bitmapForImageView, measuredWidth, measuredHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        Rect src = new Rect(measuredWidth / 2, 0, measuredWidth, measuredHeight);
        Rect des = new Rect(0, 0, measuredWidth / 2, measuredHeight);

        canvas.drawBitmap(bitmap, src, des, null);
    }

}
