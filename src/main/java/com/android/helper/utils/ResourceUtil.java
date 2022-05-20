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
import androidx.core.content.res.ResourcesCompat;

import com.android.helper.R;
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
        try {
            if (id != 0 && mContext != null) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), id);
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (Exception e) {
            LogUtil.e("获取Bitmap失败！");
        }
        return bitmap;
    }

    /**
     * @param id dimens的id
     * @return 获取dimens的值
     */
    public static int getDimension(@DimenRes int id) {
        int dimension = 0;
        try {
            Application application = BaseApplication.getInstance().getApplication();
            if (application != null) {
                Resources resources = application.getResources();
                if (resources != null) {
                    // 获取资源文件中定义的dimension值
                    dimension = resources.getDimensionPixelSize(id);
                    return dimension;
                }
            }
        } catch (Exception e) {
            LogUtil.e("获取dimens失败！");
        }
        return dimension;
    }

    public static Drawable getShapeDrawable(int shapeId) {
        Drawable drawableShape = null;
        try {
            Application application = BaseApplication.getInstance().getApplication();
            if (application != null) {
                Resources resources = application.getResources();
                if (resources != null) {
                    // 获取资源文件中定义的dimension值
                    drawableShape = ResourcesCompat.getDrawable(resources, shapeId, application.getTheme());
                }
            }
        } catch (Exception e) {
            LogUtil.e("获取dimens失败！");
        }
        return drawableShape;
    }

}
