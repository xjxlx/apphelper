package com.android.helper.utils.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.helper.interfaces.lifecycle.LifecycleDestroyObserver
import com.android.helper.utils.LogUtil
import com.android.helper.utils.TextViewUtil

/**
 * @author : 流星
 * @CreateDate: 2022/11/6-14:12
 * @Description:
 */
class PopupWindowUtil {

    private val TAG = "PopupWindowUtil2"
    private var mActivity: FragmentActivity? = null
    private var mFragment: Fragment? = null
    private var mLayout: View? = null
    private var mTypeFromPage: DialogFromType? = null

    var popupWindow: PopupWindow? = null
    private var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT // 默认的宽高
    private var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mGravity = Gravity.CENTER // 默认居中显示
    private var mTouchable = true // 是否可以按下
    private var mClippingEnabled = true // 是否可以超出屏幕显示，默认true:表示可以不遮罩
    private var focusable = true // 是否拥有焦点，默认可以

    private var mDismissListener: OnDismissListener? = null
    private var mCreatedListener: ViewCreatedListener? = null
    private var mShowListener: OnShowListener? = null
    private val mCloseList: ArrayList<View> = arrayListOf()

    private val mLifecycleObserver = LifecycleDestroyObserver {
        LogUtil.e(TAG, "onDestroy")
        // 手动关闭弹窗，避免崩溃
        if (isShowing()) {
            dismiss()
        }

        if (popupWindow != null) {
            popupWindow = null
        }
    }

    fun setContentView(fragment: Fragment, view: View): PopupWindowUtil {
        this.mFragment = fragment
        this.mActivity = fragment.activity
        this.mLayout = view
        mTypeFromPage = DialogFromType.TYPE_FRAGMENT
        return this
    }

    fun setContentView(activity: FragmentActivity, view: View): PopupWindowUtil {
        this.mActivity = activity
        this.mLayout = view
        mTypeFromPage = DialogFromType.TYPE_ACTIVITY
        return this
    }

    fun setContentView(fragment: Fragment, resource: Int): PopupWindowUtil {
        val inflate = LayoutInflater.from(fragment.activity).inflate(resource, null, false)
        setContentView(fragment, inflate)
        return this
    }

    fun setContentView(activity: FragmentActivity, resource: Int): PopupWindowUtil {
        val inflate = LayoutInflater.from(activity).inflate(resource, null, false)
        setContentView(activity, inflate)
        return this
    }

    private fun initPopupWindow() {
        // 添加管理
        if (mTypeFromPage == DialogFromType.TYPE_FRAGMENT) {
            mFragment?.lifecycle?.addObserver(mLifecycleObserver)
        } else if (mTypeFromPage == DialogFromType.TYPE_ACTIVITY) {
            mActivity?.lifecycle?.addObserver(mLifecycleObserver)
        }

        // 释放掉原来的pop
        popupWindow?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            popupWindow = null
        }

        //解决android 9.0水滴屏/刘海屏有黑边的问题
        mActivity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val window = it.window
                val attributes = window.attributes
                attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                window.attributes = attributes
            }
        }

        popupWindow = PopupWindow().apply {
            this.width = mWidth // 宽度
            this.height = mHeight // 高度

            // 点击外部是否关闭popupWindow
            if (mTouchable) {
                this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            } else {
                this.setBackgroundDrawable(null)
            }

            // 焦点
            this.isFocusable = focusable
            // 设置可以点击pop以外的区域
            this.isOutsideTouchable = mTouchable
            // 设置PopupWindow可触摸
            this.isTouchable = mTouchable
            // 设置超出屏幕显示，默认为false,代表可以
            this.isClippingEnabled = mClippingEnabled

            mLayout?.let { layout ->
                // 移除原来的父类
                val parent = layout.parent
                if (parent is ViewGroup) {
                    parent.removeAllViews()
                }
                // 设置布局
                contentView = layout

                // 布局加载后的回调
                if (mCreatedListener != null) {
                    LogUtil.e(TAG, " onViewCreated ")
                    mCreatedListener?.onViewCreated(layout, this@PopupWindowUtil)
                }

                // close view
                for (item in mCloseList) {
                    item.setOnClickListener {
                        this@PopupWindowUtil.dismiss()
                    }
                }
            }

            // 关闭
            this.setOnDismissListener {
                LogUtil.e(TAG, "onDismiss")
                mDismissListener?.onDismiss(mLayout, this@PopupWindowUtil)
            }
        }
    }

    fun closeView(vararg closeView: View): PopupWindowUtil {
        for (item in closeView) {
            if (item !in mCloseList) {
                mCloseList.add(item)
            }
        }
        return this
    }

    fun closeView(vararg closeView: Int): PopupWindowUtil {
        for (item in closeView) {
            val view = mLayout?.findViewById<View>(item)
            if (view != null) {
                if (view !in mCloseList) {
                    mCloseList.add(view)
                }
            }
        }
        return this
    }

    /**
     * @param width ViewGroup.LayoutParams.WRAP_CONTENT
     */
    fun setWidth(width: Int): PopupWindowUtil {
        this.mWidth = width
        return this
    }

    /**
     * @param height ViewGroup.LayoutParams.WRAP_CONTENT
     */
    fun setHeight(height: Int): PopupWindowUtil {
        this.mHeight = height
        return this
    }

    fun setGravity(gravity: Int): PopupWindowUtil {
        this.mGravity = gravity
        return this
    }

    /**
     * @param touchable 是否消失
     * @return 点击popupWindow 外部的区域是否消失
     */
    fun setOutsideTouchable(touchable: Boolean): PopupWindowUtil {
        this.mTouchable = touchable
        return this
    }

    /**
     * @return 是否可以超出屏幕显示，false :可以，true:不可以，默认不可以
     */
    fun setClippingEnabled(clippingEnabled: Boolean): PopupWindowUtil {
        this.mClippingEnabled = clippingEnabled
        return this
    }

    fun setText(@IdRes id: Int, text: String): PopupWindowUtil {
        mLayout?.let {
            val view = it.findViewById<View>(id)
            if (view is TextView) {
                setText(view, text)
            }
        }
        return this
    }

    fun setText(textView: TextView, text: String): PopupWindowUtil {
        TextViewUtil.setText(textView, text)
        return this
    }

    fun setClickListener(@IdRes id: Int, listener: View.OnClickListener): PopupWindowUtil {
        mLayout?.let {
            val view = it.findViewById<View>(id)
            setClickListener(view, listener)
        }
        return this
    }

    fun setClickListener(view: View, listener: View.OnClickListener): PopupWindowUtil {
        view.setOnClickListener(listener)
        return this
    }

    fun setViewCreatedListener(viewCreated: ViewCreatedListener): PopupWindowUtil {
        this.mCreatedListener = viewCreated
        return this
    }

    fun setOnShowListener(showListener: OnShowListener): PopupWindowUtil {
        mShowListener = showListener
        return this
    }

    fun setOnDismissListener(dismissListener: OnDismissListener): PopupWindowUtil {
        this.mDismissListener = dismissListener
        return this
    }

    fun dismiss() {
        popupWindow?.let {
            it.dismiss()
            it.update()
        }
    }

    fun showAtLocation(view: View?) {
        showAtLocation(view, 0, 0)
    }

    fun showAtLocation(view: View?, xOff: Int, yOff: Int) {
        view?.let {
            it.post {
                popupWindow?.showAtLocation(view, mGravity, xOff, yOff)
                LogUtil.e(TAG, "onShow")
                mShowListener?.onShow(view, this@PopupWindowUtil)
            }
        }
    }

    fun showAsDropDown(view: View) {
        showAsDropDown(view, 0, 0)
    }

    fun showAsDropDown(view: View?, xOff: Int, yOff: Int) {
        view?.let {
            it.post {
                popupWindow?.showAsDropDown(view, mGravity, xOff, yOff)
                LogUtil.e(TAG, "onShow")
                mShowListener?.onShow(view, this@PopupWindowUtil)
            }
        }
    }

    fun isShowing(): Boolean {
        return if (popupWindow != null) {
            return popupWindow!!.isShowing
        } else false
    }

    fun build(): PopupWindowUtil {
        if (mLayout == null) {
            throw NullPointerException("setContentView 未调用！")
        }
        initPopupWindow()
        return this
    }

    interface ViewCreatedListener {

        fun onViewCreated(rootView: View?, popupWindow: PopupWindowUtil)
    }

    interface OnShowListener {

        fun onShow(view: View?, popupWindow: PopupWindowUtil)
    }

    interface OnDismissListener {

        fun onDismiss(view: View?, popupWindow: PopupWindowUtil)
    }

}