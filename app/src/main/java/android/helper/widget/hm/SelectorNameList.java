package android.helper.widget.hm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.helper.R;
import android.util.AttributeSet;

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
 * 2：设置总的高度，高度等于：红心的高度 + 列表中每个view的高度 + paddingTop + paddingBottom
 *
 * <p>
 * 1:顶部位置绘制一个红心，作为收藏的标记
 * 2：计算出所有的高度 =  所有的字的高度 + paddingTop + paddingBottom + 红心的高度 + paddingTop + paddingBottom
 * 3：计算出所有的宽度 =  便利所有子的宽度 ，获取到最大的那个宽度  + paddingLeft + paddingRight
 * 4：内容设置居中，红心：布局宽度 - bitmap宽 /2 ,字，paint设置
 */
public class SelectorNameList extends BaseView {

    private final String[] mIndexArr = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    private float mPaddingLeft = 40;
    private float mPaddingRight = 40;
    private float mPaddingTop = 20;
    private float mPaddingBottom = 20;
    private Paint mPaint;
    private int mTotalHeight;
    private int mTotalWidth;
    private final float mBitmapTargetWidth = ConvertUtil.toDp(40);// bitmap的目标宽度
    private Bitmap mBitmap;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mBitmapLeft;

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
        mPaint.setTextSize(45);

        // 获取bitmap
        Bitmap bitmap = ResourceUtil.getBitmap(R.mipmap.icon_rad_xin);
        // 生成一个新的bitmap
        mBitmap = BitmapUtil.getBitmapForMatrixScaleWidth(bitmap, mBitmapTargetWidth);

        int tempWidth = 0;
        // 统计出所有字的高度
        for (String value : mIndexArr) {
            // 获取字的宽高
            float[] textSize = CustomViewUtil.getTextSize(mPaint, value);

            // 获取当前view发的高度
            mTotalHeight += (textSize[1] + mPaddingTop + mPaddingBottom);

            // 对比出最大的宽度
            int width = (int) textSize[0];
            mTotalWidth = Math.max(tempWidth, width);
            // 变量的赋值
            tempWidth = width;
        }

        // 计算出bitmap的高度
        int bitmapHeight = mBitmap.getHeight();
        // 获取所有的高度
        mTotalHeight += (bitmapHeight + mPaddingTop + mPaddingBottom);

        // 计算所有的宽度
        mTotalWidth += (mBitmap.getWidth() + mPaddingLeft + mPaddingRight);
        LogUtil.e("view的总高度为：" + mTotalHeight + "   view的宽度：" + mTotalWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 重新设置宽高
        setMeasuredDimension(mTotalWidth, mTotalHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();

        // 获取bitmap的left值
        mBitmapLeft = (mMeasuredWidth - mBitmap.getWidth()) / 2;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制红心
//        canvas.drawBitmap(mBitmap, mBitmapLeft, 0, mPaint);
//
//        /*
//         * 绘制文字
//         * 高度 = bitmap 的高度 + top + bottom +  每个view的高度 + top + bottom
//         */
//        float height = mBitmap.getHeight() + mPaddingTop + mPaddingBottom;
//        for (String value : mIndexArr) {
//
//            float textHeight = CustomViewUtil.getTextHeight(mPaint, value);
//            height += (textHeight + mPaddingTop + mPaddingBottom);
//
//            canvas.drawText(value, mPaddingLeft, height, mPaint);
//        }

        float width = CustomViewUtil.getTextWidth(mPaint, "A");
        float v = (mMeasuredWidth - width) / 2;

        canvas.drawText("A", v, mPaddingTop, mPaint);
    }
}
