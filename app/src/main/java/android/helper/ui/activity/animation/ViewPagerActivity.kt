package android.helper.ui.activity.animation

import android.helper.R
import android.helper.base.BaseTitleActivity
import kotlinx.android.synthetic.main.activity_view_pager.*

/**
 * 自定义viewpager的类
 */
class ViewPagerActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_view_pager
    }

    override fun initData() {
        super.initData()
        setTitleContent("自定义ViewPager的类")

        banner_view.setDateListResource(intArrayOf(R.mipmap.icon_banner_1, R.mipmap.icon_banner_2,
                R.mipmap.icon_banner_3, R.mipmap.icon_banner_4))

        banner_view.setIndicatorView(fl_viewpager_indicator, 30, R.drawable.selector_banner_indicator_default)
        banner_view.setLoop(true,0)


        btn_reset.setOnClickListener {
            banner_view.reset()
        }
    }

}