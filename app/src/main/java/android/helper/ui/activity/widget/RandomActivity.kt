package android.helper.ui.activity.widget

import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.helper.R
import android.helper.base.BaseTitleActivity
import android.helper.utils.TextViewUtil
import android.helper.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_random.*

/**
 * 随机布局的activity
 */
class RandomActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_random
    }

    override fun initData() {
        super.initData()

        setTitleContent("随机数的布局")

        val listData = arrayListOf<String>()

        listData.add("this")
        listData.add("in")
        listData.add("water")
        listData.add("live")
        listData.add("Many")
        listData.add("gnimals")

        val textView = TextView(mContext)
        textView.textSize = 12f
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue_10))
        textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_1))
        textView.gravity = Gravity.BOTTOM
        TextViewUtil.setTextFont(mContext, textView, "DINCondensedBold.ttf")

        rl_layout.textView = textView
        rl_layout.setRandomRotatingView(true)


        rl_layout.setRandomClickListener { _, position: Int, t ->
            ToastUtil.show("position:$position   value:$t")
        }

        btn_start.setOnClickListener {
            rl_layout.setDataList(listData)
        }
    }
}