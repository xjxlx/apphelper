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

        // 获取导航的管理器，应该通过fragmentManager 去获取
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frg_content2) as NavHostFragment
//        // 从NavHostFragment 中去获取 导航管理器
//        val navController = navHostFragment.navController

//        navController.navigate(R.id.action_fragment1_to_fragment2)

    }
}