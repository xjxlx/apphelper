package android.helper.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.helper.utils.LogUtil;
import android.helper.utils.photo.GlideUtil;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    private int childCount;
    private int measuredWidth;

    private GestureDetector mDetector;
    private float mStartX;
    private boolean isToLeft;//  是否是向左滑动
    private int mPreset; // 预设的值
    private int mPosition;

    public BannerView(Context context) {
        super(context);
        initView(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (context instanceof Activity) {
            activity = (Activity) context;
        }

        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollBy((int) distanceX, 0);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
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
        setMeasuredDimension(width, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        measuredWidth = getMeasuredWidth();
        // 预设的值
        mPreset = measuredWidth / 3;
        int left = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                int height = childAt.getMeasuredHeight();
                childAt.layout(left, 0, (measuredWidth + left), height);
                left += measuredWidth;
            }
        }
    }




    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();

                break;

            case MotionEvent.ACTION_UP:
                float eventX = event.getX();
                isToLeft = (eventX - mStartX) < 0;

                LogUtil.e("向左滑动：" + isToLeft);

                int scrollX = getScrollX();
                int position = getPositionForScrollX(scrollX);
                int offsetX = getOffsetX(scrollX);

                LogUtil.e("scrollX:   " + scrollX + "   position:  " + position + "  offsetX:" + offsetX + "  mPreset: " + mPreset);
                if (isToLeft) {
                    if (offsetX >= mPreset) {
                        if (position < childCount - 1) {
                            mPosition = position + 1;
                        } else {
                            mPosition = position;
                        }
                    } else {
                        mPosition = position;
                    }
                } else {
                    if ((measuredWidth - offsetX) >= mPreset) {
                        mPosition = position;
                    } else {
                        mPosition = position + 1;
                    }
                }
                scrollTo(mPosition * measuredWidth, 0);

                break;
        }
        return true;
    }

}
