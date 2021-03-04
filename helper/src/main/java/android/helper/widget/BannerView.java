package android.helper.widget;

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

import androidx.fragment.app.Fragment;

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

    private Scroller mScroller;
    private int childCount;
    private int measuredWidth;
    private boolean isToLeft;
    private float startX;
    private float interval;
    private int scrollX;
    private int preset;

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


    public void setDateListView(List<View> viewList) {
        this.mViewList = viewList;
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
        if (mPathList != null && mPathList.size() > 0) {
            for (int i = 0; i < mPathList.size(); i++) {
                String path = mPathList.get(i);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                if ((!TextUtils.isEmpty(path)) && (activity != null)) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(params);
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
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                if (((resourceId != 0)) && (activity != null)) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(params);
                    imageView.setImageResource(resourceId);
                    addView(imageView);
                }
            }
            requestLayout();
        }
    }

    public void setDateListFragment(List<Fragment> viewList) {
        this.mFragmentList = viewList;
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


    private int getPositionForScrollX(int scrollX) {
        return scrollX / measuredWidth;
    }

    private int getOffsetX(int scrollX) {
        return scrollX % measuredWidth;
    }

    public void reset() {
        scrollTo(0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = 0;
        childCount = getChildCount();

        int width = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {

                // 测量子view的 宽高
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec);

                int height = childAt.getMeasuredHeight();
                if (measuredHeight < height) {
                    measuredHeight = height;
                }
            }
        }
        LogUtil.e("width:" + width + " height:" + measuredHeight);
        setMeasuredDimension(width, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        measuredWidth = getMeasuredWidth();
        // 预设的值
        preset = measuredWidth / 3;
        int left = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                LogUtil.e("l:" + l + " t:" + t + " r:" + r + " b:" + b);
                int height = childAt.getMeasuredHeight();
                childAt.layout(left, 0, (measuredWidth + left), height);
                left += measuredWidth;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                float rawX = event.getRawX();
                interval = rawX - startX;
                isToLeft = interval < 0;


                scrollBy((int) -interval, 0);

                startX = rawX;
                break;

            case MotionEvent.ACTION_UP:
                scrollX = getScrollX();
                int position = getPositionForScrollX(scrollX);
                int offsetX = getOffsetX(scrollX);

                if (isToLeft) {
                    if (offsetX >= preset) {
                        if (position < childCount - 1) {
                            position++;
                        }
                    }
                } else {
                    int rightOffset = measuredWidth - offsetX;
                    LogUtil.e("rightOffset" + "   " + rightOffset + "  preset:" + preset);
                    if (rightOffset <= preset) {
                        if (position < childCount - 1) {
                            position++;
                        }
                    }
                }
                scrollTo(position * measuredWidth, 0);
                break;
        }
        return true;
    }
}
