package android.helper.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.helper.utils.LogUtil;
import android.helper.utils.photo.GlideUtil;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import androidx.annotation.IntDef;
import androidx.fragment.app.Fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 自定义BannerView
 */
public class BannerView extends ViewGroup {

    private Activity activity;

    /**
     * 设置数据：使用View
     */
    private List<View> mViewList;
    /**
     * 设置数据：数据的集合
     */
    private List<String> mPathList;

    private int[] mResourceList;

    /**
     * 设置数据：Fragment的集合
     */
    private List<Fragment> mFragmentList;

    private int mDataType; // 数据来源的类型
    private Scroller mScroller;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DataType.Path, DataType.View, DataType.Fragment})
    public @interface DataType {
        int Path = 1;
        int View = 2;
        int Fragment = 3;
    }

    public BannerView(Context context) {
        super(context);
        initView(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int right = 0;

        int measuredWidth = getMeasuredWidth();

        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                if (view != null) {
                    right += measuredWidth;
                    LogUtil.e("当前的position:" + i + " left:" + left + "  right:" + right);

                    view.layout(left, 0, right, b);
                    left += measuredWidth;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));
        int height = resolveSize(heightMeasureSpec, MeasureSpec.getSize(heightMeasureSpec));

        setMeasuredDimension(width, height);
    }

    public void setDateListView(List<View> viewList) {
        this.mViewList = viewList;
        mDataType = DataType.View;

        if (mViewList != null && mViewList.size() > 0) {
            for (int i = 0; i < mViewList.size(); i++) {
                View view = mViewList.get(i);
                if (view != null) {
                    addView(view);
                }
            }
            requestLayout();
        }
    }

    public void setDataListPath(List<String> viewList) {
        this.mPathList = viewList;
        mDataType = DataType.Path;

        if (mPathList != null && mPathList.size() > 0) {
            for (int i = 0; i < mPathList.size(); i++) {
                String path = mPathList.get(i);
                if ((!TextUtils.isEmpty(path)) && (activity != null)) {
                    ImageView imageView = new ImageView(activity);
                    GlideUtil.loadView(activity, path, imageView);

                    addView(imageView);
                }
            }
            requestLayout();
        }
    }

    /**
     * 设置本地的资源id
     */
    public void setDateListResource(int[] resourceList) {
        this.mResourceList = resourceList;
        if (mResourceList != null && resourceList.length > 0) {
            for (int resourceId : resourceList) {
                if (((resourceId != 0)) && (activity != null)) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setImageResource(resourceId);

                    addView(imageView);
                }
            }
            requestLayout();
        }
    }

    public void setDateListFragment(List<Fragment> viewList) {
        this.mFragmentList = viewList;
        mDataType = DataType.Fragment;

        if (mFragmentList != null && mFragmentList.size() > 0) {
            for (int i = 0; i < mFragmentList.size(); i++) {
                Fragment fragment = mFragmentList.get(i);
                if (fragment != null) {
                    View view = fragment.getView();
                    if (view != null) {
                        addView(view);
                    }
                }
            }
            requestLayout();
        }
    }

    public List<View> getViewList() {
        return mViewList;
    }

    public List<String> getDataList() {
        return mPathList;
    }

    public List<Fragment> getFragmentList() {
        return mFragmentList;
    }

    public int[] getResourceList() {
        return mResourceList;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int finalX = mScroller.getFinalX();
        int left = this.getLeft();

        float rawX = ev.getRawX();
        float x = ev.getX();
        LogUtil.e("--->rawX:" + rawX + "  left:" + left);
        return super.dispatchTouchEvent(ev);
    }

    float startX = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                LogUtil.e("startX:" + startX);
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                break;

            case MotionEvent.ACTION_MOVE:
                float endY = event.getRawX();

                // 获取一个滑动的间距
                float intervalValue = endY - startX;

                // 滑动viewGroup中的子view
                mScroller.startScroll(mScroller.getFinalX(), 0, (int) -intervalValue, 0, 3000);
                invalidate();
                startX = endY;
                break;

        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    /**
     * 复原位置
     */
    public void reset() {
        scrollTo(0, 0);
    }
}
