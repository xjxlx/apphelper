package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.helper.R;
import android.helper.utils.BitmapUtil;
import android.helper.utils.LogUtil;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class SwitchView extends View {

    private Bitmap mBitmapBackground;
    private Bitmap mBitmapSelector;
    private int left = 20;
    private int mBackgroundWidth;
    private int mSelectorWidth;
    private int mTop;

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

        // 背景的宽度
        mBackgroundWidth = mBitmapBackground.getWidth();
        int height = mBitmapBackground.getHeight();
        // 滑块的宽度
        mSelectorWidth = mBitmapSelector.getWidth();
        int height1 = mBitmapSelector.getHeight();
        int i = height - height1;
        mTop = i / 2;
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

        canvas.drawBitmap(mBitmapBackground, 0, 0, null);
        LogUtil.e("onDraw:--->left:" + left);
        canvas.drawBitmap(mBitmapSelector, left, mTop, null);
    }

    private int mStartX;
    private int mOffestX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) event.getX();
                mOffestX = endX - mStartX;

                LogUtil.e("sx:" + mStartX + "  endx:" + endX + "  dx:" + mOffestX);

                // 限制左侧
                if (mOffestX < 20) {
                    mOffestX = 20;
                }

                // 限制右侧
                if ((mOffestX + mSelectorWidth + 20) > mBackgroundWidth) {
                    mOffestX = mBackgroundWidth - mSelectorWidth - 20;
                }
                invalidate();
                left = mOffestX;

                LogUtil.e("移动的值为：" + mOffestX);
                break;

            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }
}
