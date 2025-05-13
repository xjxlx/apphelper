package com.android.helper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import androidx.annotation.LayoutRes;
import com.android.common.utils.LogUtil;
import com.android.helper.utils.ScreenUtil;
import java.util.ArrayList;

/** 可以滑动的九宫格View */
public class PageView extends ViewGroup {

  private final int mViewRows = 2; // view的行数
  private final int mViewColumn = 4; // view的列数
  private final int mRowInterval = 30; // view行的间距
  private Context mContext;
  private int mResource; // 资源文件
  private int mScreenWidth; // 屏幕的宽度
  private int mMarginLeft;
  private int mMarginRight;
  private int mLayoutMeasuredWidth; // layout的宽度
  private int mLayoutMeasuredHeight; // layout的高度
  private int mScrollPresetValue; // 滑动预设的值
  private int mViewIntervalWidth; // 宽度的间距
  private int mOnePageCount; // 一页的个数
  private int mPageCount; // 一共有几页
  private GestureDetector mDetector;

  private int isToLeft; // 1：向左，2：向右 ，其他不处理
  private Scroller mScroller;
  private float mScrollInterval; // 滑动的间距
  private int mViewMeasuredHeight; // view的高度
  private int mViewMeasuredWidth; // view的宽度
  private ItemClickListener mListener;
  private float mInterceptDown;
  private float mInterceptDx; // 移动的偏移量
  private float mDownDx;
  private float mDownStartX;
  private int mCurrentPage; // 当前是第几页

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
    mDetector =
        new GestureDetector(
            context,
            new GestureDetector.SimpleOnGestureListener() {
              @Override
              public boolean onScroll(
                  MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                LogUtil.e("onScroll:" + mOnePageCount);
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
    // view的宽度 = 屏幕的宽度 - 左右的margin值
    mLayoutMeasuredWidth = mScreenWidth - mMarginLeft - mMarginRight;
    // 预设的值
    // mScrollPresetValue = ViewConfiguration.get(mContext).getScaledTouchSlop();
    mScrollPresetValue = (mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight()) / 4;
  }

  /**
   * @param dataList 设置数据
   */
  public <T> void setDataList(ArrayList<T> dataList) {
    getMargin();
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    if ((dataList != null) && (dataList.size() > 0)) {
      int size = dataList.size();
      if (mResource != 0) {
        for (int i = 0; i < size; i++) {
          View view = LayoutInflater.from(mContext).inflate(mResource, null);
          view.setLayoutParams(params);
          view.setTag(i);
          if (mListener != null) {
            int finalI = i;
            int finalI1 = i;
            view.setOnClickListener(
                new OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    mListener.onItemClickListener(finalI, view, dataList.get(finalI1));
                  }
                });
          }
          addView(view);
        }
      }
      // 一共有多少页
      int value = size / mOnePageCount;
      int i = size % mOnePageCount;
      if (i > 0) {
        mPageCount = (value + 1);
      } else {
        mPageCount = value;
      }
    }
    requestLayout();
  }

  public void setLayout(@LayoutRes int resource) {
    this.mResource = resource;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    getMargin();
    LogUtil.e(
        "screenWidth:"
            + mScreenWidth
            + " marginLeft:"
            + mMarginLeft
            + "  marginRight: "
            + mMarginRight
            + "  result:"
            + mLayoutMeasuredWidth);
    int mChildCount = getChildCount();
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
        mLayoutMeasuredHeight = mViewMeasuredHeight * mViewRows; // 说明大于一行就需要叠加view的高度
      } else {
        mLayoutMeasuredHeight = mViewMeasuredHeight; // 小于一行，一个view的高度就可以了
      }
      // 加入行高
      mLayoutMeasuredHeight += ((Math.abs(mViewRows - 1)) * mRowInterval);
      // 重新设置
    }
    setMeasuredDimension(
        mLayoutMeasuredWidth, mLayoutMeasuredHeight + getPaddingTop() + getPaddingBottom());
    LogUtil.e("w:" + mLayoutMeasuredWidth + "  h:" + mViewMeasuredHeight);
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
      mViewIntervalWidth =
          (mLayoutMeasuredWidth
                  - getPaddingLeft()
                  - getPaddingRight()
                  - (mViewColumn * mViewMeasuredWidth))
              / (mViewColumn + 1);
      int positionForColumn = getPositionForColumn(i);
      int positionForRow = getPositionForRow(i);
      int page = getPageForPosition(i);
      // （间距 *（列数 +1）） + （列数 * view的宽度）+ paddingLeft = 一屏view的宽度
      left +=
          (((positionForColumn + 1) * mViewIntervalWidth)
              + (positionForColumn * mViewMeasuredWidth)
              + getPaddingLeft());
      // 增加 页数 =（屏幕的宽度 -左右的padding - 间距）
      left +=
          (page
              * ((mLayoutMeasuredWidth - getPaddingLeft() - getPaddingRight())
                  - mViewIntervalWidth));
      right = left + mViewMeasuredWidth;
      top =
          positionForRow * mViewMeasuredHeight + (positionForRow * mRowInterval) + getPaddingTop();
      bottom = top + mViewMeasuredHeight;
      childAt.layout(left, top, right, bottom);
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        /*
         * 此处必须要增加手势识别器的事件，因为在move的时候，已经做了返回，此处如果不做的，会固定返回false，导致手势的动作被拦截，导致出现异常情况
         */
        mDetector.onTouchEvent(ev);
        mInterceptDown = ev.getX();
        break;
      case MotionEvent.ACTION_MOVE:
        float x = ev.getX();
        float dx = Math.abs(x - mInterceptDown);
        mInterceptDx += dx;
        if (mInterceptDx > 0) {
          // 此处是为了区分是点击事件还是移动的时间，让移动和点击同时进行，因为点击事件是在移动事件的后面，所以返回为true,就是要中断点击的事件，让滑动的事件继续进行
          return true;
        }
        break;
      case MotionEvent.ACTION_UP:
        // 每次都要清空数据
        mInterceptDx = 0;
        break;
    }
    return super.onInterceptTouchEvent(ev);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    mDetector.onTouchEvent(event);
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        float startX = event.getX();
        mDownDx = startX;
        mDownStartX = startX;
        // 求出当前的页面是哪一个page
        mCurrentPage =
            getScrollX()
                / (mLayoutMeasuredWidth
                    - getPaddingLeft()
                    - mViewIntervalWidth
                    - getPaddingRight());
        break;
      case MotionEvent.ACTION_MOVE:
        float eventX = event.getX();
        float dx = eventX - mDownStartX;
        // 防止手指抖动的设置
        if (dx < -0.5) {
          isToLeft = 1;
        } else if (dx > 1) {
          isToLeft = 2;
        }
        // 右侧的边距
        float rightInterval =
            (mPageCount - 1)
                * (mLayoutMeasuredWidth
                    - mViewIntervalWidth
                    - getPaddingLeft()
                    - getPaddingRight());
        int scrollX = getScrollX();
        if (scrollX < 0) {
          scrollTo(0, 0);
        } else if (scrollX >= (rightInterval)) {
          scrollTo((int) rightInterval, 0);
        }
        LogUtil.e(
            "isToLeft:" + isToLeft + "   sx:" + mDownStartX + "  ex:" + eventX + "   rx:" + dx);
        mDownStartX = eventX; // 这句话的含义是为了更快的测量出来手指的方向
        break;
      case MotionEvent.ACTION_UP:
        LogUtil.e("mScrollInterval:" + mScrollInterval + "   mCurrentPage:" + mCurrentPage);
        float dxUp = event.getX();
        mScrollInterval = dxUp - mDownDx;
        int scrollX1 = getScrollX();
        if (isToLeft == 1) {
          if (Math.abs(mScrollInterval) >= mScrollPresetValue) {
            if (mCurrentPage < mPageCount - 1) {
              mCurrentPage += 1;
            }
          }
        } else if (isToLeft == 2) {
          if (mScrollInterval >= mScrollPresetValue) {
            if (mCurrentPage > 0) {
              mCurrentPage -= 1;
            }
          }
        }
        startScrollX(
            scrollX1,
            mCurrentPage
                * (mLayoutMeasuredWidth
                    - getPaddingLeft()
                    - getPaddingRight()
                    - mViewIntervalWidth));
        // 清空数据
        isToLeft = 0;
        mScrollInterval = 0;
        break;
    }
    return true;
  }

  private void startScrollX(int scrollX, int target) {
    LogUtil.e("page:" + mCurrentPage);
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

  /**
   * 必须在设置数据前面设置
   *
   * @param onClickListener 监听器
   * @param <T> 泛型类型
   */
  public <T> void setOnItemClickListener(ItemClickListener<T> onClickListener) {
    this.mListener = onClickListener;
  }

  @Override
  public void computeScroll() {
    super.computeScroll();
    if (mScroller.computeScrollOffset()) {
      scrollTo(mScroller.getCurrX(), 0);
      invalidate();
    }
  }

  public interface ItemClickListener<T> {
    void onItemClickListener(int position, View view, T t);
  }
}
