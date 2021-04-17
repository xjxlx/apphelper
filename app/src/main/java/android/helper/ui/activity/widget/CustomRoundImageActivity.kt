package android.helper.ui.activity.widget

import android.helper.R
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.photo.GlideUtil
import kotlinx.android.synthetic.main.activity_custom_round_image.*

class CustomRoundImageActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_custom_round_image
    }

    override fun initData() {
        super.initData()
        setTitleContent("自定义任意圆角任意图形")

        val url1 = "http://file.jollyeng.com/picture_book/201805/When I grow up.png";
        val url = "http://file.jollyeng.com/picture_book/201809/1537253778.png";
        GlideUtil.loadView(mContext, url, rv_image, R.drawable.abc)
    }

}