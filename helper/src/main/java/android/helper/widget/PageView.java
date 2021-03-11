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
    private int mPageCount;// 一页的个数
    private GestureDetector mDetector;
    private int mMarginLeft;
    private int mMarginRight;

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
        mPageCount = mViewRows * mViewColumn;

        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                scrollBy((int) distanceX, 0);

                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    public void setDataList(int[] resources) {
        if (resources != null && resources.length > 0) {
            LogUtil.e("size:" + resources.length);
            if (mResource != 0) {
                for (int i = 0; i < resources.length; i++) {
                    View view = LayoutInflater.from(mContext).inflate(mResource, null);
                    addView(view);
                }
            }
        }
    }

    public void setLayout(@LayoutRes int resource) {
        this.mResource = resource;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = 0;// view的宽度
        int measuredHeight = 0; // view的高度
        // 屏幕的宽度
        if (mScreenWidth <= 0) {
            mScreenWidth = ScreenUtil.getScreenWidth(getContext());
        }
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

            if (childCount > (mPageCount)) { // 如果数据大于一页，就需要拓展view的宽度
                // 重新设置
                measuredWidth = mMeasuredWidth * ((childCount / mPageCount) + 1);
            } else {
                measuredWidth = mMeasuredWidth;
            }
            // 重新设置
            setMeasuredDimension(measuredWidth, measuredHeight);
            LogUtil.e("w:" + measuredWidth + "  h:" + measuredHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        // 可以使用的总宽度 = 屏幕的宽度 - marginLeft -marginRight  -paddingLeft  - paddingRight
        int width = mMeasuredWidth - getPaddingLeft() - getPaddingRight();
        LogUtil.e("View的宽度为：" + mMeasuredWidth + "   width:" + width);

        int childCount = getChildCount();
        if (childCount > 0) {
            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;
            int columnInterval = 0;// 列的间距

            // 循环设置view的位置
            for (int i = 0; i < childCount; i++) {

                View childAt = getChildAt(i);

                // 每个view的宽度
                int viewMeasuredWidth = childAt.getMeasuredWidth();
                // 每个view的高度
                int viewMeasuredHeight = childAt.getMeasuredHeight();

                // 列的间距 = view的总宽度 减去view的所有宽度和 除以 列数 减一，因为第一列不用做view的间距
                columnInterval = (width - (mViewColumn * viewMeasuredWidth)) / mViewColumn;

                // 当前的页数
                int page = getPageForPosition(i);
                // 所在第几行
                int positionForRow = getPositionForRow(i);
                // 所在第几列
                int positionForColumn = getPositionForColumn(i);

                // 获取当前的页数
                LogUtil.e("当前view是：" + i + "  在第 " + positionForRow + " 行，在第 " + positionForColumn + " 列" + "  当前的页数：" + page);
                // 左侧 =  ( view的宽度 + paddingLeft  * page ) + （view的宽度 * 列） + （列间距 * 列）
                left = ((mMeasuredWidth) * page) + (viewMeasuredWidth * positionForColumn) + (columnInterval * positionForColumn) + getPaddingLeft() * (page + 1);
                right = left + viewMeasuredWidth;
                top = getPaddingTop() + viewMeasuredHeight * positionForRow + mRowInterval;
                bottom = top + viewMeasuredHeight;

                // 重新设置view的位置
                childAt.layout(left, top, right, bottom);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);

        return true;
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

}
