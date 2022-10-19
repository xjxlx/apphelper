package com.android.helper.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

public class CustomViewUtil {

    /**
     * @param paint   画笔
     * @param content 内容
     * @return 根据画笔和内容返回baseLine的基线, 适用于view写在开始的位置
     */
    public static float getBaseLine(Paint paint, String content) {
        if (paint == null || (TextUtils.isEmpty(content))) {
            return 0f;
        }
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return (float) Math.abs(rect.top);
    }

    /**
     * @param paint   画笔
     * @param content 文字内容
     * @return 根据画笔和文字去获取text的宽高  【0】：宽  【1】：高
     */
    public static float[] getTextSize(Paint paint, String content) {
        if (paint == null || (TextUtils.isEmpty(content))) {
            return null;
        }

        float[] ints = new float[2];
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        ints[0] = rect.width();
        ints[1] = rect.height();
        return ints;
    }

    /**
     * @param paint   画笔
     * @param content 内容
     * @return 返回测量文字的宽度
     */
    public static float getTextViewWidth(Paint paint, String content) {
        if (paint == null || TextUtils.isEmpty(content)) {
            return 0;
        }
        return paint.measureText(content, 0, content.length());
    }

    /**
     * @param paint   画笔
     * @param content 内容
     * @return 获取文字的高度
     */
    public static float getTextHeight(Paint paint, String content) {
        if ((paint != null) && (!TextUtils.isEmpty(content))) {
            Rect rect = new Rect();
            paint.getTextBounds(content, 0, content.length(), rect);
            return rect.height();
        }
        return 0;
    }

    /**
     * @param paint   画笔
     * @param content 内容
     * @return 获取文字的宽度
     */
    public static float getTextWidth(Paint paint, String content) {
        if ((paint != null) && (!TextUtils.isEmpty(content))) {
            Rect rect = new Rect();
            paint.getTextBounds(content, 0, content.length(), rect);
            return rect.width();
        }
        return 0;
    }

    /**
     * @param paint   画笔
     * @param rect    写入位置的大小
     * @param content 内容
     * @return 返回一个居中显示的baseLine ,适用于在一个区域中显示在最中心的情况
     */
    public static float getBaseLienCenter(Paint paint, Rect rect, String content) {
        if ((paint != null) && (!TextUtils.isEmpty(content)) && rect != null) {
            //计算baseline
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            return rect.centerY() + distance;
        }
        return 0;
    }

    /***
     *
     * @param paint 画笔
     * @return 返回drawText 的基线位置
     */
    public static float getBaseLine2(Paint paint, Rect rect) {
        /*
         * 距离 = 文字高度的一半 - 基线到文字底部的距离（也就是bottom）
         */
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseLine = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;

        return baseLine + rect.centerY();
    }

}
