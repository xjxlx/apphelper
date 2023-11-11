package com.android.helper.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.android.common.utils.LogUtil;
import com.android.helper.BuildConfig;

/**
 * 基类的View,以后所有的view都集成他，避免重写很多的袋面
 */
public abstract class BaseView extends View {

    public String Tag;

    public BaseView(Context context) {
        super(context);
        Tag = getClass().getSimpleName();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Tag = getClass().getSimpleName();
    }

    /**
     * 初始化view
     *
     * @param context 上下文
     * @param attrs   xml的属性
     */
    public void initAttributeSet(Context context, AttributeSet attrs) {
        //        if (attrs != null) {
        //            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundView);
        //            // 圆角的类型  1:圆形  2：圆角
        //            mRoundType = typedArray.getInt(R.styleable.RoundView_rv_roundType, 0);
        //            // 圆角的角度 1：四个角全用 2：左上角 3：右上角  4：左下角  5：右下角
        //            mRoundAngle = typedArray.getInt(R.styleable.RoundView_rv_angle, 0);
        //            // 圆角的度数
        //            mRoundRadius = typedArray.getDimension(R.styleable.RoundView_rv_radius, 0);
        //            // 是否有站位图
        //            mRoundPlaceholder = typedArray.getBoolean(R.styleable.RoundView_rv_placeholder, false);
        //
        //            typedArray.recycle();
        //        }
    }

    protected abstract void initView(Context context, AttributeSet attrs);

    /**
     * 该方法只负责测量，不参与和测量无关的逻辑
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 该方法只负责绘制，其中的动态逻辑要放到此处去处理
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    /**
     * @return 获取图片
     */
    public Bitmap getBitmap(Context context, @DrawableRes int id) {
        Bitmap bitmap = null;
        if (context != null && id != 0) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        }
        return bitmap;
    }

    /**
     * 打印日志
     *
     * @param value 具体内容
     */
    public void log(String value) {
        if (BuildConfig.DEBUG) {
            if (!TextUtils.isEmpty(value)) {
                LogUtil.e(value);
            }
        }
    }
}
