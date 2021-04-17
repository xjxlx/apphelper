package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.helper.R;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.CustomViewUtil;
import com.android.helper.utils.TextViewUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多列的ListView，适用于数据简单的列表，不适用于数据复杂的列表，因为里面没有做数据的复用
 */
public class MultipleListView extends View {

    private final Paint mPaint = new Paint();
    private final Paint mPaint2 = new Paint();
    private final List<String> mList = new ArrayList<>();
    private int divider; // 中心线
    private float mLineHeight; // 文字之间的间隔

    private final Map<Integer, Float> mMapBaseLine = new HashMap<>(); // 所有view的基准线
    private final Map<Integer, Float> mMapContentHeight = new HashMap<>(); // 所有view的高度
    private float textSize;

    public MultipleListView(Context context) {
        super(context);
        initView(context, null);
    }

    public MultipleListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.LEFT); // 文字在左侧

        mPaint2.setColor(ContextCompat.getColor(context, R.color.picture_color_black));
        mPaint2.setStrokeWidth(2);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultipleListView);

            // 字体的大小
            textSize = typedArray.getDimension(R.styleable.MultipleListView_mlv_text_size, ConvertUtil.toDp(18));
            // 字体颜色
            int color = typedArray.getColor(R.styleable.MultipleListView_mlv_text_color, Color.parseColor("#ff404040"));
            // 字体
            String fontName = typedArray.getString(R.styleable.MultipleListView_mlv_text_font);
            // 间隔高度
            float line = typedArray.getDimension(R.styleable.MultipleListView_mlv_line_height, ConvertUtil.toDp(5));

            setTextColor(color);
            setTextSize(textSize);
            setTextFont(context, fontName);
            setLineHeight(line);

            // 释放资源
            typedArray.recycle();
        }
    }

    /**
     * 设置字体的颜色
     *
     * @param color 指定字体的颜色
     */
    public void setTextColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    /**
     * 设置字体的大小
     *
     * @param textSize 指定的字体大小
     */
    public void setTextSize(float textSize) {
        mPaint.setTextSize(textSize);
    }

    /**
     * @param font 设置文字的字体
     */
    public void setTextFont(@NotNull Context context, @NotNull String font) {
        Typeface typeFace = TextViewUtil.getTypeFace(context, font);
        if (typeFace != null) {
            mPaint.setTypeface(typeFace);
        }
    }

    /**
     * @param lineHeight 设置文字中间间隔的高度
     */
    public void setLineHeight(float lineHeight) {
        this.mLineHeight = lineHeight;
    }

    /**
     * @param list 设置数据源
     */
    public void setList(List<String> list) {

        if ((list != null) && (list.size() > 0)) {
            this.mList.clear();

            // LogUtil.e("集合的数据为：" + list);
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (!TextUtils.isEmpty(s)) {
                    mList.add(s);

                    // 存入所有的基准线
                    float baseLine = CustomViewUtil.getBaseLine(mPaint, s);
                    mMapBaseLine.put(i, baseLine);

                    // 存入所有view的高度
                    float height = CustomViewUtil.getTextSize(mPaint, s)[1];
                    mMapContentHeight.put(i, height);
                }
            }

            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        int height = 0;// 默认0dp

        float leftHeight = 0; // 左侧的高度
        float rightHeight = 0; // 右侧的高度
        float contentHeight; // 左右最高的值

        float mTotalHeight = 0; // 累积的高度
        // 循环便利测量view的高度
        if (mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                if (!TextUtils.isEmpty(mList.get(i))) {
                    // 只计算左侧的高度进行累积就行了
                    if (i % 2 == 0) {
                        // 左侧的高度
                        leftHeight = mMapContentHeight.get(i);
                        if ((i + 1) < mMapContentHeight.size()) { // 普通的行数
                            rightHeight = mMapContentHeight.get(i + 1);
                        } else { // 最后一行
                            rightHeight = leftHeight;
                        }

                        if ((leftHeight - rightHeight) > 0) {
                            contentHeight = leftHeight;
                        } else {
                            contentHeight = rightHeight;
                        }

                        // 累积数据的高度
                        if (i < 2) {
                            mTotalHeight += contentHeight;
                        } else { // 加上分割线的高度
                            mTotalHeight += (contentHeight + mLineHeight);
                        }
                    }
                }
            }

            // 四舍五入数据，只能多不能少
            height = Math.round(mTotalHeight + (textSize / 4));
            //  LogUtil.e("|--->|:总的高度为：" + round);
            // 这里必须设置，如果不设置，就会 导致默认的宽高设置成最大的值
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 中心的分割线
        if (divider <= 0) {
            // 获取view的宽度
            int measuredWidth = getMeasuredWidth();
            // 获取分割线
            divider = measuredWidth / 2;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float mTotalHeight = 0; // 累积的高度
        float mTotalViewHeights = 0; // 累积view的高度
        float totalLine;// 最大的中心线

        if (mList.size() > 0) {
            // 循环便利数据
            for (int i = 0; i < mList.size(); i++) {
                // 每个具体的数据
                String s = mList.get(i);

                if (!TextUtils.isEmpty(s)) {
                    /*
                     *逻辑：
                     *      1：计算出view的基准线
                     *      2：累积加上 间隔高度 + view的高度 + 基准线
                     */

                    if (i % 2 == 0) { // 左侧的view
                        // 计算出中心线
                        totalLine = getLeftBaseLine(i);

                        // 绘制view
                        if (i < 2) { // 第一行
                            canvas.drawText(s, 0, totalLine, mPaint);

                            /*********************************************/
//                            Float aFloat = mMapContentHeight.get(i);
//                            Float aFloat1 = mMapContentHeight.get(i + 1);
//                            float aa;
//                            if (aFloat - aFloat1 > 0) {
//                                aa = aFloat;
//                            } else {
//                                aa = aFloat1;
//                            }
//                            canvas.drawLine(0, aa, getMeasuredWidth(), aa, mPaint2);
                            /*********************************************/

                        } else { // 后续的行

                            // ------- 计算出上一个view的高度 --------

                            // 对比出view的高度
                            float height = getLeftBeforeHeight(i);
                            // 累积view的高度
                            mTotalViewHeights += (height + mLineHeight);

                            // 高度 = 累积的高度  +中线的高度
                            mTotalHeight = mTotalViewHeights + totalLine;

                            canvas.drawText(s, 0, mTotalHeight, mPaint);

                            /*********************************************/
                            // 累积view的高度
//                            int measuredWidth = getMeasuredWidth();
//
//                            float leftCurrentHeight = getLeftCurrentHeight(i);
//
//                            float heightValue = leftCurrentHeight + mTotalViewHeights;
//
//                            canvas.drawLine(0, heightValue, measuredWidth, heightValue, mPaint2);
                            /*********************************************/

                        }
                    } else { // 右侧的view
                        if (i < 2) {
                            totalLine = getRightBaseLine(i);
                            canvas.drawText(s, divider, totalLine, mPaint);
                        } else {
                            canvas.drawText(s, divider, mTotalHeight, mPaint);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param position 角标
     * @return 获取左侧的一个中心线的高度
     */
    private float getLeftBaseLine(int position) {
        float rightLine;// 右侧的中心线

        // 计算出中心线
        float leftLien = mMapBaseLine.get(position); // 左侧的中心线
        if ((position + 1) < mMapBaseLine.size()) {
            rightLine = mMapBaseLine.get(position + 1);
        } else {
            rightLine = leftLien;
        }

        // 计算出最大的中心线
        if (leftLien - rightLine > 0) {
            return leftLien;
        } else {
            return rightLine;
        }
    }

    /**
     * @param position 角标
     * @return 获取左侧的一个中心线的高度
     */
    private float getRightBaseLine(int position) {

        // 计算出中心线
        float leftLien = mMapBaseLine.get(position);
        float rightLine = mMapBaseLine.get(position - 1);

        // 计算出最大的中心线
        if (leftLien - rightLine > 0) {
            return leftLien;
        } else {
            return rightLine;
        }
    }

    /**
     * @return 获取上一组左侧和右侧view的高度，对比出来最高的那个view
     */
    private float getLeftBeforeHeight(int position) {
        // 上一个左侧的view
        float leftHeight = mMapContentHeight.get(position - 2);
        // 上一个右侧的view
        float rightHeight = mMapContentHeight.get(position - 1);

        if (leftHeight - rightHeight > 0) {
            return leftHeight;
        } else {
            return rightHeight;
        }
    }

    /**
     * @return 获取上一组左侧和右侧view的高度，对比出来最高的那个view
     */
    private float getLeftCurrentHeight(int position) {
        float rightHeight;
        // 上一个左侧的view
        float leftHeight = mMapContentHeight.get(position);
        // 上一个右侧的view
        if ((position + 1) < mMapContentHeight.size()) {
            rightHeight = mMapContentHeight.get(position + 1);
        } else {
            rightHeight = leftHeight;
        }

        if (leftHeight - rightHeight > 0) {
            return leftHeight;
        } else {
            return rightHeight;
        }
    }

}

