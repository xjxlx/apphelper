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

    }
}
