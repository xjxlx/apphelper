package com.android.helper.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.android.common.utils.LogUtil
import com.android.helper.utils.ScreenUtil

/**
 * @author : 流星
 * @CreateDate: 2022/11/26-23:52
 * @Description:
 */
class SideMenuView : ViewGroup {
    val TAG_CONTENT = "content"
    val TAG_MENU = "menu"
    private var mContentView: ViewGroup? = null
    private var mMenuView: ViewGroup? = null
    private var mContentViewWidth = 0
    private var mContentViewHeight = 0
    private var mMenuViewWidth = 0
    private val mMiddleValue by lazy { mMenuViewWidth / 2 }
    private val mStartX by lazy { mContentViewWidth - mMenuViewWidth }
    private var mWidthPixels: Int = 0
    private var mDx = 0
    private var mDragCallBackListener: DragCallBackListener? = null

    private val mCallBack =
        object : ViewDragHelper.Callback() {
            /*
             * 3:哪一个view可以被移动，这是一个抽象类，必须去实现，也只有在这个方法返回true的时候下面的方法才会生效
             */
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                // 不返回true就不会被移动
                // 如果这里有多个View的话，返回值改变成 return child == mDragView1;
                // 那么只有MDragView1可以被拖拽，其他View不能
                return true
            }

            /*
             * 4：先限制一下横向滑动范围，给一个最大值
             */
            override fun getViewHorizontalDragRange(child: View): Int {
                return child.measuredWidth // 只要返回大于0的值就行
            }

            // 5：限制横向滑动的范围，不能让view无限制的滑动，否则没有意义
            override fun clampViewPositionHorizontal(
                child: View,
                left: Int,
                dx: Int,
            ): Int {
                LogUtil.e(
                    "clampViewPositionHorizontal: dx: $dx  mDx:$mDx  mMenuViewWidth： $mMenuViewWidth  left：$left"
                )

                // 处理手势的拦截事件
                if (left > 0) { // 往右滑动，不拦截
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    // 往左滑动，往左滑动，都是负数，只要负数不大于 munu的宽度，都拦截
                    if (left >= -mMenuViewWidth) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

                // 限制左侧的边距
                var mLeftValue = left
                if (child == mContentView) {
                    // 防止不够一屏的时候，进行移动
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
                LogUtil.e(
                    "mLeftValue:" +
                        mLeftValue +
                        " left:" +
                        left +
                        " mDx: " +
                        mDx +
                        "  mContentViewWidth: " +
                        mContentViewWidth +
                        "  ++： " +
                        (mContentViewWidth + mDx) +
                        "  mContentView Left:" +
                        mContentView?.left +
                        "  mMenuViewWidth：" +
                        mMenuViewWidth
                )
                return mLeftValue
            }

            /*
             * 6：view滑动的改变，
             */
            override fun onViewPositionChanged(
                changedView: View,
                left: Int,
                top: Int,
                dx: Int,
                dy: Int,
            ) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                mDx += dx
                LogUtil.e(
                    "onViewPositionChanged:$left  dx:$mDx" +
                        "  mMenuViewWidth: " +
                        mMenuViewWidth +
                        "  left: " +
                        left
                )

                if (changedView == mContentView) {
                    val menuScrollLeft = mContentViewWidth + mDx
                    val menuScrollTop = mContentView?.top
                    val menuScrollRight = menuScrollLeft + mMenuViewWidth
                    val menuScrollBottom = mContentViewHeight
                    mMenuView?.layout(
                        menuScrollLeft,
                        menuScrollTop!!,
                        menuScrollRight,
                        menuScrollBottom,
                    )
                } else if (changedView == mMenuView) {
                    // 左侧  =
                    val contentScrollLeft = mDx
                    val contentScrollTop = mContentView?.top
                    val contentScrollRight = contentScrollLeft + mContentViewWidth
                    val contentScrollBottom = mContentViewHeight
                    LogUtil.e(
                        "mContentView:  mDx:" +
                            mDx +
                            "  contentScrollLeft: " +
                            contentScrollLeft +
                            " contentScrollRight:" +
                            contentScrollRight
                    )
                    mContentView?.layout(
                        contentScrollLeft,
                        contentScrollTop!!,
                        contentScrollRight,
                        contentScrollBottom,
                    )
                }
            }

            /** 7：手指松开时候的处理 */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                val left = releasedChild.left
                if (releasedChild == mContentView) {
                    if (left < -mMiddleValue) {
                        open()
                    } else if (left > -mMiddleValue) {
                        close()
                    }
                } else if (releasedChild == mMenuView) {
                    if (left < mStartX + mMiddleValue) {
                        open()
                    } else {
                        close()
                    }
                }
            }

            /** 状态的改变 */
            override fun onViewDragStateChanged(state: Int) {
                super.onViewDragStateChanged(state)
                LogUtil.e(" state ::: " + state)
                mDragCallBackListener?.let {
                    // 1:拖拽中 2：停止
                    if (state == ViewDragHelper.STATE_DRAGGING) {
                        it.onStatusChange(1)
                    } else {
                        it.onStatusChange(2)
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
            }
        }

        val maxWidth = mMenuViewWidth + mContentViewWidth
        val maxHeight = mContentViewHeight
        setMeasuredDimension(maxWidth, maxHeight)
    }

    private var mContentMarginLeft = 0

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //        val marginLayoutParams = layoutParams as MarginLayoutParams
        //        val rightMargin = marginLayoutParams.rightMargin

        mContentView?.let {
            if (mContentMarginLeft <= 0) {
                val lp = it.layoutParams as MarginLayoutParams
                mContentMarginLeft = lp.marginStart
            }

            it.layout(
                mContentMarginLeft + it.left,
                it.top,
                it.left + mContentViewWidth,
                it.top + mContentViewHeight,
            )

            mMenuView?.layout(it.right, it.top, it.right + mMenuViewWidth, it.bottom)
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
        mMenuView?.let { mMenuViewWidth = it.measuredWidth }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 1 把viewDragHelper 交给interceptTouchEvent 去处理拦截的机制
        return mViewDragHelper.shouldInterceptTouchEvent(ev!!)
    }

    fun restore() {
        mContentView?.let {
            it.layout(0, it.top, it.measuredWidth, it.measuredHeight)
            mMenuView?.let { menu ->
                val left = it.measuredWidth
                val right = left + menu.measuredWidth
                LogUtil.e(" --- left: $left  right: $right")
                menu.layout(left, it.top, right, it.measuredHeight)
                // 强制归零
                mDx = 0
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 2 把viewDragHelper 交给touchEvent去使用，让viewDragHelper去实际的处理事件，但是这里必须返回为true，不然不会去执行
        mViewDragHelper.processTouchEvent(event!!)
        return true
    }

    private fun close() {
        // settleCapturedViewAt 尽量不要使用，否则会很麻烦，要计算每个view的滑动，最好用smoothSlideViewTo去控制
        // mViewDragHelper.settleCapturedViewAt(0,0)
        mContentView?.let {
            mViewDragHelper.settleCapturedViewAt(0, it.top)
            // mViewDragHelper.smoothSlideViewTo(it, 0, it.top);
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    fun open() {
        mContentView?.let {
            mViewDragHelper.smoothSlideViewTo(it, -mMenuViewWidth, it.top)
            // mViewDragHelper.settleCapturedViewAt(-mMenuViewWidth, it.top)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        // 如果动画正在进行中，就进行view的绘制
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    fun setMenuClickListener(listener: SideMenuClickListener?) {
        mMenuView?.let {
            it.setOnClickListener { listener?.onClick(this@SideMenuView, this) }
        }
    }

    fun setContentClickListener(listener: SideMenuClickListener?) {
        mContentView?.let {
            it.setOnClickListener { listener?.onClick(this@SideMenuView, this) }
        }
    }

    fun setDragCallBackListener(listener: DragCallBackListener?) {
        mDragCallBackListener = listener
    }

    interface SideMenuClickListener {
        fun onClick(sideMenu: SideMenuView, view: View)
    }

    interface DragCallBackListener {
        /** 1:拖拽中 2：停止 */
        fun onStatusChange(status: Int)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams =
        MarginLayoutParams(p)

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams =
        MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
}
