package com.android.helper.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;

/**
 * @author : 流星
 * @CreateDate: 2022/11/26-00:13
 * @Description:
 */
public class ShapeUtil {

    /**
     * @return 获取shape 的对象，可以对他进行各种的处理
     */
    public static GradientDrawable getShape(Drawable drawable) {
        GradientDrawable gradientDrawable = null;
        if (drawable != null) {
            if (drawable instanceof GradientDrawable) {
                gradientDrawable = (GradientDrawable) drawable;
            }
        }
        return gradientDrawable;
    }

    public static void setStroke(Drawable drawable, int width, @ColorInt int color) {
        GradientDrawable shape = getShape(drawable);
        if (shape != null) {
            shape.setStroke(width, color);
        }
    }

    public static void setColor(Drawable drawable, String color) {
        GradientDrawable shape = getShape(drawable);
        if (shape != null) {
            shape.setColor(Color.parseColor(color));
        }
    }

    public static void setColor(Drawable drawable, @ColorInt int color) {
        GradientDrawable shape = getShape(drawable);
        if (shape != null) {
            shape.setColor(color);
        }
    }
}
