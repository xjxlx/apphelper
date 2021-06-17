package android.helper.ui.activity.widget

import android.helper.R
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.ConvertUtil
import com.android.helper.utils.LogUtil
import com.android.helper.utils.TextViewUtil
import com.android.helper.utils.ToastUtil
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

        val inflate = layoutInflater.inflate(R.layout.item_random, null)
        val randomContent = inflate.findViewById<TextView>(R.id.tv_random_content)
        TextViewUtil.setTextFont(mContext, randomContent, "DINCondensedBold.ttf")

        rl_layout.textView = randomContent
        rl_layout.setRandomRotatingView(true)


        rl_layout.setRandomClickListener { _, position: Int, t ->
            ToastUtil.show("position:$position   value:$t")
        }

        btn_start.setOnClickListener {
            rl_layout.setDataList(listData)
        }
    }
}