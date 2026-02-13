package com.android.helper.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import com.android.common.utils.LogUtil
import kotlin.math.abs

/**
 * @author : 流星
 * @CreateDate: 2022/12/3-23:11
 * @Description:
 */
class SideMenuView2(
    context: Context,
    attributeSet: AttributeSet
) : ViewGroup(context, attributeSet) {
    val TAG_CONTENT = "content"
    val TAG_MENU = "menu"
    private var mContentView: View? = null
    private var mMenuView: View? = null
    private var mContentViewWidth = 0
    private var mContentViewHeight = 0
    private var mMenuViewWidth = 0
    private var mScroller: Scroller
    private var mContentMarginLeft = 0
    private var mLeftBorder = 0 // 左侧边界

    init {
        mScroller = Scroller(context)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        for (index in 0 until childCount) {
            val childAt = getChildAt(index)
            if (childAt == mContentView) {
                mContentViewWidth = childAt.measuredWidth
                mContentViewHeight = childAt.measuredHeight
            } else if (childAt == mMenuView) {
                mMenuViewWidth = childAt.measuredWidth
            }
        }

        val maxWidth = mContentViewWidth
        val maxHeight = mContentViewHeight
        setMeasuredDimension(maxWidth, maxHeight)
        mLeftBorder = mMenuViewWidth / 2
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        LogUtil.run { e(" le: $l  r: $r") }
        mContentView?.let {
            if (mContentMarginLeft <= 0) {
                val lp = it.layoutParams as MarginLayoutParams
                mContentMarginLeft = lp.marginStart
            }

            it.layout(
                mContentMarginLeft + it.left,
                it.top,
                it.left + mContentViewWidth,
                it.top + mContentViewHeight
            )
            mMenuView?.layout(it.right, it.top, it.right + mMenuViewWidth, it.bottom)
        }
        LogUtil.e(" le: " + mContentView?.left)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mContentView = findViewWithTag(TAG_CONTENT)
        mMenuView = findViewWithTag(TAG_MENU)
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        mContentView?.let {
            mContentViewWidth = it.measuredWidth
            mContentViewHeight = it.measuredHeight
        }
        mMenuView?.let { mMenuViewWidth = it.measuredWidth }

        mLeftBorder = mMenuViewWidth / 2
    }

    private var mDx: Int = 0
    private var mGestureDetector =
        GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    mDx = distanceX.toInt()

                    mContentView?.let {
                        val left = it.left
                        LogUtil.e(
                            "left: " +
                                left +
                                "  dx: " +
                                mDx +
                                "  （left - mDx）" +
                                (left - mDx) +
                                "  mMenuViewWidth: " +
                                mMenuViewWidth +
                                " mContentMarginLeft: " +
                                mContentMarginLeft
                        )
                        if (mDx > 0) { // 向左
                            if (abs(left) + mDx < (mMenuViewWidth)) {
                                LogUtil.e("<----")
                                it.layout(
                                    it.left - mDx,
                                    it.top,
                                    it.right - mDx,
                                    it.bottom
                                )
                                mMenuView?.layout(
                                    it.right,
                                    it.top,
                                    it.right + mMenuViewWidth,
                                    it.bottom
                                )
                            }
                        } else { // 向右
                            if (left - mDx <= mContentMarginLeft) {
                                LogUtil.e("---->")
                                it.layout(
                                    it.left - mDx,
                                    it.top,
                                    it.right - mDx,
                                    it.bottom
                                )
                                mMenuView?.layout(
                                    it.right,
                                    it.top,
                                    it.right + mMenuViewWidth,
                                    it.bottom
                                )
                            }
                        }
                    }
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }
            }
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { mGestureDetector.onTouchEvent(event) }
        if (
            event?.action == MotionEvent.ACTION_CANCEL ||
            event?.action == MotionEvent.ACTION_UP
        ) {
            mContentView?.let {
                val left = it.left
                LogUtil.e("up --- left:" + left + "  mDx: " + mDx)
                if (abs(left) >= mLeftBorder) {
                    open()
                    LogUtil.e("---> open: ")
                } else {
                    close()
                    LogUtil.e("---> close: ")
                }
            }
        }
        return true
    }

    private fun open() {
        mContentView?.let {
            val leftValue = -(mMenuViewWidth - mContentMarginLeft)
            it.layout(leftValue, it.top, leftValue + mContentViewWidth, it.bottom)
            mMenuView?.layout(it.right, it.top, it.right + mMenuViewWidth, it.bottom)
        }
    }

    private fun close() {
        mContentView?.let {
            it.layout(
                mContentMarginLeft,
                it.top,
                mContentMarginLeft + mContentViewWidth,
                it.bottom
            )
            mMenuView?.layout(it.right, it.top, it.right + mMenuViewWidth, it.bottom)
            mMenuView?.layout(it.right, it.top, it.right + mMenuViewWidth, it.bottom)
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams(): LayoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
}
