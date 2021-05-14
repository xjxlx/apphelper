package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.helper.R;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.android.helper.base.BaseView;
import com.android.helper.utils.BitmapUtil;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.ResourceUtil;

/**
 * 检索通讯录名字的列表数据
 * 目标：
 * 写一个列表 从 A  - z ,最上方在设置一个红心
 * <p>
 * 实现逻辑：
 * 1：获取红心的bitmap,并绘制
 * 2：设置总的高度，高度等于：红心的高度 + 每个固定大小的高度 * 集合的长度
 *
 * <p>
 * 1:顶部位置绘制一个红心，作为收藏的标记
 */
public class SelectorNameList extends BaseView {

    private final String[] mIndexArr = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    private final float mTextTotalHeight = 350;// 每个字的高度
    private final float mPaddingLeft = 20;
    private final float mPaddingRight = 20;
    private final float mInterval = 20;

    private Paint mPaint;
    private float mTotalHeight;
    private float mTotalWidth;
    private final float mBitmapTargetWidth = ConvertUtil.toDp(40);// bitmap的目标宽度
    private Bitmap mBitmap;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mBitmapLeft;
    private float mTextCenter;

    public SelectorNameList(Context context) {
        super(context);
        initView(context, null);
    }

    public SelectorNameList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(60);
        mPaint.setTextAlign(Paint.Align.CENTER);

        // 获取bitmap
        Bitmap bitmap = ResourceUtil.getBitmap(R.mipmap.icon_rad_xin);
        // 生成一个新的bitmap
        mBitmap = BitmapUtil.getBitmapForMatrixScaleWidth(bitmap, mBitmapTargetWidth);

        float tempWidth = 0;
        // 统计出所有字的高度
        for (String value : mIndexArr) {
            // 获取字的宽高
            float width = CustomViewUtil.getTextWidth(mPaint, value);

            // 对比出最大的宽度
            mTotalWidth = Math.max(tempWidth, width);
            // 变量的赋值
            tempWidth = width;
        }

        // 计算出bitmap的高度
        int bitmapHeight = mBitmap.getHeight();
        // 获取所有的高度  = 字的所有高度 + bitmap的高度 + bitmap上方的高度 + 字最后的底部高度
        mTotalHeight += (mInterval + bitmapHeight + ((mTextTotalHeight + mInterval) * mIndexArr.length));

        // 计算所有的宽度
        mTotalWidth += (mBitmap.getWidth() + mPaddingLeft + mPaddingRight);
        LogUtil.e("view的总高度为：" + mTotalHeight + "   view的宽度：" + mTotalWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 重新设置宽高
//        setMeasuredDimension((int) mTotalWidth, (int) mTotalHeight);
//        setMeasuredDimension(500, 70);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();

        // 获取bitmap的left值
        mBitmapLeft = (mMeasuredWidth - mBitmap.getWidth()) / 2;

        // 获取文字的间距
        if (mMeasuredWidth > 0) {
            mTextCenter = mMeasuredWidth / 2;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制一个顶边文字的毕竟
        Rect rect1 = new Rect(50, 50, 600, 300);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(rect1, mPaint);

        // 绘制一个顶边的文字
        String content1 = "我是顶边的文字";
        mPaint.setColor(Color.WHITE);
        // 计算出文字距离顶部的距离
        int top = Math.abs(rect1.top);
        float baseLine = CustomViewUtil.getBaseLine(mPaint, content1);
        canvas.drawText(content1, rect1.centerX(), top + baseLine, mPaint);

        // 绘制一个盒子中心的文字
        Rect rect2 = new Rect(50, 500, 600, 700);
        String content2 = "我是居中的文字";
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(rect2, mPaint);

        float textHeight = CustomViewUtil.getTextHeight(mPaint, content2);
        float basline = rect2.centerY() + textHeight / 2;
        mPaint.setColor(Color.WHITE);
        canvas.drawText(content2, rect2.centerX(), basline, mPaint);

        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(50, rect2.centerY(), 600, rect2.centerY(), mPaint);


        String content3 = "matt's blog";
        Rect rect3 = new Rect(50, 800, 600, 1000);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(rect3, mPaint);


        mPaint.setColor(Color.parseColor("#887766"));
        canvas.drawLine(50,rect3.centerY(),600,rect3.centerY(),mPaint);

        canvas.drawText(content3,rect3.centerX(), ,mPaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                float position = event.getY();
                if ((position >= 0) && (position <= mMeasuredHeight)) {

                    LogUtil.e("position:" + position);
                }

                break;

            case MotionEvent.ACTION_UP:

                break;

        }

        return true;
    }
}
