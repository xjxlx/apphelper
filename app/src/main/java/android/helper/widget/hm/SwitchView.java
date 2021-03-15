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
    private float left = 0;
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
        canvas.drawBitmap(mBitmapSelector, left + 20, mTop, null);
    }

    private float mStartX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                float endX = event.getX();
                left = endX - mStartX;

                // 限制左边
                if (left < 0) {
                    left = 0;
                }
                // 限制右侧
                if ((left + mSelectorWidth + 40) > mBackgroundWidth) {
                    left = mBackgroundWidth - mSelectorWidth - 40;
                }

                invalidate();
                LogUtil.e("left:" + left + "  mSelectorWidth:" + mSelectorWidth + "   mBackgroundWidth:" + mBackgroundWidth);
                break;

            case MotionEvent.ACTION_UP:

                break;
        }

//        return super.onTouchEvent(event);
        return true;
    }
}
