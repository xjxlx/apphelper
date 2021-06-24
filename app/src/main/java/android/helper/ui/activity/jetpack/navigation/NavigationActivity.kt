package android.helper.ui.activity.jetpack.navigation

import android.helper.R
import com.android.helper.base.BaseTitleActivity

/**
 * Navigation导航的页面
 */
class NavigationActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_navigation
    }

    override fun initView() {
        super.initView()
        setTitleContent("Navigation导航")
    }
}