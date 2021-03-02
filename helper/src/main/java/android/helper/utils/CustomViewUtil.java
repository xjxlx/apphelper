package android.helper.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

public class CustomViewUtil {
    
    /**
     * @param paint   画笔
     * @param content 内容
     * @return 根据画笔和内容返回baseLine的基线
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
    
        float [] ints = new float[2];
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        ints[0] = rect.width();
        ints[1] = rect.height();
        return ints;
    }
    
}
