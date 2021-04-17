package com.android.helper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

/**
 * bitmap的工具类
 */
public class BitmapUtil {

    /**
     * @param context context
     * @param id      资源的id
     * @return 通过id的值，返回一个bitmap的对象
     */
    public static Bitmap getBitmapForResourceId(Context context, int id) {
        Bitmap bitmap = null;
        try {
            if (context != null && id != 0) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            }
        } catch (Exception ignored) {
        }
        return bitmap;
    }

    /**
     * @return 根据bitmap 生成一个缩放的bitmap
     */
    public static Bitmap getScaleBitmap(@NotNull Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap bitmapResult = null;

        if ((newWidth > 0) && (newHeight > 0)) {

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            Matrix matrix = new Matrix();

            // 求出缩放的比例
            float scaleWidth = (float) newWidth / bitmapWidth;
            float scaleHeight = (float) newHeight / bitmapHeight;

            // 使用最小的缩放比例，避免变形
            matrix.postScale(scaleWidth, scaleHeight);

            bitmapResult = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);

            if (!bitmap.equals(bitmapResult) && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return bitmapResult;// Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * @return 获取ImageView设置的图片内容
     */
    public static Bitmap getBitmapForImageView(ImageView imageView) {
        Bitmap bitmap = null;
        if (imageView == null) {
            return null;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof ColorDrawable) {
                // 获取view的宽高
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                int color = ((ColorDrawable) drawable).getColor();
                c.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            }
            if (drawable instanceof StateListDrawable) {
                Drawable current = drawable.getCurrent();
                if (current instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) current).getBitmap();
                }
            }
            // 获取应该还会有其他类型的图片需要处理，待定
        }
        return bitmap;
    }

    /**
     * @return 获取ImageView设置的图片内容
     */
    public static Bitmap getBitmapForDrawable(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof ColorDrawable) {
                // 获取view的宽高
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                int color = ((ColorDrawable) drawable).getColor();
                c.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            }
            if (drawable instanceof StateListDrawable) {
                Drawable current = drawable.getCurrent();
                if (current instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) current).getBitmap();
                }
            }
            // 获取应该还会有其他类型的图片需要处理，待定
        }
        return bitmap;
    }

    public static Bitmap getBitmapScaleWidth(Drawable drawable, float height) {
        Bitmap bitmap = null;
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof StateListDrawable) {
            Drawable current = drawable.getCurrent();
            if (current instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) current).getBitmap();
            }
        } else if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        if (bitmap != null) {

            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();

            float scaleHeight = height / bmpHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleHeight, scaleHeight);

            return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
        } else {
            return null;
        }
    }

    /**
     * @param bitmap       原来的bitmap
     * @param targetWidth  目标的宽度
     * @param targetHeight 目标的高度
     * @return 把bitmap转换成一个适合指定宽高尺寸的缩放bitmap
     */
    public static Bitmap getBitmapForMatrixScale(Bitmap bitmap, int targetWidth, int targetHeight) {
        Bitmap bitmapResult = null;
        try {
            if ((bitmap != null) && (targetWidth != 0) && (targetHeight != 0)) {
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();

                // 求出目标宽度和图片宽度的比值，这里需要确定的是目标的比例，所以使用目标除以图片
                float scaleWidth = (float) targetWidth / bitmapWidth;
                float scaleHeight = (float) targetHeight / bitmapHeight;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                bitmapResult = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            }
        } catch (Exception ignored) {
            LogUtil.e("图像缩放失败！");
        }
        return bitmapResult;
    }

}
