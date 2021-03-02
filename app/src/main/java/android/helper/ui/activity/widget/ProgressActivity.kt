package android.helper.ui.activity.widget

import android.text.TextUtils
import android.view.View
import android.helper.R
import android.helper.base.BaseTitleActivity
import android.helper.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_progress2.*

class ProgressActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_progress2
    }

    override fun initData() {
        super.initData()

        setTitleContent("自定义进度条")

        // 进度条1
        btn1.setOnClickListener { spv.startAnimation() }
        btn2.setOnClickListener { spv.cancelAnimation() }

        // 进度条2
        btn.setOnClickListener {
            val toString = ed_input.text.toString()
            if (TextUtils.isEmpty(toString)) {
                ToastUtil.show("数据不能为空")
                return@setOnClickListener
            }
            pb2.setCharging(true)
            pb2.startAnimation(toString.toInt())

        }

    }
}