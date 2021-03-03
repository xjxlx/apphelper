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
    private int measuredWidth;
    private int childCount;
    private float mStartX; // 开始滑动的X轴位置
    private float mStartXRight;
    private boolean isToLeft;// 是否是向左滑动
    private float interValRight; // 右侧滑动所用到的距离

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

        // 获取屏幕的宽度
        measuredWidth = getMeasuredWidth();
        childCount = getChildCount();

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
        // 设置宽高
        setMeasuredDimension(width, height);
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
        LogUtil.e("----> dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.e("----> onInterceptTouchEvent");
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int scrollX = getScrollX();
            if ((scrollX < 0) || (scrollX > (childCount * (measuredWidth - 1)))) {
                LogUtil.e("scroll --->停止滑动的scroll:" + scrollX);
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.e("----> onTouchEvent");
        int position = 0;
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.e("ACTION_DOWN:" + MotionEvent.ACTION_DOWN);

                float rawXLeft = event.getRawX();
                mStartXRight = rawXLeft;
                mStartX = rawXLeft;
                break;

            case MotionEvent.ACTION_MOVE:
                float rawX = event.getRawX();
                // 获取间距
                // 滑动的间距，用来连续的滑动
                float interVal = rawX - mStartX;
                interValRight = rawX - mStartXRight;
                // 区分左右
                isToLeft = !(interVal >= 0);

                // 避免滑动到布局的外边
                int scrollX1 = getScrollX();

                LogUtil.e("rawX   :" + rawX);
                scrollBy((int) -interVal, 0);
//                    LogUtil.e("scroll --->当前滑动的值为：:" + -interVal);
                // 把移动后的距离赋值给开始距离，使得滑动充满连贯性
                mStartX = rawX;

                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                LogUtil.e("ACTION_UP:" + MotionEvent.ACTION_UP + "  isLeft:" + isToLeft);
                // 预设的值
                int preset = measuredWidth / 3;

//                scrollTo(position * measuredWidth, 0);
                break;
        }
        return true;
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
}
