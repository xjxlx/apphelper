package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.android.helper.app.BaseApplication;

/**
 * 获取资源的工具类
 */
public class ResourceUtil {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext = BaseApplication.getApplication();

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

}
