package android.helper.utils.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import android.helper.utils.LogUtil;

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
    private static boolean checkParameter(@NotNull Activity activity, @NotNull String url) {
        if (activity.isFinishing() || (activity.isDestroyed()) || TextUtils.isEmpty(url)) {
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
        boolean checkParameter = checkParameter(activity, url);
        if (checkParameter) {
            Glide.with(activity)
                    .load(url)
                    .into(imageView);
        }
    }

    /**
     * @param placeResourceId 加载成功前显示的图片
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull ImageView imageView,
                                @DrawableRes int placeResourceId) {
        boolean checkParameter = checkParameter(activity, url);
        if (checkParameter) {
            Glide
                    .with(activity)
                    .load(url)
                    .placeholder(placeResourceId)
                    .into(imageView);
        }
    }

    /**
     * @param placeResourceId 加载成功前显示的图片
     * @param errorResourceId url为空，或者加载错误时候,显示的图片
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull ImageView imageView,
                                @DrawableRes int placeResourceId, @DrawableRes int errorResourceId) {
        boolean checkParameter = checkParameter(activity, url);
        if (checkParameter) {
            Glide
                    .with(activity)
                    .load(url)
                    .placeholder(placeResourceId)
                    .error(errorResourceId)
                    .fallback(errorResourceId)
                    .into(imageView);
        }
    }

    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull View view) {
        boolean checkParameter = checkParameter(activity, url);

        if (checkParameter) {
            Glide
                    .with(activity)
                    .load(url)
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

    /**
     * @param placeResourceId 加载成功前显示的图片
     */
    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull View view,
                                @DrawableRes int placeResourceId) {
        boolean checkParameter = checkParameter(activity, url);
        if (checkParameter) {
            Glide
                    .with(activity)
                    .load(url)
                    .placeholder(placeResourceId)
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

    @SuppressLint("CheckResult")
    public static void loadView(@NotNull Activity activity, @NotNull String url, @NotNull View view,
                                @DrawableRes int placeResourceId, @DrawableRes int errorResourceId) {
        boolean checkParameter = checkParameter(activity, url);
        if (checkParameter) {
            Glide
                    .with(activity)
                    .load(url)
                    .placeholder(placeResourceId)
                    .error(errorResourceId)
                    .fallback(errorResourceId)
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
}
