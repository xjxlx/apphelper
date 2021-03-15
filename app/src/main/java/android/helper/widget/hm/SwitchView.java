package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.helper.R;
import android.helper.utils.BitmapUtil;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SwitchView extends View {

    private Bitmap mBitmapBackground;
    private Bitmap mBitmapSelector;

    public SwitchView(Context context) {
        super(context);
        initView(context, null);
    }

    public SwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        Bitmap background = BitmapUtil.getBitmapForResourceId(context, R.mipmap.switch_background);
        Bitmap selector = BitmapUtil.getBitmapForResourceId(context, R.mipmap.slide_button);

        mBitmapBackground = BitmapUtil.getScaleBitmap(background, background.getWidth() * 4, background.getHeight() * 4);
        mBitmapSelector = BitmapUtil.getScaleBitmap(selector, selector.getWidth() * 4, selector.getHeight() * 4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = mBitmapBackground.getWidth();
        int height = mBitmapBackground.getHeight();

        setMeasuredDimension(width, height);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect src1 = new Rect(0, 0, mBitmapBackground.getWidth(), mBitmapBackground.getHeight());
        Rect des1 = new Rect(0, 0, mBitmapBackground.getWidth(), mBitmapBackground.getHeight());

        Rect src2 = new Rect(0, 0, mBitmapSelector.getWidth(), mBitmapSelector.getHeight());
        Rect des2 = new Rect(0, 0, mBitmapSelector.getWidth(), mBitmapSelector.getHeight());

        canvas.drawBitmap(mBitmapBackground, src1, des1, null);
        canvas.drawBitmap(mBitmapSelector, src2, des2, null);
    }
}
