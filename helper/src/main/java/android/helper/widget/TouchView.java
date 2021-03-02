package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.helper.R;
import android.helper.utils.BitmapUtil;
import android.helper.utils.ConvertUtil;
import android.helper.utils.LogUtil;
import android.helper.utils.ToastUtil;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 按下的View效果
 */
public class TouchView extends View {
    private final long waitTime = 500; // 长按的等待时间
    private final int waitCode = 199716; // 长按的code

    private String TAG = "Touch";
    private Bitmap mBitmap;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mTopValue; // 距离顶部的距离
    private float mBottomValue; // 距离底部的距离
    private int mRadiusPadding; // 左右边距的值

    private int mLeft;
    private int mRight;
    private int mTop;
    private int mBottom;

    private int mCenterX;       // view的X轴中心
    private int mCenterY;       // view的Y轴中心
    private int mBitmapRadius;  // 圆形的半径
    private int mMaxRadius = 0; // 最大的半径值

    private Rect mRectSrc, mRectDes;
    private final Paint mPaint = new Paint();

    private boolean isStartAnim = false; // 控制是否循环

    private final List<Float> mRadiusList = new ArrayList();
    private final List<Integer> mAlphaList = new ArrayList();
    private int intervalWidth; // 每隔view间隔的宽度
    private float mSpeed;// 扩散的速度
    private float mAlphasZoom; // 透明度的比例

    private TouchListener mListener;

    public TouchView(Context context) {
        super(context);
        initView(context, null);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        // 获取属性
        @SuppressLint("CustomViewStyleable")
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TouchView);

        float dp = ConvertUtil.toDp(100f);

        // drawable
        Drawable drawable = array.getDrawable(R.styleable.TouchView_tv_drawable);
        mTopValue = array.getDimension(R.styleable.TouchView_tv_padding_Top, dp);
        mBottomValue = array.getDimension(R.styleable.TouchView_tv_padding_Bottom, dp);
        mRadiusPadding = (int) array.getDimension(R.styleable.TouchView_tv_padding_Radius, dp);
        intervalWidth = (int) array.getDimension(R.styleable.TouchView_tv_interval_Width, dp);
        mSpeed = array.getFloat(R.styleable.TouchView_tv_speed, 1);

        mBitmap = BitmapUtil.getBitmapForDrawable(drawable);
        // 设置bitmap的数据
        if (mBitmap != null) {
            mBitmapWidth = mBitmap.getWidth();
            mBitmapHeight = mBitmap.getHeight();
        }
        // 释放对象
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LogUtil.e(TAG, "mBitmapWidth:" + mBitmapWidth + "   mBitmapHeight:" + mBitmapHeight);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        // 设定边距
        mLeft = (measuredWidth - mBitmapWidth) / 2 + mRadiusPadding;
        mRight = mLeft + mBitmapWidth;

        if (mTopValue <= 0) {
            mTop = (int) mTopValue;
        } else {
            mTop = (measuredHeight - mBitmapHeight) / 2;
        }
        mBottom = mTop + (mBitmapHeight);

        // 半径
        mCenterX = mLeft + (mBitmapWidth / 2);
        mCenterY = mTop + (mBitmapHeight / 2);
        mBitmapRadius = mBitmapWidth / 2;

        // mMaxRadius = measuredWidth / 2 + mRadiusPadding;
        mMaxRadius = Math.min((measuredWidth / 2 + mRadiusPadding), (measuredHeight / 2));

        mRectSrc = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
        mRectDes = new Rect(mLeft, mTop, mRight, mBottom);

        // 画笔
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#979797"));

        // 求出每一份view占据的透明度
        mAlphasZoom = 255f / (mMaxRadius - mBitmapRadius); //注意这里 如果为int类型就会为0,除数中f一定要加,默认int ;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mBottomValue + mTopValue <= 0) {
            int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec) - (mRadiusPadding * 2);
            setMeasuredDimension(width, width);
        } else {
            setMeasuredDimension(resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec) - (mRadiusPadding * 2),
                    (int) (mBitmapHeight + mTopValue + mBottomValue));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制动态圆圈
        if (isStartAnim) {
            for (int i = 0; i < mRadiusList.size(); i++) {
                // 设置透明度
                Integer alphas = mAlphaList.get(i);
                mPaint.setAlpha(alphas);
                // 设置
                Float width = mRadiusList.get(i);
                canvas.drawCircle(mCenterX, mCenterY, width + mBitmapRadius, mPaint);

                if (mListener != null) {
                    mListener.onDownTouch();
                }

                if (width + mBitmapRadius <= mMaxRadius) {
                    mRadiusList.set(i, (width + mSpeed));
                    mAlphaList.set(i, (int) (255 - (width * mAlphasZoom)));
                }
                LogUtil.e("当前的widht:" + width + " 间隔：" + ((width + mSpeed) + mBitmapRadius));
            }

            if (mRadiusList.get(mRadiusList.size() - 1) >= intervalWidth) {
                mRadiusList.add(0f);
                mAlphaList.add(255);
            }

            if (mRadiusList.size() > 10) {
                mRadiusList.remove(0);
                mAlphaList.remove(0);
            }

            invalidate();
        }

        // 绘制中心图片
        canvas.drawBitmap(mBitmap, mRectSrc, mRectDes, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 按下和移动的动作
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if ((x >= mLeft) && (x <= mRight) && (y >= mTop) && (y <= mBottom)) {
                    // 在这里需要去触动水波纹效果
                    LogUtil.e("开始发送消息 ...");
                    // 发送一个延迟的消息
                    mHandler.sendEmptyMessageDelayed(waitCode, waitTime);
                    return true;
                } else {
                    return false;
                }

            case MotionEvent.ACTION_UP:
                if (mListener != null) {
                    mListener.onUpTouch();
                }
                mHandler.removeMessages(waitCode);
                mHandler.removeCallbacksAndMessages(null);
                endView();
                invalidate();
                return false;
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startView();
        }
    };

    private void startView() {
        ToastUtil.show("开始执行动画...");
        this.isStartAnim = true;
        mHandler.removeMessages(waitCode);
        mHandler.removeCallbacksAndMessages(null);

        // 添加默认的数据
        mRadiusList.add(0f);
        mAlphaList.add(255);
        invalidate();
    }

    public void endView() {
        mHandler.removeMessages(waitCode);
        mHandler.removeCallbacksAndMessages(null);
        this.isStartAnim = false;
        mRadiusList.clear();
        mAlphaList.clear();
        invalidate();
    }

    public interface TouchListener {
        void onDownTouch();

        void onUpTouch();
    }

    public void setOnTouchListener(TouchListener listener) {
        this.mListener = listener;
    }
}
