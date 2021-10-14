package com.android.helper.utils.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.helper.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

/**
 * 图片加载的工具类
 */
public class GlideUtil {

    private static final String TAG = "GlideUtil";

    private GlideUtil() {
    }

    /**
     * @return 是否能正常的加载view true:可以使用，false：不可使用
     */
    private static boolean checkParameter(@NotNull Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return checkParameter(activity);
        } else {
            return true;
        }
    }

    /**
     * @return 是否能正常的加载view true:可以使用，false：不可使用
     */
    private static boolean checkParameter(@NotNull Activity activity) {
        if ((activity.isFinishing()) || (activity.isDestroyed())) {
            LogUtil.e(TAG, "传入的参数异常，请检查参数！");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 加载普通的view
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull ImageView imageView) {
        loadView(activity, url, imageView, 0);
    }

    /**
     * 加载普通的view
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Context context, @NotNull String url, @NotNull ImageView imageView) {
        loadView(context, url, imageView, 0);
    }

    /**
     * @param placeResourceId 加载成功前显示的图片
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Context context, @NotNull String url, @NotNull ImageView imageView, @DrawableRes int placeResourceId) {
        boolean checkParameter = checkParameter(context);

        if (checkParameter) {
            RequestOptions options = new RequestOptions();
            if (placeResourceId != 0) {
                options.placeholder(placeResourceId);
            }

            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(imageView);
        }
    }

    /**
     * @param placeResourceId 加载成功前显示的图片
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull ImageView imageView, @DrawableRes int placeResourceId) {
        boolean checkParameter = checkParameter(activity);

        if (checkParameter) {
            RequestOptions options = new RequestOptions();
            if (placeResourceId != 0) {
                options.placeholder(placeResourceId);
                options.error(placeResourceId);
                options.fallback(placeResourceId);
            }

            Glide.with(activity)
                    .load(url)
                    .apply(options)
                    .into(imageView);
        }
    }

    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull View view) {
        loadView(activity, url, view, 0);
    }

    /**
     * @param placeResourceId 加载成功前显示的图片
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull View view, @DrawableRes int placeResourceId) {

        boolean checkParameter = checkParameter(activity);
        if (checkParameter) {

            RequestOptions options = new RequestOptions();
            if (placeResourceId != 0) {
                options.placeholder(placeResourceId);
            }

            Glide.with(activity)
                    .load(url)
                    .apply(options)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            view.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    public static void loadViewCenterCrop(Context context, ImageView view, String url) {
        if ((context != null) && (view != null) && (!TextUtils.isEmpty(url)))
            loadViewCenterCrop(context, view, url, 0);
    }

    @SuppressLint("CheckResult")
    public static void loadViewCenterCrop(Context context, ImageView view, String url, int placeResourceId) {
        if ((context != null) && (view != null) && (!TextUtils.isEmpty(url))) {
            RequestOptions options = new RequestOptions();
            if (placeResourceId != 0) {
                options.placeholder(placeResourceId);
            }

            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .into(view);
        }
    }
}
