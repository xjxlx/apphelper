package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.helper.R;
import android.helper.utils.BitmapUtil;
import android.helper.utils.LogUtil;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class LeftView extends View {

    private Bitmap bitmap;
    private int bitmapWidth;
    private int bitmapHeight;

    public LeftView(Context context) {
        super(context);
    }

    public LeftView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapUtil.getBitmapForDrawable(ContextCompat.getDrawable(context, R.drawable.icon_left_right));
        if (bitmap != null) {
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtil.e("--->onMeasure");

        int measureWidth = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));
        int measureHeight = resolveSize(heightMeasureSpec, MeasureSpec.getSize(heightMeasureSpec));

        LogUtil.e("bitW:" + bitmapWidth + "  bitH:" + bitmapHeight + " vW:" + measureWidth + " vH:" + measureHeight);

        // 图片显示的区域
        setMeasuredDimension(measureWidth / 2, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtil.e("--->onLayout");
        requestLayout();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.e("--->onDraw");
//        // 获取view的宽高
//        int measuredWidth = getMeasuredWidth();
//        int measuredHeight = getMeasuredHeight();
//
//        // view的宽高是固定的，bitmap的宽高也是固定的，但是如果需要充满布局的话，那么就需要计算比值
//        float scaleW = (float) bitmapWidth / measuredWidth;
//        float scaleH = (float) bitmapHeight / measuredHeight;
//
//        Rect src = new Rect(0, 0, (int) (measuredWidth * scaleW), (int) (measuredHeight * scaleH));
//        Rect dst = new Rect(measuredWidth / 2, 0, measuredWidth * 2, measuredHeight);
//        canvas.drawBitmap(bitmap, src, dst, null);
    }
}
