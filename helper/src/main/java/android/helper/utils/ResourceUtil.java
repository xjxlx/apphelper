package android.helper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.helper.app.BaseApplication;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

/**
 * 获取资源的工具类
 */
public class ResourceUtil {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext = BaseApplication.getContext();

    public static int getColor(@ColorRes int id) {
        int color = 0;

        if (id != 0 && mContext != null) {
            color = ContextCompat.getColor(mContext, id);
        }
        return color;
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        Drawable drawable = null;
        if (id != 0 && mContext != null) {
            drawable = ContextCompat.getDrawable(mContext, id);
        }
        return drawable;
    }

}
