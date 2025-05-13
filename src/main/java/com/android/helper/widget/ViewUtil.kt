package com.android.helper.widget

import android.view.View
import android.view.ViewGroup
import com.android.common.utils.ConvertUtil

object ViewUtil {
    /**
     * 设置view的margin的数据
     *
     * @param view  view的对象，一般都是viewGrounp
     * @param array 左上右下的顺序
     */
    @JvmStatic
    fun setMargin(
        view: View?,
        array: IntArray?
    ) {
        if (view == null) {
            return
        }
        if ((array == null) || (array.size != 4)) {
            return
        }
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            val left = array[0]
            val top = array[1]
            val right = array[2]
            val bottom = array[3]
            marginLayoutParams.setMargins(left, top, right, bottom)
            view.layoutParams = marginLayoutParams
        }
    }

    /**
     * @param view   指定的view
     * @param bottom 需要设置的bottom的高度，单位是dp
     */
    @JvmStatic
    fun setBottomMargin(
        view: View,
        bottom: Int
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.bottomMargin = bottom
            view.layoutParams = marginLayoutParams
        }
    }

    @JvmStatic
    fun setTopMargin(
        view: View?,
        topMargin: Float
    ) {
        if (view != null) {
            val layoutParams = view.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                val marginLayoutParams = layoutParams
                marginLayoutParams.topMargin = ConvertUtil.dp(view.context, topMargin).toInt()
                view.layoutParams = marginLayoutParams
            }
        }
    }

    fun getMarginEnd(view: View?): Int {
        if (view != null) {
            val layoutParams = view.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                return layoutParams.marginEnd
            }
        }
        return 0
    }

    fun getMarginStart(view: View?): Int {
        if (view != null) {
            val layoutParams = view.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                return layoutParams.marginStart
            }
        }
        return 0
    }

    @JvmStatic
    fun setLeftMargin(
        view: View,
        topMargin: Float
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.leftMargin = ConvertUtil.dp(view.context, topMargin).toInt()
            view.layoutParams = marginLayoutParams
        }
    }

    fun setRightMargin(
        view: View,
        topMargin: Float
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.rightMargin = ConvertUtil.dp(view.context, topMargin).toInt()
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginStart(
        view: View,
        marginStart: Float
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.marginStart = ConvertUtil.dp(view.context, marginStart).toInt()
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginEnd(
        view: View,
        marginEnd: Float
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.marginEnd = ConvertUtil.dp(view.context, marginEnd).toInt()
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginLeftPx(
        view: View,
        marginLeft: Int
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.leftMargin = marginLeft
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginTopPx(
        view: View,
        marginTop: Int
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.topMargin = marginTop
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginRightPx(
        view: View,
        marginRight: Int
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.rightMargin = marginRight
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginBottomPx(
        view: View,
        marginBottom: Int
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.bottomMargin = marginBottom
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginStartPx(
        view: View,
        marginStart: Int
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.marginStart = marginStart
            view.layoutParams = marginLayoutParams
        }
    }

    fun setMarginEndPx(
        view: View,
        marginEnd: Int
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            marginLayoutParams.marginEnd = marginEnd
            view.layoutParams = marginLayoutParams
        }
    }

    /**
     * 设置view的状态
     *
     * @param view       view
     * @param visibility 状态
     */
    fun setVisibility(
        view: View?,
        visibility: Int
    ) {
        if (view != null) {
            val viewVisibility = view.visibility
            if (visibility != viewVisibility) {
                view.visibility = visibility
            }
        }
    }

    /**
     * @param view    指定view
     * @param visible true:可见，false:不可见
     */
    fun setViewVisible(
        view: View?,
        visible: Boolean
    ) {
        if (view != null) {
            val visibility = view.visibility
            if (visible) {
                if (visibility != View.VISIBLE) {
                    view.visibility = View.VISIBLE
                }
            } else {
                if (visibility != View.GONE) {
                    view.visibility = View.GONE
                }
            }
        }
    }

    @JvmStatic
    fun getMarginLeft(view: View?): Int {
        if (view != null) {
            val layoutParams = view.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                return layoutParams.leftMargin
            }
        }
        return 0
    }

    @JvmStatic
    fun getMarginRight(view: View?): Int {
        if (view != null) {
            val layoutParams = view.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                return layoutParams.rightMargin
            }
        }
        return 0
    }

    fun getMarginTop(view: View?): Int {
        if (view != null) {
            val layoutParams = view.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                return layoutParams.topMargin
            }
        }
        return 0
    }

    fun getMarginBottom(view: View?): Int {
        if (view != null) {
            val layoutParams = view.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                return layoutParams.bottomMargin
            }
        }
        return 0
    }
}
