package com.android.helper.utils.photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;

/**
 * 图片加载的工具类
 */
public class GlideUtil implements BaseLifecycleObserver {

    private static final String TAG = "GlideUtil";

    /**
     * -------------------  重构-----------------------------
     */
    private int mErrorResource;
    private int mPlaceholderResource;
    private int mAngle;// 圆角的角度
    private FragmentActivity mActivity, mOldAcivity;
    private Fragment mFragment, mOldFragment;
    private Context mContext;
    private View mView;
    private ImageView mImageView;
    private boolean isDestroy;//页面是不是已经被销毁了；
    private RequestOptions mOptions;
    private String mUrl;

    private GlideUtil(Builder builder) {
        if (builder != null) {
            this.mErrorResource = builder.mErrorResource;
            this.mPlaceholderResource = builder.mPlaceholderResource;
            this.mAngle = builder.mAngle;
            this.mFragment = builder.mFragment;
            this.mActivity = builder.mActivity;
            this.mContext = builder.mContext;
        }
    }

    private void addObserverActivity() {
        if (mActivity != null) {
            /*
             * 这么做的原因是因为，为了避免同一个页面的对象，去不停的观察生命周期的状态，不然遇到列表的时候会很麻烦
             */
            if (mOldAcivity != null) {
                if (mActivity == mOldAcivity) {
                    return;
                }
            }
            Lifecycle lifecycle = mActivity.getLifecycle();
            lifecycle.addObserver(this);
            mOldAcivity = mActivity;
        }
    }

    private void addObserverFragment() {
        if (mFragment != null) {
            /*
             * 这么做的原因是因为，为了避免同一个页面的对象，去不停的观察生命周期的状态，不然遇到列表的时候会很麻烦
             */
            if (mOldFragment != null) {
                if (mOldFragment == mFragment) {
                    return;
                }
            }
            Lifecycle lifecycle = mFragment.getLifecycle();
            lifecycle.addObserver(this);
            mOldFragment = mFragment;
        }
    }

    public GlideUtil loadUrl(View view, String url) {
        if ((view != null) && (!TextUtils.isEmpty(url))) {
            // view相斥
            this.mImageView = null;
            this.mView = view;
            this.mUrl = url;

            mUrl = url;

            if (mContext != null) {
                if (mContext instanceof FragmentActivity) {
                    mActivity = (FragmentActivity) mContext;
                }
            }

            // 配置参数
            addOptions();

            // 观察生命周期
            if (mActivity != null) {
                addObserverActivity();
            }

            if (mFragment != null) {
                addObserverFragment();
            }

            // 加载view
            loadView();
        } else {
            LogUtil.e(TAG, "加载的View为空,或者ulr为空！");
        }
        return this;
    }

    public GlideUtil loadUrl(ImageView imageView, String url) {
        if ((imageView != null) && (!TextUtils.isEmpty(url))) {
            // view相斥
            this.mView = null;
            this.mImageView = imageView;
            this.mUrl = url;

            // 配置参数
            addOptions();

            // 观察生命周期
            if (mActivity != null) {
                addObserverActivity();
            }

            if (mFragment != null) {
                addObserverFragment();
            }
            // 加载view
            loadView();
        } else {
            LogUtil.e(TAG, "加载的View为空,或者ulr为空！");
        }
        return this;
    }

    @SuppressLint("CheckResult")
    private void addOptions() {
        if (mOptions == null) {
            mOptions = new RequestOptions().fitCenter();
        }

        // 占位图
        if (mPlaceholderResource != 0) {
            mOptions.placeholder(mPlaceholderResource);
        }

        // 错误图片
        if (mErrorResource != 0) {
            mOptions.error(mPlaceholderResource);
            mOptions.fallback(mPlaceholderResource);
        }

        // 指定圆角
        if (mAngle > 0) {
            mOptions.transform(new GlideRoundTransform(mAngle));
        }
    }

    private void loadView() {
        if (!TextUtils.isEmpty(mUrl)) {
            // activity
            if (mActivity != null && !isDestroy) {
                RequestBuilder<Drawable> builder = Glide
                        .with(mActivity)
                        .load(mUrl)
                        .thumbnail(0.2f) // 图片未加载出来之前的缩略图展示
                        .apply(mOptions);

                intoView(builder);
            }

            // fragment
            if (mFragment != null && !isDestroy) {
                RequestBuilder<Drawable> builder = Glide
                        .with(mFragment)
                        .load(mUrl)
                        .thumbnail(0.2f) // 图片未加载出来之前的缩略图展示
                        .apply(mOptions);

                intoView(builder);
            }

            //  context 这种情况，有可能会崩溃，但是目前无法处理，拿不到生命周期的对象就无法处理
            if (mContext != null) {
                RequestBuilder<Drawable> builder = Glide
                        .with(mContext)
                        .load(mUrl)
                        .thumbnail(0.2f) // 图片未加载出来之前的缩略图展示
                        .apply(mOptions);
                try {
                    intoView(builder);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void intoView(RequestBuilder<Drawable> builder) {
        if (mImageView != null) {
            builder.into(mImageView);
        }
        if (mView != null) {
            builder.into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                    mView.setBackground(resource);
                }

                @Override
                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                }
            });
        }
    }

    @Override
    public void onCreate() {
        // 感知生命周期，在生命周期里面进行加载，避免加载异常
        isDestroy = false;
        // 首次进来的时候，在这里加载一此对象
        loadView();
    }

    @Override
    public void onStart() {
        isDestroy = false;
    }

    @Override
    public void onResume() {
        isDestroy = false;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        isDestroy = true;
    }

    public static class Builder {
        private int mErrorResource;
        private int mPlaceholderResource;
        private int mAngle;// 圆角的角度
        private FragmentActivity mActivity;
        private Fragment mFragment;
        private Context mContext;

        public Builder(FragmentActivity activity) {
            mActivity = activity;
        }

        public Builder(Fragment fragment) {
            mFragment = fragment;
        }

        /**
         * 这种情况绝对的存在，有些地方，只能传递context
         */
        public Builder(Context context) {
            mContext = context;
        }

        /**
         * @return 错误占位图的资源
         */
        public Builder setErrorResource(int errorResource) {
            this.mErrorResource = errorResource;
            return this;
        }

        /**
         * @return 默认占位图的资源
         */
        public Builder setPlaceholderResource(int placeholder) {
            this.mPlaceholderResource = placeholder;
            return this;
        }

        /**
         * @return 设置指定的圆角角度
         */
        public Builder setAngle(int angle) {
            this.mAngle = angle;
            return this;
        }

        public GlideUtil build() {
            return new GlideUtil(this);
        }
    }

    /**
     * <ul>
     * <p>
     * <p>
     *                 Glide
     *                         .with(context)
     *                         .load(s.getLogo())
     *                         .thumbnail(0.2f) // 图片未加载出来之前的缩略图展示
     *                         .error(R.mipmap.icon_image_loading)
     *                         .placeholder(R.mipmap.icon_image_loading) //加载成功前显示的图片
     *                         .fallback(R.mipmap.icon_image_loading) //url为空的时候,显示的图片
     *                         .fitCenter()
     *                         .transform(new LoadImageUtils.GlideRoundTransform())
     *                         .dontAnimate()
     *                         .into(dealerHolder.img);
     *
     * </ul>
     */

    public static class GlideRoundTransform extends BitmapTransformation {
        private static float radius = 0f;

        public GlideRoundTransform() {
            this(4);
        }

        public GlideRoundTransform(int dp) {
            super();
            radius = dp;
        }

        @Override
        protected Bitmap transform(@NotNull BitmapPool pool, @NotNull Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(@NotNull MessageDigest messageDigest) {

        }
    }

}
