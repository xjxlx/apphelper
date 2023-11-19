package com.android.helper.utils;

public class ColorUtil {

    /**
     * @param fraction   过渡色的比例
     * @param startValue 开始的颜色
     * @param endValue   结束的颜色
     * @return 根据一个比例，把一个颜色在这个规定的比例范围内，过度成另外一个颜色
     */
    public static Object evaluateColor(float fraction, Object startValue,
                                       Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;
        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;
        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | (startB + (int) (fraction * (endB - startB)));
    }

    /**
     * @param fraction   过渡色的比例
     * @param startValue 开始的颜色
     * @param endValue   结束的颜色
     * @return 根据一个比例，把一个颜色在这个规定的比例范围内，过度成另外一个颜色
     */
    public static int evaluateColor(float fraction, int startValue, int endValue) {
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;
        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;
        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | (startB + (int) (fraction * (endB - startB)));
    }
}
