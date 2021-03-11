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
    private int mMeasuredWidth;// 一屏view的宽度
    private LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private int mViewRows = 2; // view的行数
    private int mViewColumn = 4;// view的列数
    private int mRowInterval = 30;// view行的间距
    private int mScrollPresetValue;// 滑动预设的值
    private int viewIntervalWidth;// 宽度的间距
    private int mOnePageCount;// 一页的个数
    private int mPageCount;// 一共有几页
    private GestureDetector mDetector;
    private int mMarginLeft;
    private int mMarginRight;
    private boolean isToLeft;
    private Scroller mScroller;

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
        mMeasuredWidth = mScreenWidth - mMarginLeft - mMarginRight;

        // 预设的值
        mScrollPresetValue = mMeasuredWidth / 4;

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
            mPageCount = (resources.length / mOnePageCount) + 1;
        }
    }

    public void setLayout(@LayoutRes int resource) {
        this.mResource = resource;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredHeight;// view的高度
        int measuredWidth;// view的宽度
        getMargin();

        LogUtil.e("screenWidth:" + mScreenWidth + " marginLeft:" + mMarginLeft + "  marginRight: " + mMarginRight + "  result:" + mMeasuredWidth);

        int childCount = getChildCount();
        if (childCount > 0) {

            // 测量每一个view的宽高
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            }

            int viewHeight = getChildAt(0).getMeasuredHeight();

            if (childCount > (mViewColumn)) {
                measuredHeight = viewHeight * mViewRows;// 说明大于一行就需要叠加view的高度
            } else {
                measuredHeight = viewHeight;// 小于一行，一个view的高度就可以了
            }

            // 加入行高
            measuredHeight += ((Math.abs(mViewRows - 1)) * mRowInterval);

            // 重新设置
            setMeasuredDimension(mMeasuredWidth, measuredHeight);
            LogUtil.e("w:" + mMeasuredWidth + "  h:" + measuredHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 可以使用的总宽度 = 屏幕的宽度 - marginLeft -marginRight
        int width = mMeasuredWidth - getPaddingLeft() - getPaddingRight();
        LogUtil.e("View的宽度为：" + mMeasuredWidth + "   width:" + width);

        int childCount = getChildCount();
        if (childCount > 0) {
            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;

            // 循环设置view的位置
            for (int i = 0; i < childCount; i++) {

                View childAt = getChildAt(i);

                // 每个view的宽度
                int viewMeasuredWidth = childAt.getMeasuredWidth();
                // 每个view的高度
                int viewMeasuredHeight = childAt.getMeasuredHeight();

                // 当前的页数
                int page = getPageForPosition(i);
                // 所在第几行
                int positionForRow = getPositionForRow(i);
                // 所在第几列
                int positionForColumn = getPositionForColumn(i);

                // 行的间距 =  可用的区间 -  （ 列数 +1 ）
                viewIntervalWidth = (width - (mViewColumn * viewMeasuredWidth)) / (mViewColumn + 1);

                // 获取当前的页数
                LogUtil.e("当前view是：" + i + "  在第 " + positionForRow + " 行，在第 " + positionForColumn + " 列" + "  当前的页数：" + page);

                // 左侧 = 左侧padding + 左侧间距  +左侧的view
                if (page == 0) {
                    left = (((page) * mMeasuredWidth)) + ((positionForColumn + 1) * viewIntervalWidth) + (positionForColumn * viewMeasuredWidth);
                } else {
                    left = (((page) * mMeasuredWidth) - viewIntervalWidth) + ((positionForColumn + 1) * viewIntervalWidth) + (positionForColumn * viewMeasuredWidth);
                }

                if (positionForColumn == 0) {
                    left += ((page + 1) * getPaddingLeft());
                }

                right = left + viewMeasuredWidth;

                top = getPaddingTop() + (viewMeasuredHeight * positionForRow) + (positionForRow * mRowInterval);

                bottom = top + viewMeasuredHeight;

                // 重新设置view的位置
                childAt.layout(left, top, right, bottom);
            }
        }

    }

    private float mDownStartX;
    private int mPage;// 当前是第几页

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownStartX = event.getX();

                // 当前按下的时候，是第几页
                mPage = getScrollX() / mMeasuredWidth;
                break;

            case MotionEvent.ACTION_MOVE:
                float eventX = event.getX();
                float v = eventX - mDownStartX;
                isToLeft = v < 0;

                // 向左滑动的限制
                int scrollX = getScrollX();
                LogUtil.e("向左：" + isToLeft + "   scrollX:" + scrollX);
                if (scrollX <= 0) {
                    setScrollX(0);
                }
                // 向右滑动的限制
                if (scrollX >= ((mOnePageCount - 1) * mMeasuredWidth)) {

                }
                break;

            case MotionEvent.ACTION_UP:
                int scrollX1 = getScrollX();

                LogUtil.e("isToLeft:" + isToLeft);
                if (isToLeft) { // 向左
                    if (scrollX1 >= mScrollPresetValue) {
                        // 向右滑动的时候，页数增加1
                        mPage += 1;
                        startScrollX(scrollX1, mMeasuredWidth - viewIntervalWidth);
                    } else {
                        startScrollX(scrollX1, 0);
                    }
                } else { // 向右

                    if (Math.abs(scrollX1 - mMeasuredWidth) >= mScrollPresetValue) {
                        // 向左滑动的时候，页数减去1
                        if (mPage > 0) {
                            mPage -= 1;
                        }
                        startScrollX(scrollX1, 0);
                    } else {
                        startScrollX(scrollX1, mMeasuredWidth);
                    }
                }
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

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }
}
