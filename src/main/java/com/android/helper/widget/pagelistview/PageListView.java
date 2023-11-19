package com.android.helper.widget.pagelistview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.common.utils.LogUtil;
import com.android.helper.utils.ScreenUtil;

/**
 * @author : 流星
 * @CreateDate: 2022/1/5-2:54 下午
 * @Description: 绘制一个可以左右分页滑动的列表View
 * <ol>
 * 思路：
 * 1：创建一个adapter,用来绑定数据和视图
 * 2：创建viewHolder，用来缓存和复用
 * </ol>
 */
public class PageListView extends HorizontalScrollView {

    private View mConvertView;
    private int mRow; // 行数
    private int mColumn; // 列数
    private int mRowInterval; // 行间距
    private int mColumnInterval; // 列间距

    private int mMaxWidth; // 总的宽度
    private int mMaxHeight; // 总的高度
    private int mChildWidth; // 单个条目的宽度
    private int mChildHeight = 100; // 单个条目的高度

    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom;// view的padding值
    private int mAverageWidth; // 每一列平均的宽度
    private int mCount; // 列表的数据长度

    public PageListView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public PageListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
    }

    public void setAdapter(PageListAdapter<?> adapter) {
        if (adapter != null) {
            // 获取数量，根据数量去区分排列
            mCount = adapter.getCount();
            mConvertView = adapter.getView(0, mConvertView, this);
            // 刷新列表，重新加载
            invalidate();
            addViews();
        }
    }

    private void addViews() {
    }

    /**
     * 刷新全部数据
     */
    public void notifyDataSetChanged() {
    }

    /**
     * 刷新单个数据
     *
     * @param position 指定的位置角标
     */
    public void notifyItemChanged(int position) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mChildHeight = 50;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 计算页面的宽高
        if (mConvertView != null) {
            // 获取单个view的宽高
            // mChildWidth = mConvertView.getMeasuredWidth();
            // mChildHeight = mConvertView.getMeasuredHeight();
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            // 计算出每个view所占用的平均宽度
            int screenWidth = ScreenUtil.getScreenWidth(getContext());
            if (screenWidth > 0 && mColumn > 0) {
                mAverageWidth = screenWidth / mColumn;
            }
            // 计算出最大的宽度
            getMaxWidth(screenWidth);
            // 计算出最大的高度
            getMaxHeight();
            LogUtil.e("最大的宽度：" + mMaxWidth + "   最大的高度：" + mMaxHeight);
            setMeasuredDimension(mMaxWidth, mMaxHeight);
        }
    }

    /**
     * 计算出单页最大的高度
     */
    private void getMaxHeight() {
        if (mCount > 0) {
            // 数据的总量 对比 每一行的数量，即列的数量
            if (mColumn > 0) {
                // 如果数据的量，小于等于列的数量，说明不满足一行，则高度 = view的高度
                if (mCount <= mColumn) {
                    mMaxHeight = mChildHeight;
                } else {
                    // 如果数量大于指定的列，则去使用具体的行数
                    mMaxHeight = mChildHeight * (mRow + mRowInterval);
                }
            }
        }
    }

    /**
     * 获取最大的宽度
     *
     * @param screenWidth 屏幕的宽度
     */
    private void getMaxWidth(int screenWidth) {
        // 默认设置屏幕的宽度
        mMaxWidth = screenWidth;
        if (mCount > 0 && mColumn > 0 && mRow > 0 && screenWidth > 0) {
            // 获取单页的数据量
            int pageCount = mColumn * mRow;
            if (mCount <= pageCount) {
                // 计算单页的行数和列数，如果想乘小于一页，就默认当前的最大宽度为屏幕的宽度
                mMaxWidth = screenWidth;
            } else {
                int divisor = mCount / pageCount; // 获取除数
                int remainder = mCount % pageCount; // 获取余数
                // 回去多页时候具体的页数
                if (remainder <= 0) {
                    // 说明正好是整页的数量，宽度 = 屏幕宽度 * 除数
                    mMaxWidth = screenWidth * divisor;
                } else {
                    // 说明数据量会超出整页的数量，需要在分一页
                    mMaxWidth = screenWidth * (divisor + 1);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 绘制流程：
         * 1：计算行和列
         * 2：计算其中一个item的宽高，作为主要的宽高使用
         * 3：计算出view的上下左右的padding
         * 4：计算出行和列的间距
         * 5：根据行和列去绘制页面
         * 6：如果翻页的话，计算出多页的宽高
         * 7：绘制多页的宽高的总宽高
         * 8：翻页的动画效果
         */
        // 计算页面的宽高
        if (mConvertView != null) {
            // 获取单个view的宽高
            mChildWidth = mConvertView.getMeasuredWidth();
            mChildHeight = mConvertView.getMeasuredHeight();
            LogUtil.e("onDraw ---> mChildWidth:" + mChildWidth + "  mChildHeight:" + mChildHeight);
        }
    }

    /**
     * 设置每页的行数
     *
     * @param row 每页指定的行数
     */
    public void setRwo(int row) {
        this.mRow = row;
    }

    /**
     * 设置每页的列数
     *
     * @param column 每页的列数
     */
    public void setColumn(int column) {
        this.mColumn = column;
    }

    /**
     * 设置行间距
     *
     * @param rowInterval 单页的具体行间距
     */
    public void setRowInterval(int rowInterval) {
        mRowInterval = rowInterval;
    }

    /**
     * 设置单页的具体列间距
     *
     * @param columnInterval 具体的列间距
     */
    public void setColumnInterval(int columnInterval) {
        mColumnInterval = columnInterval;
    }
}
