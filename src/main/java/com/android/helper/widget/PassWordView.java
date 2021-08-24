package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.utils.ConvertUtil;
import com.android.helper.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义密码输入框
 */
public class PassWordView extends ViewGroup {

    private int measuredWidth;
    private int measuredHeight;
    private Paint mPaint_unSelector;
    private Paint mPaint_selector;
    private Paint mPaint_text;
    private Paint mPaint_drawable;

    private float childWidth;
    private float mStartX = 0.0f; // 开始位置
    private float mStopX = 0.0f; // 结束位置
    private EditText editText;
    private List<String> mList = new ArrayList<>();// 用来存储数据
    private int size; // 设置的数量
    private float padding;
    private int mUnSelectorColor;
    private int mSelectorColor;
    private float mUnSelectorHeight;
    private float mSelectorHeight;
    private int mDrawable_color;
    private int mContentColor;
    private float mContentSize;
    private boolean mShowPassWord;
    private float mDrawableSize;
    private boolean mShowInput;
    private int mErrorColor;
    private boolean mIsError;

    public PassWordView(Context context) {
        super(context);
        initView(context, null);
    }

    public PassWordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (editText != null) {
            editText.layout(0, 0, measuredWidth, measuredHeight);
        }
    }

    void initView(Context context, @Nullable AttributeSet attrs) {

        setWillNotDraw(false);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PassWordView);
        // 输入密码的个数
        size = array.getInteger(R.styleable.PassWordView_pd_size, 4);
        // 获取每个view中间的间隔距离
        padding = array.getDimension(R.styleable.PassWordView_pd_HorizontalSpacing, ConvertUtil.toDp(10));
        // 没有选中的颜色
        mUnSelectorColor = array.getColor(R.styleable.PassWordView_pd_un_selector_color, Color.parseColor("#BEC4CC"));
        // 没有选中的高度
        mUnSelectorHeight = array.getDimension(R.styleable.PassWordView_pd_un_selector_height, ConvertUtil.toDp(1));
        // 选中文字的颜色
        mSelectorColor = array.getColor(R.styleable.PassWordView_pd_selector_color, Color.parseColor("#3E485A"));
        // 选中背景的高度
        mSelectorHeight = array.getDimension(R.styleable.PassWordView_pd_selector_height, ConvertUtil.toDp(2));
        // 展示的内容
        mDrawable_color = array.getColor(R.styleable.PassWordView_pd_drawable_color, Color.parseColor("#171B21"));
        // 密文的半径
        mDrawableSize = array.getDimension(R.styleable.PassWordView_pd_drawable_size, ConvertUtil.toDp(4.5F));
        //明文的信息
        mContentColor = array.getColor(R.styleable.PassWordView_pd_content_color, Color.parseColor("#171B21"));
        mContentSize = array.getDimension(R.styleable.PassWordView_pd_content_size, ConvertUtil.toDp(16));

        // 是否显示密码，默认为true
        mShowPassWord = array.getBoolean(R.styleable.PassWordView_pd_show_password, true);
        // 是否可以展示输入框
        mShowInput = array.getBoolean(R.styleable.PassWordView_pd_show_input, true);
        // 错误颜色的color
        mErrorColor = array.getColor(R.styleable.PassWordView_pd_selector_error_color, Color.parseColor("#B60004"));

        // 释放对象
        array.recycle();

        mPaint_unSelector = new Paint();
        mPaint_unSelector.setColor(mUnSelectorColor);
        mPaint_unSelector.setStrokeWidth(mUnSelectorHeight);

        mPaint_selector = new Paint();
        mPaint_selector.setColor(mSelectorColor);
        mPaint_selector.setStrokeWidth(mSelectorHeight);

        mPaint_text = new Paint();
        mPaint_text.setTextSize(mContentSize);
        mPaint_text.setColor(mContentColor);

        mPaint_drawable = new Paint();
        mPaint_drawable.setStyle(Paint.Style.FILL);
        mPaint_drawable.setAntiAlias(true);

        if (mShowInput) {

            editText = new EditText(context);
            // 去掉背景
            editText.setBackground(null);
            // 限制数据类型
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setCursorVisible(false);//隐藏光标

            // 获取输入内容
            InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
                // 限制输入长度
                if ((!TextUtils.isEmpty(source)) && (mList.size() < size)) {
                    mList.add(source + "");

                    // 重新刷新布局
                    invalidate();
                }
                return "";
            };
            // 设置输入的最大长度
            editText.setFilters(new InputFilter[]{inputFilter});

            // EditText删除的监听
            editText.setOnKeyListener((v, keyCode, event) -> {
                if ((keyCode == KeyEvent.KEYCODE_DEL) && (event.getAction() == KeyEvent.ACTION_UP)) {
                    int position = mList.size() - 1;
                    if (position >= 0) {
                        mList.remove(position);

                        // 重置正确和错误的状态
                        if (mIsError) {
                            setErrorBackGround(false);
                        }
                        LogUtil.e("mList:" + mList);
                        // 刷新布局
                        invalidate();
                        return true;
                    }
                }
                return false;
            });

            addView(editText);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 测量出控件的宽高
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();

        // 每隔view的宽度
        childWidth = (measuredWidth - (padding * (size - 1))) / size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i <= size; i++) {
            mStartX = (childWidth * i) + (padding * i);
            mStopX = (childWidth * (i + 1)) + (padding * i);
            // 绘制背景颜色
            canvas.drawLine(mStartX, measuredHeight, mStopX, measuredHeight, mPaint_unSelector);        //绘制直线
        }

        if (mList.size() > 0) {
            // 货值文字和选中的背景色
            for (int j = 0; j < mList.size(); j++) {

                mStartX = (childWidth * j) + (padding * j);
                mStopX = (childWidth * (j + 1)) + (padding * j);
                if (mIsError) {
                    mPaint_selector.setColor(mErrorColor);
                } else {
                    mPaint_selector.setColor(mSelectorColor);
                }
                // 绘制选中的颜色
                canvas.drawLine(mStartX, measuredHeight, mStopX, measuredHeight, mPaint_selector);        //绘制直线

                // 是否展示密码
                if (mShowPassWord) {
                    // 绘制文字
                    String s = mList.get(j);
                    @SuppressLint("DrawAllocation")
                    Rect rect = new Rect();
                    mPaint_text.getTextBounds(s, 0, s.length(), rect);
                    // 求出textView的宽高
                    int width = rect.width();
                    int height = rect.height();

                    // 求出每个text在中间的位置
                    float sta = (childWidth - width) / 2;
                    // 设置text文字  x:表示左侧的X轴位置，y:表示基准线的位置，就是文字底部的位置
                    int y = (measuredHeight / 2) + (height / 2);
                    canvas.drawText(s, (mStartX + sta), y, mPaint_text);
                } else {
                    // 绘制密文形式
                    for (int i = 0; i < mList.size(); i++) {
                        // 计算开始的X轴坐标
                        float startX = (childWidth / 2) + (childWidth * i) + (padding * i);

                        canvas.drawCircle(startX, measuredHeight / 2, mDrawableSize, mPaint_drawable);
                    }
                }
            }
        }
    }

    /**
     * 设置显示错误布局
     *
     * @param isError true：显示错误的布局
     */
    public void setErrorBackGround(boolean isError) {
        mIsError = isError;
    }

    /**
     * 直接设置数据
     */
    public void setList(List<String> list) {
        if (list.size() <= size) {
            this.mList = list;
            invalidate();
        }
    }

    public List<String> getListData() {
        return mList;
    }

    public int getSize() {
        return size;
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        return null;
    }
}
