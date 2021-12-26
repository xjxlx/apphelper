package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.android.helper.app.BaseApplication;

/**
 * 获取资源的工具类
 */
public class ResourceUtil {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext = BaseApplication.getInstance().getApplication();

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

    public static Bitmap getBitmap(@DrawableRes int id) {
        Bitmap bitmap = null;
        if (id != 0 && mContext != null) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), id);
            if (bitmap != null) {
                return bitmap;
            }
        }
        return bitmap;
    }

    /**
     * @param id dimens的id
     * @return 获取dimens的值
     */
    public static float getDimension(@DimenRes int id) {
        float dimension = 0.0f;
        Application application = BaseApplication.getInstance().getApplication();
        if (application != null) {
            Resources resources = application.getResources();
            if (resources != null) {
                dimension = resources.getDimension(id);
                return dimension;
            }
        }
        return dimension;
    }

}
