package com.android.helper.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import com.android.common.utils.ToastUtil;
import com.android.helper.R;

/**
 * @author : 流星
 * @CreateDate: 2021/12/9-2:10 下午
 * @Description: 购物车的计算机
 */
public class ShoppingNumber extends FrameLayout {

    private TextView mTvMinusSign;  // 减号
    private TextView mTvNumber;     // 数量
    private TextView mTvPlusSign;   // 加号
    private View mInflate;
    private boolean mAutoChange = true; // 点击加减号的时候，自动变化数量
    private boolean isHint;        // 是否主动提示，如果使用了这个控制，则必须去设置提示的内容
    private String mMinNumberHint; // 数量最少的提示
    private String mMaxNumberHint; // 数量最多的提示
    private int mMinNumber = 1; // 最少的数量
    private int mMaxNumber = 1; // 最少的数量
    private ImageView mIvMinusSign; // 图片减号
    private ImageView mIvPlusSign;  // 图片加号
    private FrameLayout mFlMinusSign;
    private FrameLayout mFlPlusSign;

    public ShoppingNumber(Context context) {
        super(context);
        initView(context, null);
    }

    public ShoppingNumber(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        mInflate = LayoutInflater.from(context).inflate(R.layout.custom_shopping_number, null, false);
        // 文字
        mTvMinusSign = mInflate.findViewById(R.id.tv_minus_sign);
        mTvNumber = mInflate.findViewById(R.id.tv_number);
        mTvPlusSign = mInflate.findViewById(R.id.tv_plus_sign);
        // 图片
        mIvMinusSign = mInflate.findViewById(R.id.iv_minus_sign);
        mIvPlusSign = mInflate.findViewById(R.id.iv_plus_sign);
        // 减号 减号的父布局
        mFlMinusSign = mInflate.findViewById(R.id.fl_minus_sign);
        mFlPlusSign = mInflate.findViewById(R.id.fl_plus_sign);
        addView(mInflate);
    }

    /**
     * 设置背景
     */
    public void setBackground(@DrawableRes int resId) {
        if (mInflate != null) {
            mInflate.setBackgroundResource(resId);
        }
    }

    public boolean isHint() {
        return isHint;
    }

    public void setHint(boolean hint) {
        isHint = hint;
    }

    /**
     * 设置减号的样式
     *
     * @param color 颜色
     * @param size  大小
     */
    public void setMinusSignStyle(@ColorInt int color, float size) {
        if (mTvMinusSign != null) {
            // 隐藏图片
            if (mIvMinusSign.getVisibility() != View.GONE) {
                mIvMinusSign.setVisibility(View.GONE);
            }
            // 展示文字
            if (mTvMinusSign.getVisibility() != View.VISIBLE) {
                mTvMinusSign.setVisibility(View.VISIBLE);
            }
            if (color > 0) {
                mTvMinusSign.setTextColor(color);
            }
            if (size > 0) {
                mTvMinusSign.setTextSize(size);
            }
        }
    }

    /**
     * 设置加号的样式
     *
     * @param color 颜色
     * @param size  大小
     */
    public void setPlusSignStyle(@ColorInt int color, float size) {
        if (mTvPlusSign != null) {
            // 隐藏图片
            if (mTvPlusSign.getVisibility() != View.GONE) {
                mTvPlusSign.setVisibility(View.GONE);
            }
            // 展示文字
            if (mIvPlusSign.getVisibility() != View.VISIBLE) {
                mIvPlusSign.setVisibility(View.VISIBLE);
            }
            if (color > 0) {
                mTvPlusSign.setTextColor(color);
            }
            if (size > 0) {
                mTvPlusSign.setTextSize(size);
            }
        }
    }

    /**
     * 设置中间数量的样式
     *
     * @param color 颜色
     * @param size  大小
     */
    public void setNumberStyle(@ColorInt int color, float size) {
        if (mTvNumber != null) {
            if (color > 0) {
                mTvNumber.setTextColor(color);
            }
            if (size > 0) {
                mTvNumber.setTextSize(size);
            }
        }
    }

    /**
     * 设置中间数量的改变
     */
    public void setNumber(String number) {
        if ((mTvNumber != null) && (!TextUtils.isEmpty(number))) {
            mTvNumber.setText(number);
        }
    }

    /**
     * 在点击左右按钮的时候，是否进行中间数字的自动加减
     */
    public void setAutoChange(boolean autoChange) {
        this.mAutoChange = autoChange;
    }

    /**
     * @param minHint 数量最少的提示
     */
    public void setMinNumberHint(int minNumber, String minHint) {
        this.mMinNumber = minNumber;
        this.mMinNumberHint = minHint;
    }

    /**
     * @param maxHint 数量最多的提示
     */
    public void setMaxNumberHint(int maxNumber, String maxHint) {
        this.mMaxNumber = maxNumber;
        this.mMaxNumberHint = maxHint;
    }

    /**
     * @param listener 设置减号的点击事件
     */
    public void setMinusSignClick(OnClickListener listener) {
        if (mFlMinusSign != null) {
            mFlMinusSign.setOnClickListener(v -> {
                try {
                    // 获取当前的数据
                    String number = mTvNumber.getText().toString();
                    int numberValue = Integer.parseInt(number);
                    // 如果当前数据小于指定的最小值
                    if (numberValue <= mMinNumber) {
                        // 如果提示则进行数据的校验，默认不提示
                        if (isHint()) {
                            if (!TextUtils.isEmpty(mMinNumberHint)) {
                                ToastUtil.show(mMinNumberHint);
                            }
                        } else {
                            // 如果不提示的话，就继续点击事件的处理
                            if (listener != null) {
                                listener.onClick(v);
                            }
                        }
                    } else {
                        // 自动变化数据
                        if (mAutoChange) {
                            numberValue--;
                            mTvNumber.setText(String.valueOf(numberValue));
                        }
                        if (listener != null) {
                            listener.onClick(v);
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }

    /**
     * 设置加号的点击事件
     */
    public void setPlusSignClick(OnClickListener listener) {
        if (mFlPlusSign != null) {
            mFlPlusSign.setOnClickListener(v -> {
                try {
                    // 转换当前值
                    String number = mTvNumber.getText().toString();
                    int numberValue = Integer.parseInt(number);
                    // 当前值大于最大值
                    if (numberValue >= mMaxNumber) {
                        if (isHint()) {
                            // 大于这个数量，则进行提示
                            if (!TextUtils.isEmpty(mMaxNumberHint)) {
                                ToastUtil.show(mMaxNumberHint);
                            }
                        } else {
                            if (listener != null) {
                                listener.onClick(v);
                            }
                        }
                    } else {
                        if (mAutoChange) {
                            numberValue++;
                            mTvNumber.setText(String.valueOf(numberValue));
                        }
                        if (listener != null) {
                            listener.onClick(v);
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }

    /**
     * 设置减号的资源
     */
    public void setMinusSignResource(int resId) {
        if (mTvMinusSign != null && resId > 0 && mIvMinusSign != null) {
            // 文字的隐藏
            if (mTvMinusSign.getVisibility() != View.GONE) {
                mTvMinusSign.setVisibility(GONE);
            }
            // 文字的显示
            if (mIvMinusSign.getVisibility() != View.VISIBLE) {
                mIvMinusSign.setVisibility(VISIBLE);
            }
            mIvMinusSign.setImageResource(resId);
        }
    }

    /**
     * 设置加号的资源
     */
    public void setPlusSignResource(int resId) {
        if (mTvPlusSign != null && resId > 0 && mIvPlusSign != null) {
            // 文字的隐藏
            if (mTvPlusSign.getVisibility() != View.GONE) {
                mTvPlusSign.setVisibility(GONE);
            }
            // 文字的显示
            if (mIvPlusSign.getVisibility() != View.VISIBLE) {
                mIvPlusSign.setVisibility(VISIBLE);
            }
            mIvPlusSign.setImageResource(resId);
        }
    }

    /**
     * @return 获取减号的父布局，用于点击
     */
    public View getMinusSign() {
        return mFlMinusSign;
    }

    /**
     * @return 获取加号的布局，用于点击
     */
    public View getPlusSign() {
        return mFlPlusSign;
    }

}
