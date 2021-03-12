package android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.helper.utils.LogUtil;
import android.helper.utils.ScreenUtil;
import android.helper.utils.ViewUtil;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.annotation.LayoutRes;

/**
 * 可以滑动的九宫格View
 */
public class PageView extends ViewGroup {

    private Context mContext;
    private int mResource;// 资源文件
    private int mScreenWidth; // 屏幕的宽度
    private int mMarginLeft;
    private int mMarginRight;
    private int mLayoutMeasuredWidth;// layout的宽度
    private int mLayoutMeasuredHeight;     // layout的高度

    private int mViewRows = 2; // view的行数
    private int mViewColumn = 4;// view的列数
    private int mRowInterval = 30;// view行的间距

    private int mScrollPresetValue;// 滑动预设的值
    private int mViewIntervalWidth;// 宽度的间距
    private int mOnePageCount;// 一页的个数
    private int mPageCount;// 一共有几页
    private GestureDetector mDetector;

    private boolean isToLeft;
    private Scroller mScroller;
    private float mScrollInterval; // 滑动的间距
    private int mViewMeasuredHeight;// view的高度
    private int mViewMeasuredWidth;// view的宽度
    private int mChildCount;

    public PageView(Context context) {
        super(context);
        initView(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        mOnePageCount = mViewRows * mViewColumn;
        mScroller = new Scroller(context);

        // 屏幕的宽度
        mScreenWidth = ScreenUtil.getScreenWidth(getContext());
        getMargin();

        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                // 大于一页的时候，才可以滑动
                if (mOnePageCount > 0) {
                    scrollBy((int) distanceX, 0);
                }

                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    private void getMargin() {
        // view的marginLeft
        if (mMarginLeft <= 0) {
            mMarginLeft = ViewUtil.getMarginLeft(this);
        }
        // view的marginRight
        if (mMarginRight <= 0) {
            mMarginRight = ViewUtil.getMarginRight(this);
        }
        // view的宽度 =  屏幕的宽度 -  左右的margin值
        mLayoutMeasuredWidth = mScreenWidth - mMarginLeft - mMarginRight;

        // 预设的值
        mScrollPresetValue = mLayoutMeasuredWidth / 4;

    }

    public void setDataList(int[] resources) {
        getMargin();

        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (resources != null && resources.length > 0) {
            LogUtil.e("size:" + resources.length);
            if (mResource != 0) {
                for (int i = 0; i < resources.length; i++) {
                    View view = LayoutInflater.from(mContext).inflate(mResource, null);
                    view.setLayoutParams(params);
                    addView(view);
                }
            }
            // 一共有多少页
            int value = resources.length / mOnePageCount;
            int i = resources.length % mOnePageCount;
            if (i > 0) {
                mPageCount = (value + 1);
            } else {
                mPageCount = value;
            }
        }
    }

    public void setLayout(@LayoutRes int resource) {
        this.mResource = resource;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        getMargin();

        LogUtil.e("screenWidth:" + mScreenWidth + " marginLeft:" + mMarginLeft + "  marginRight: " + mMarginRight + "  result:" + mLayoutMeasuredWidth);

        mChildCount = getChildCount();
        if (mChildCount > 0) {

            // 测量每一个view的宽高
            for (int i = 0; i < mChildCount; i++) {
                View childAt = getChildAt(i);
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            }

            if (mViewMeasuredHeight <= 0) {
                mViewMeasuredHeight = getChildAt(0).getMeasuredHeight();
            }
            if (mViewMeasuredWidth <= 0) {
                mViewMeasuredWidth = getChildAt(0).getMeasuredWidth();
            }

            if (mChildCount > (mViewColumn)) {
                mLayoutMeasuredHeight = mViewMeasuredHeight * mViewRows;// 说明大于一行就需要叠加view的高度
            } else {
                mLayoutMeasuredHeight = mViewMeasuredHeight;// 小于一行，一个view的高度就可以了
            }

            // 加入行高
            mLayoutMeasuredHeight += ((Math.abs(mViewRows - 1)) * mRowInterval);
            // 重新设置
            setMeasuredDimension(mLayoutMeasuredWidth, mLayoutMeasuredHeight + getPaddingTop() + getPaddingBottom());
            LogUtil.e("w:" + mLayoutMeasuredWidth + "  h:" + mViewMeasuredHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 重新布局
        int right, top, bottom;
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int left = 0;
            // 列间距 = view的宽度 - 一排view的宽度和 除以 列数 +1
            mViewIntervalWidth = (mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight() - (mViewColumn * mViewMeasuredWidth)) / (mViewColumn + 1);

            int positionForColumn = getPositionForColumn(i);
            int positionForRow = getPositionForRow(i);
            int page = getPageForPosition(i);

            // （间距 *（列数 +1）） + （列数 * view的宽度）+ paddingLeft  = 一屏view的宽度
            left += (((positionForColumn + 1) * mViewIntervalWidth) + (positionForColumn * mViewMeasuredWidth) + getPaddingLeft());
            // 增加 页数 =（屏幕的宽度 -左右的padding - 间距）
            left += (page * ((mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight()) - mViewIntervalWidth));

            right = left + mViewMeasuredWidth;
            top = positionForRow * mViewMeasuredHeight + (positionForRow * mRowInterval) + getPaddingTop();
            bottom = top + mViewMeasuredHeight;

            childAt.layout(left, top, right, bottom);
        }
    }

    private float mDownStartX;
    private int mPage;// 当前是第几页

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 小于一页就不执行
        if (mChildCount <= mOnePageCount) {
            return false;
        }
        mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownStartX = event.getX();

                // 当前按下的时候，是第几页
                mPage = getScrollX() / (mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight() - mViewIntervalWidth);
                break;

            case MotionEvent.ACTION_MOVE:
                float eventX = event.getX();
                // 求出差值
                mScrollInterval = eventX - mDownStartX;
                // 向左还是向右
                isToLeft = mScrollInterval < 0;

                int scrollX = getScrollX();
                // 滑动的限制
                if (scrollX <= 0) {
                    scrollTo(0, 0);
                }
                LogUtil.e("scroll:" + scrollX);
                if (isToLeft) {
                    if (scrollX > ((mPageCount - 1) * (mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight() - mViewIntervalWidth))) {

                        scrollTo(((mPageCount - 1) * (mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight() - mViewIntervalWidth)), 0);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                int targetEndX = 0;
                int scrollXUp = getScrollX();
                // 抬起的动作
                if ((Math.abs(mScrollInterval)) > mScrollPresetValue) {
                    if (isToLeft) {
                        if (mPage < mPageCount - 1) {
                            mPage += 1;
                        }
                    } else {
                        if (mPage > 0) {
                            mPage -= 1;
                        }
                    }
                }

                targetEndX = mPage * (mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight() - mViewIntervalWidth);

                startScrollX(scrollXUp, targetEndX);

                break;
        }
        return true;
    }

    private void startScrollX(int scrollX, int target) {
        LogUtil.e("page:" + mPage);
        mScroller.startScroll(scrollX, 0, target - scrollX, 0);
        invalidate();
    }

    /**
     * @param currentPosition 当前便利的i
     * @return 获取当前view 在哪一行
     */
    private int getPositionForRow(int currentPosition) {
        if (currentPosition > 0) {
            return currentPosition / mViewColumn % mViewRows;
        }
        return 0;
    }

    /**
     * @param currentPosition 当前便利的i
     * @return 获取当前view是在那一列
     */
    private int getPositionForColumn(int currentPosition) {
        if (currentPosition > 0) {
            return currentPosition % mViewColumn;
        }
        return 0;
    }

    /**
     * @param position 当前的position
     * @return 根据当前的position获取当前的页数
     */
    public int getPageForPosition(int position) {
        if (position <= 0) {
            return 0;
        }
        return (position / (mViewRows * mViewColumn));
    }

    public void setOnClickListener(View.OnClickListener onClickListener){

    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }
}
