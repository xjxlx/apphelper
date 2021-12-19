package com.android.helper.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by spc on 2017/4/13.
 */

public class TextViewUtil {

    private static AssetManager mgr;

    /**
     * 给TextView设置任意类型的数据
     *
     * @param textView textView对象
     * @param object   任意对象
     */
    public static void setText(TextView textView, Object object) {
        if (object != null) {
            setText(textView, String.valueOf(object));
        }
    }

    /**
     * 给TextView设置String类型的数据
     *
     * @param textView textView
     * @param value    具体的值
     */
    public static void setText(TextView textView, String value) {
        setText(textView, value, "");
    }

    public static void setText(TextView textView, Object object, Object defaultValue) {
        if (object != null && defaultValue != null) {
            setText(textView, String.valueOf(object), String.valueOf(defaultValue));
        }
    }

    public static void setText(TextView textView, String value, String defaultValue) {
        if (textView != null) {
            if (value != null) {
                textView.setText(value);
            } else {
                if (defaultValue != null) {
                    textView.setText(defaultValue);
                } else {
                    textView.setText("");
                }
            }
        }
    }

    /**
     * 给指定的textview设置指定的字体
     *
     * @param context  上下文
     * @param textView textview
     * @param fontName 字体的名字
     */
    public static void setTextFont(Context context, TextView textView, String fontName) {
        //得到AssetManager
        Typeface typeFace = getTypeFace(context, fontName);
        if (typeFace != null) {
            textView.setTypeface(typeFace);
        }
    }

    /**
     * @param context  context
     * @param fontName 字体的全路径名字，例如：FZY4K_GBK1_0.ttf
     * @return 获取字体的资源
     */
    public static Typeface getTypeFace(Context context, String fontName) {
        try {
            //得到AssetManager
            if (mgr == null) {
                mgr = context.getAssets();
            }
            if (mgr != null) {
                return Typeface.createFromAsset(mgr, fontName);
            }
        } catch (Exception ignored) {
            LogUtil.e("字体获取失败!");
        }
        return null;
    }

    /**
     * @param value 数据对象
     * @return 检测对象是否为空，true:为空，false:不为空
     */
    public static boolean isEmpty(CharSequence value) {
        boolean empty = false;
        empty = TextUtils.isEmpty(value);
        if (!empty) {
            if (value.equals("null")) {
                empty = true;
            } else if (value.equals("NULL")) {
                empty = true;
            } else if (value.equals("无")) {
                empty = true;
            }
        }
        return empty;
    }

}
