package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.helper.R;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.helper.utils.BitmapUtil;
import com.android.helper.utils.LogUtil;

public class SwitchView extends View {

    private Bitmap mBitmapBackground;
    private Bitmap mBitmapSelector;
    private float mLeft = 20;
    private int mBackgroundWidth;
    private int mSelectorWidth;
    private float mTop;
    private boolean isOpen;// 默认是关闭的状态
    private SwitchChangeListener mListener;

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

        mBitmapBackground = BitmapUtil.getScaleBitmap(background, background.getWidth() * 2, background.getHeight() * 2);
        mBitmapSelector = BitmapUtil.getScaleBitmap(selector, selector.getWidth() * 2, selector.getHeight() * 2);

        // 背景的宽度
        mBackgroundWidth = mBitmapBackground.getWidth();
        int height = mBitmapBackground.getHeight();
        // 滑块的宽度
        mSelectorWidth = mBitmapSelector.getWidth();
        int height1 = mBitmapSelector.getHeight();
        int i = height - height1;
        mTop = i / 2;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    mLeft = 20;
                } else {
                    mLeft = mBackgroundWidth - mSelectorWidth - 20;
                }
                invalidate();
            }
        });
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
        LogUtil.e("onDraw:--->left:" + mLeft);
        canvas.drawBitmap(mBitmapSelector, mLeft, mTop, null);
    }

    private float mStartX;
    private boolean isMove;
    private float mOffsetX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mStartX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                float eventX = event.getX();
                float dx = eventX - mStartX;

                mLeft += dx;

                mOffsetX = Math.abs(mLeft);

                // 左侧的边界
                if (mLeft < 20) {
                    mLeft = 20;
                }

                // 右侧的边界
                int i = mBackgroundWidth - mSelectorWidth - 20;
                if (mLeft > i) {
                    mLeft = i;
                }

                LogUtil.e("left:" + mLeft);

                invalidate();
                mStartX = eventX;

                break;

            case MotionEvent.ACTION_UP:

                // 移动还是惦记
                isMove = mOffsetX > 5;

                // 去除左侧的边距，剩余的距离
                int i1 = mBackgroundWidth - mSelectorWidth - 20;
                int offsetX = (i1) / 2;
                LogUtil.e("isMove:" + isMove);

                if (isMove) { // 如果是移动的话，那么入股哦当前left的值大于
                    if (mLeft > offsetX) {
                        mLeft = i1;
                        isOpen = true;
                    } else {
                        mLeft = 20;
                        isOpen = false;
                    }
                } else {
                    if (isOpen) {
                        mLeft = 20;
                        isOpen = false;
                    } else {
                        mLeft = i1;
                        isOpen = true;
                    }
                }

                if (mListener != null) {
                    mListener.onChange(isOpen);
                }

                invalidate();
                // 清空数据
                mOffsetX = 0;
                break;
        }
        return true;
    }

    public interface SwitchChangeListener {
        void onChange(boolean isOpen);
    }

    public void setSwitchChangeListener(SwitchChangeListener listener) {
        mListener = listener;
    }
}
