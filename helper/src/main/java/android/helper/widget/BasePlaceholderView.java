package android.helper.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.helper.R;
import android.helper.databinding.BaseEmptyViewBinding;
import android.helper.utils.ConvertUtil;

/**
 * 封装刷新控件的站位图
 * 目标：三种状态的展示：
 * 1：图片和文字展示的view,通常用来展示默认的数据，例如：数据为空的时候，提示找不到你想要的内容
 * 2：普通的错误提示，用来展示具体的错误消息
 * 3：网络异常的错误提示，用来展示网络异常时候的view，并带有再次展示的按钮
 */
public class BasePlaceholderView extends FrameLayout {


    private BaseEmptyViewBinding binding;

    private String mTitle;
    private int mResource;// 图片的资源
    private String mRefreshTitle = "点击刷新";

    public BasePlaceholderView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public BasePlaceholderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        binding = BaseEmptyViewBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot());
    }


    /**
     * 设置网络请求错误的站位图
     */
    public void setNetWorkError(OnClickListener listener) {
        binding.ivBaseEmptyImage.setImageResource(R.drawable.icon_base_empty_http_error);
        binding.tvBaseEmptyTitle.setText("当前网络不太顺畅");

        binding.tvBaseEmptyRefresh.setVisibility(VISIBLE);
        binding.tvBaseEmptyRefresh.setOnClickListener(listener);
    }

    /**
     * 设置指定图片加文字的布局
     *
     * @param imageResources 具体的图片
     * @param title          错误的标题
     */
    public void setEmptyView(int imageResources, String title) {
        // 设置图片资源
        int visibility = binding.ivBaseEmptyImage.getVisibility();
        if (imageResources > 0) {
            if (visibility != VISIBLE) {
                binding.ivBaseEmptyImage.setVisibility(View.VISIBLE);
            }
            binding.ivBaseEmptyImage.setImageResource(imageResources);
        } else {
            if (visibility != INVISIBLE) {
                binding.ivBaseEmptyImage.setVisibility(View.INVISIBLE);
            }
        }

        // 标题的内容
        int visibility1 = binding.tvBaseEmptyTitle.getVisibility();
        if (!TextUtils.isEmpty(title)) {
            if (visibility1 != View.VISIBLE) {
                binding.tvBaseEmptyTitle.setVisibility(View.VISIBLE);
            }
            binding.tvBaseEmptyTitle.setText(title);
        } else {
            if (visibility1 != View.INVISIBLE) {
                binding.tvBaseEmptyTitle.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置指定图片加文字的布局
     *
     * @param imageResources 图片的资源
     * @param title          错误的标题头
     * @param msg            具体的错误信息
     */
    public void setErrorView(int imageResources, String title, String msg) {

        setEmptyView(imageResources, title);

        // 提示的msg消息
        int visibility = binding.tvBaseEmptyMsg.getVisibility();
        if (!TextUtils.isEmpty(msg)) {
            if (visibility != VISIBLE) {
                binding.tvBaseEmptyMsg.setVisibility(VISIBLE);
            }
            binding.tvBaseEmptyMsg.setText(msg);
        } else {
            if (visibility != INVISIBLE) {
                binding.tvBaseEmptyMsg.setVisibility(INVISIBLE);
            }
        }
    }


    /**
     * @param imageResources  具体的图片
     * @param title           具体的标题头
     * @param msg             具体的错误消息
     * @param buttonContext   按钮的文字提示
     * @param refreshListener 点击事件的回调
     */
    public void setEmptyView(int imageResources, String title, String msg, String buttonContext,
                             OnClickListener refreshListener) {
        // 设置站位图
        setErrorView(imageResources, title, msg);
        // 设置点击事件
        setNetWorkError(buttonContext, refreshListener);
    }


    /**
     * 设置默认空数据时候图片带文字的布局,默认不去重复请求
     */
    public void setEmptyView() {
        if (mResource == 0) {
            // 设置默认的资源图片
            mResource = R.drawable.icon_base_empty_empty;
        }

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = "没有找到您想要的内容";
        }

        setEmptyView(mResource, mTitle);
        // 隐藏刷新按钮
        showRefreshButton(false);
    }

    /**
     * 设置网络请求错误的站位图
     */
    public void setNetWorkError(String refreshText, OnClickListener listener) {

        if (mResource == 0) {
            // 设置默认的资源图片
            mResource = R.drawable.icon_base_empty_http_error;
        }

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = "当前网络不太顺畅";
        }
        setEmptyView(mResource, mTitle);

        // 隐藏刷新按钮
        showRefreshButton(false);


        if (mResource == 0) {
            // 设置默认的资源图片
            mResource = R.drawable.icon_base_empty_http_error;
        }

        setEmptyView(R.drawable.icon_base_empty_empty, mTitle);
        // 隐藏刷新按钮
        showRefreshButton(true);

        // 只有点击事件不为空，才会设置button的文字和点击事件，否则没有任何的意义
        if (listener != null) {
            if (TextUtils.isEmpty(refreshText)) {
                refreshText = mRefreshTitle;
            }
            binding.tvBaseEmptyRefresh.setText(refreshText);
            binding.tvBaseEmptyRefresh.setOnClickListener(listener);
        }
    }


    /**
     * 是否展示刷新按钮
     *
     * @param isShow true  or  false
     */
    protected void showRefreshButton(boolean isShow) {
        int visibility = binding.tvBaseEmptyRefresh.getVisibility();
        if (isShow) {
            if (visibility != View.VISIBLE) {
                binding.tvBaseEmptyRefresh.setVisibility(VISIBLE);
            }
        } else {
            if (visibility != View.GONE) {
                binding.tvBaseEmptyRefresh.setVisibility(GONE);
            }
        }
    }


    /**
     * 动态设置图片的大小、比例，边距等，适用于特殊的情况
     *
     * @param width          图片的宽度
     * @param height         图片的高度
     * @param dimensionRatio 图片的比例
     * @param left           距离左侧的边距
     * @param right          距离右侧的边距
     * @param top            距离上边的边距
     * @param bottom         距离下边的边距
     */
    public void setImageSize(int width, int height, String dimensionRatio, float left, float top, float right, float bottom) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.ivBaseEmptyImage.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        binding.ivBaseEmptyImage.setAdjustViewBounds(true);
        if (!TextUtils.isEmpty(dimensionRatio)) {
            layoutParams.dimensionRatio = dimensionRatio;
        }

        if (left > 0) {
            layoutParams.leftMargin = (int) left;
        }
        if (right > 0) {
            layoutParams.rightMargin = (int) right;
        }
        if (top > 0) {
            layoutParams.topMargin = (int) top;
        }
        if (bottom > 0) {
            layoutParams.bottomMargin = (int) bottom;
        }

        binding.ivBaseEmptyImage.setLayoutParams(layoutParams);
    }

    /**
     * @param color title的颜色
     * @param size  title的大小
     * @param top   title 距离上边距的距离
     */
    public void setTitleStyle(int color, float size, float top) {
        // 设置颜色
        int textViewColor = ContextCompat.getColor(getContext(), color);
        binding.tvBaseEmptyTitle.setTextColor(textViewColor);

        // 设置大小
        if (size > 0) {
            binding.tvBaseEmptyTitle.setTextSize(size);
        }

        if (top > 0) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.tvBaseEmptyTitle.getLayoutParams();
            layoutParams.topMargin = (int) top;
            binding.tvBaseEmptyTitle.setLayoutParams(layoutParams);
        }
    }

    /**
     * 复原整个站位图的大小
     */
    public void setRestoreDefaultUI() {
        // image
        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) binding.ivBaseEmptyImage.getLayoutParams();
        layoutParams1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.ivBaseEmptyImage.setAdjustViewBounds(true);
        layoutParams1.dimensionRatio = "110:90";

        // title
        binding.tvBaseEmptyTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.base_loading_text));
        binding.tvBaseEmptyTitle.setTextSize(ConvertUtil.toPx(15));
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.tvBaseEmptyTitle.getLayoutParams();
        int dp10 = (int) ConvertUtil.toDp(10f);
        layoutParams.topMargin = dp10;
        binding.tvBaseEmptyTitle.setLayoutParams(layoutParams);
    }

}
