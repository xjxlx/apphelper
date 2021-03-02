package android.helper.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.helper.R;
import android.helper.utils.ConvertUtil;
import android.helper.utils.CustomViewUtil;

public class CustomTextView extends View {

    private Paint paint;
    private Paint paint3;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setTextSize(ConvertUtil.toSp(18));
        paint.setColor(ContextCompat.getColor(context, R.color.black_10));
        paint.setAntiAlias(true);

        paint3 = new Paint();
        paint3.setStrokeWidth(5);
        paint3.setColor(ContextCompat.getColor(context, R.color.black_10));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        int size = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);

        setMeasuredDimension(width, size);
    }

    private String value = "1234";
    private String value2 = "56789";
    private String value3 = "伟大的中国我爱你";
    private float line = ConvertUtil.toDp(5);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float baseLine = CustomViewUtil.getBaseLine(paint, value);
        canvas.drawText(value, 0, baseLine, paint);

        float v = CustomViewUtil.getTextSize(paint, value)[1];
        float baseLine2 = CustomViewUtil.getBaseLine(paint, value2);

        canvas.drawText(value2, 0, (v + baseLine2 + line), paint);

        float baseLine3 = CustomViewUtil.getBaseLine(paint, value3);
        float v2 = CustomViewUtil.getTextSize(paint, value2)[1];
        canvas.drawText(value3, 0, (v + v2 + baseLine3 + line + line), paint);

        float v3 = CustomViewUtil.getTextSize(paint, value3)[1];

        int y = (int) ((v + v2 + v3) + (line * 3));
        canvas.drawLine(0, y, 50, y, paint3);
    }
}
