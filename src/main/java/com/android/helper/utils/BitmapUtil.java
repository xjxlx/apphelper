package com.android.helper.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.listener.CallBackListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.tools.ScreenUtils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * bitmap的工具类
 */
public class BitmapUtil {

    public static final String STATUS_START = "start";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error:";

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
     * @param bitmap       原来的bitmap
     * @param targetWidth  目标的宽度
     * @param targetHeight 目标的高度
     * @return 把bitmap转换成一个适合指定宽高尺寸的缩放bitmap
     */
    public static Bitmap getBitmapForScale(Bitmap bitmap, int targetWidth, int targetHeight) {
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

    /**
     * @param bitmap      原来的bitmap
     * @param targetWidth 指定bitmap的宽度
     * @return 根据一个原有的bitmap，缩放其宽度，让高度也跟着宽度的缩放而缩放，生成一个新的高度，这种情况是为了避免输入随意的数字，导致图片缩放的时候回变形
     */
    public static Bitmap getBitmapScaleForWidth(Bitmap bitmap, float targetWidth) {
        Bitmap resultBitmap;
        if (bitmap != null && targetWidth > 0) {
            try {
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();

                // 求出目标宽度和图片宽度的比值，这里需要确定的是目标的比例，所以使用目标除以图片
                float scaleWidth = targetWidth / bitmapWidth;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);

                resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
                return resultBitmap;
            } catch (Exception ignored) {
                LogUtil.e("图像缩放失败！");
            }
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
     * @param context          上下文
     * @param path             图片路径
     * @param callBackListener 返回接口，msg: 开始：start，成功： success，失败：error:
     */
    public static void getBitmapForService(Context context, final String path, CallBackListener<Bitmap> callBackListener) {
        Flowable.create(new FlowableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Bitmap> emitter) throws Exception {
                Glide.with(context)
                        .load(path)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@androidx.annotation.NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (resource instanceof BitmapDrawable) {
                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                    if (bitmap != null) {
                                        emitter.onNext(bitmap);
                                    } else {
                                        emitter.onError(new Exception("获取的bitmap为空"));
                                    }
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                emitter.onError(new Exception("获取的bitmap异常"));
                            }
                        });
            }
        }, BackpressureStrategy.LATEST)
                .compose(RxUtil.getSchedulerFlowable())
                .subscribe(new DisposableSubscriber<Bitmap>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        if (callBackListener != null) {
                            callBackListener.onBack(false, STATUS_START, null);
                        }
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        if (callBackListener != null) {
                            callBackListener.onBack(true, STATUS_SUCCESS, bitmap);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (callBackListener != null) {
                            callBackListener.onBack(false, STATUS_ERROR + t.getMessage(), null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * @param context          上下文
     * @param path             网络路径
     * @param callBackListener 数据回调的接口
     */
    public static void getDrawableForService(Context context, final String path, CallBackListener<Drawable> callBackListener) {
        Flowable.create(new FlowableOnSubscribe<Drawable>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Drawable> emitter) throws Exception {
                Glide.with(context)
                        .load(path)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@androidx.annotation.NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                emitter.onNext(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                emitter.onError(new Exception("获取的bitmap异常"));
                            }
                        });
            }
        }, BackpressureStrategy.LATEST)
                .compose(RxUtil.getSchedulerFlowable())
                .subscribe(new DisposableSubscriber<Drawable>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        if (callBackListener != null) {
                            callBackListener.onBack(false, STATUS_START, null);
                        }
                    }

                    @Override
                    public void onNext(Drawable drawable) {
                        if (callBackListener != null) {
                            callBackListener.onBack(true, STATUS_SUCCESS, drawable);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (callBackListener != null) {
                            callBackListener.onBack(false, STATUS_ERROR + t.getMessage(), null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 加载本地大图片
     */
    public static void LoadMorePhoto(Context context, int id, ViewGroup viewGroup) {
        // 获取屏幕宽高
        int screenWidth = ScreenUtils.getScreenWidth(context);
        int screenHeight = ScreenUtils.getScreenHeight(context);

        Resources resources = context.getResources();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        //请求图片属性但不申请内存
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, opts);
        // 获取图片宽高
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;

        // 图片的宽高除以屏幕宽高，算出宽和高的缩放比例，取较大值作为图片的缩放比例
        int scale = 1;
        int scaleX = imageWidth / screenWidth;
        int scaleY = imageHeight / screenHeight;
        if (scaleX >= scaleY && scaleX > 1) {
            scale = scaleX;
        } else if (scaleY > scaleX && scaleY > 1) {
            scale = scaleY;
        }

        // * 按缩放比例加载图片
        //设置缩放比例
        opts.inSampleSize = scale;
        //为图片申请内存
        opts.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeResource(resources, id, opts);

        Drawable drawable = new BitmapDrawable(resources, bm);

        viewGroup.setBackground(drawable);
    }

}
