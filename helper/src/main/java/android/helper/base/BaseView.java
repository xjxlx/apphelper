package android.helper.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 基类的View,以后所有的view都集成他，避免重写很多的袋面
 */
public class BaseView extends View {

    public BaseView(Context context) {
        super(context);
        initView(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * 初始化view
     *
     * @param context 上下文
     * @param attrs   xml的属性
     */
    public void initView(Context context, AttributeSet attrs) {
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
}
