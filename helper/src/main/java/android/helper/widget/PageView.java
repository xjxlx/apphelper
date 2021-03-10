package android.helper.widget;

import android.content.Context;
import android.helper.utils.LogUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

/**
 * 可以滑动的九宫格View
 */
public class PageView extends ViewGroup {

    private Context mContext;
    private int mResource;// 资源文件
    private View mView;// 布局的文件
    private int mMeasuredWidth;// view的整体宽度
    private LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private int mViewRows = 3; // view的行数
    private int mViewColumn = 4;// view的列数
    private int mRowInterval = 30;// view行的间距
    private int mPageCount;// 一页的个数

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
    }

    public void setDataList(int[] resources) {
        if (resources != null && resources.length > 0) {
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

        int measuredHeight = 0;// view的整体高度

        // view整体的宽度
        mMeasuredWidth = resolveSize(widthMeasureSpec, MeasureSpec.getSize(widthMeasureSpec));

        int childCount = getChildCount();
        if (childCount > 0) {
            // 测量每一个view的宽高
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            }

            int viewHeight = getChildAt(0).getMeasuredHeight();
            // 求出view布局的高度
            // 说明大于一行
            if (childCount > (mViewColumn)) {
                measuredHeight = viewHeight * mViewRows;
            } else {
                // 小于一行
                // 因为布局都一样，所以测量一个就够用了
                measuredHeight = viewHeight;
            }
        }
        // 加入行高
        measuredHeight += ((mViewRows - 1) * mRowInterval);
        // 重新设置
        setMeasuredDimension(mMeasuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount > 0) {

            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;
            int rowInterval = 0;// 行的间距
            int columnInterval = 0;// 列的间距

            // 循环设置view的位置
            for (int i = 0; i < childCount; i++) {

                View childAt = getChildAt(i);

                // 每个view的宽度
                int measuredWidth = childAt.getMeasuredWidth();
                // 每个view的高度
                int measuredHeight = childAt.getMeasuredHeight();

                // 列的间距 = view的总宽度 减去view的所有宽度和 除以 列数 减一，因为第一列不用做view的间距
                columnInterval = (mMeasuredWidth - (mViewColumn * measuredWidth)) / (mViewColumn - 1);

                // 所在第几行
                int positionForRow = getPositionForRow(i);
                // 所在第几列
                int positionForColumn = getPositionForColumn(i);

                if (i < (mViewRows * mViewColumn)) { // 小于一页的逻辑
                    LogUtil.e("当前view是：" + i + "  在第 " + positionForRow + " 行，在第 " + positionForColumn + " 列！");
                    left = (positionForColumn * measuredWidth) + (positionForColumn * columnInterval);
                    right = ((positionForColumn + 1) * measuredWidth) + (positionForColumn * columnInterval);
                    top = (positionForRow * measuredHeight) + (positionForRow * mRowInterval);
                    bottom = ((positionForRow + 1) * measuredHeight) + (positionForRow * mRowInterval);
                }

                // 重新设置view的位置
                childAt.layout(left, top, right, bottom);
            }
        }
    }

    /**
     * @param currentPosition 当前便利的i
     * @return 获取当前view 在哪一行
     */
    private int getPositionForRow(int currentPosition) {
        if (currentPosition > 0) {
            return currentPosition / mViewColumn;
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

}
