package android.helper.ui.activity.widget

import android.helper.R
import com.android.helper.base.BaseTitleActivity

class TouchUnlockActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_touch_unlock
    }

    override fun initView() {
        super.initView()
        setTitleContent("自定义触摸解锁效果")
    }

}