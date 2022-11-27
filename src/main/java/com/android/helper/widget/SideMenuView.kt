package com.android.helper.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.android.helper.utils.LogUtil
import com.android.helper.utils.ScreenUtil

/**
 * @author : 流星
 * @CreateDate: 2022/11/26-23:52
 * @Description:
 */
class SideMenuView : ViewGroup {
    val TAG_CONTENT = "content"
    val TAG_MENU = "menu"
    private var mContentView: View? = null
    private var mMenuView: View? = null
    private var mContentViewWidth = 0
    private var mContentViewHeight = 0
    private var mMenuViewWidth = 0
    private var mMenuViewHeight = 0
    private val mMiddleValue by lazy {
        mMenuViewWidth / 2
    }
    private val mStartX by lazy {
        mContentViewWidth - mMenuViewWidth
    }
    private var mWidthPixels: Int = 0

    private val mCallBack = object : ViewDragHelper.Callback() {
        /*
        * 3:哪一个view可以被移动，这是一个抽象类，必须去实现，也只有在这个方法返回true的时候下面的方法才会生效
        */
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            //不返回true就不会被移动
            //如果这里有多个View的话，返回值改变成 return child == mDragView1;
            //那么只有MDragView1可以被拖拽，其他View不能
            return true
        }

        /*
        * 4：先限制一下横向滑动范围，给一个最大值
        */
        override fun getViewHorizontalDragRange(child: View): Int {
            return child.measuredWidth //只要返回大于0的值就行
        }

        // 5：限制横向滑动的范围，不能让view无限制的滑动，否则没有意义
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            // 限制左侧的边距
            var mLeftValue = left
            if (child == mContentView) {
                //防止不够一屏的时候，进行移动
                if (mMenuViewWidth + mContentViewWidth <= mWidthPixels) {
                    mLeftValue = 0
                } else {
                    // 左滑的限制
                    if (mLeftValue < -mMenuViewWidth) {
                        mLeftValue = -mMenuViewWidth
                    }
                }

                // 禁止右侧滑动
                if (mLeftValue > 0) {
                    mLeftValue = 0
                }
            }
            if (child == mMenuView) {
                // 左侧的限制
                if (mMenuViewWidth + mContentViewWidth <= mWidthPixels) {
                    mLeftValue = mContentViewWidth
                } else {
                    if (mLeftValue <= (mContentViewWidth - mMenuViewWidth)) {
                        mLeftValue = mContentViewWidth - mMenuViewWidth
                    }
                }

                // 右侧的滑动限制
                if (mLeftValue > mContentViewWidth) {
                    mLeftValue = mContentViewWidth
                }
            }
            LogUtil.e("mLeftValue:" + mLeftValue + " left:" + left + " mDx: " + mDx + "  mContentViewWidth: " + mContentViewWidth + "  ++： " + (mContentViewWidth + mDx) + "  mContentView Left:" + mContentView?.left + "  mMenuViewWidth：" + mMenuViewWidth)
            return mLeftValue
        }

        /*
        * 6：view滑动的改变，
        */
        private var mDx = 0
        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            mDx += dx
            LogUtil.e("onViewPositionChanged:$left  dx:$mDx")

            if (changedView == mContentView) {
                val menuScrollLeft = mContentViewWidth + mDx
                val menuScrollTop = mMenuView?.top
                val menuScrollRight = menuScrollLeft + mMenuViewWidth
                val menuScrollBottom = mMenuViewHeight
                mMenuView?.layout(menuScrollLeft, menuScrollTop!!, menuScrollRight, menuScrollBottom)
            } else if (changedView == mMenuView) {
                // 左侧  =
                val contentScrollLeft = mDx
                val contentScrollTop = mContentView?.top
                val contentScrollRight = contentScrollLeft + mContentViewWidth
                val contentScrollBottom = mContentViewHeight
                LogUtil.e("mContentView:  mDx:" + mDx + "  contentScrollLeft: " + contentScrollLeft + " contentScrollRight:" + contentScrollRight)
                mContentView?.layout(contentScrollLeft, contentScrollTop!!, contentScrollRight, contentScrollBottom)
            }
        }

        /**
         *
         * 7：手指松开时候的处理
         */
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            val left = releasedChild.left
            if (releasedChild == mContentView) {
                // left 都是负数 ，left  < -mMenuViewWidth / 2  向左，打开
                // left >  -mMenuViewWidth/2 向右， 关闭
                if (left < -mMiddleValue) {
                    open()
                } else if (left > -mMiddleValue) {
                    close()
                }
            } else if (releasedChild == mMenuView) {
                // left 是整数
                // left < startx +middleValue  打开
                // left > starx +middleVale 关闭
                if (left < mStartX + mMiddleValue) {
                    open()
                } else {
                    close()
                }
            }
        }
    }

    private val mViewDragHelper by lazy { ViewDragHelper.create(this, mCallBack) }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        mWidthPixels = ScreenUtil.getScreenWidth(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        for (index in 0 until childCount) {
            val childAt = getChildAt(index)
            if (childAt == mContentView) {
                mContentViewWidth = childAt.measuredWidth
                mContentViewHeight = childAt.measuredHeight
            } else if (childAt == mMenuView) {
                mMenuViewWidth = childAt.measuredWidth
                mMenuViewHeight = childAt.measuredHeight
            }
        }
        val maxWidth = mMenuViewWidth + mContentViewWidth
        val maxHeight = mMenuViewHeight.coerceAtLeast(mContentViewHeight)
        setMeasuredDimension(maxWidth, maxHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mContentView?.let {
            mContentView?.layout(l, t, mContentViewWidth, mContentViewHeight)
            mMenuView?.layout(it.right, t, it.right + mMenuViewWidth, mContentViewHeight)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mContentView = findViewWithTag(TAG_CONTENT)
        mMenuView = findViewWithTag(TAG_MENU)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mContentView?.let {
            mContentViewWidth = it.measuredWidth
            mContentViewHeight = it.measuredHeight
        }
        mMenuView?.let {
            mMenuViewWidth = it.measuredWidth
            mMenuViewHeight = it.measuredHeight
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 1 把viewDragHelper 交给interceptTouchEvent 去处理拦截的机制
        return mViewDragHelper.shouldInterceptTouchEvent(ev!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 2 把viewDragHelper 交给touchEvent去使用，让viewDragHelper去实际的处理事件，但是这里必须返回为true，不然不会去执行
        mViewDragHelper.processTouchEvent(event!!)
        return true
    }

    private fun close() {
        // settleCapturedViewAt 尽量不要使用，否则会很麻烦，要计算每个view的滑动，最好用smoothSlideViewTo去控制
        mContentView?.let {
            mViewDragHelper.smoothSlideViewTo(it, 0, it.top);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private fun open() {
        mContentView?.let {
            mViewDragHelper.smoothSlideViewTo(it, -mMenuViewWidth, it.top);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        // 如果动画正在进行中，就进行view的绘制
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            invalidate()
        }
    }
}