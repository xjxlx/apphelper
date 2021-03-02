package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import android.helper.utils.BitmapUtil;

/**
 * 只显示右侧图像的imageView
 */
public class RightImageView extends androidx.appcompat.widget.AppCompatImageView {

    private int measuredWidth;
    private int measuredHeight;
    private Bitmap mBitmap;

    public RightImageView(Context context) {
        super(context);
    }

    public RightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取view的宽高
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();

        if ((measuredWidth > 0) && (measuredHeight > 0)) {
            setMeasuredDimension(measuredWidth / 2, measuredHeight);
        }
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //   super.onDraw(canvas);
        mBitmap = BitmapUtil.getBitmapForImageView(this);

        if (mBitmap != null && measuredWidth > 0) {
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            Rect rect = new Rect((width / 2), 0, width, height);
            RectF rectF = new RectF(0, 0, measuredWidth >> 1, measuredHeight);
            canvas.drawBitmap(mBitmap, rect, rectF, null);
        }
    }


}
