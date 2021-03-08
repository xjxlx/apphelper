package android.helper.ui.activity.animation

import android.helper.R
import android.helper.base.BaseTitleActivity
import android.helper.utils.LogUtil
import android.helper.widget.BannerView
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
        banner_view.setLoop(true, 0)
        banner_view.setBannerChangeListener(object : BannerView.BannerChangeListener {
            override fun onSelected(position: Int) {
                LogUtil.e("当前的角标为：$position")
            }
        })

        btn_reset.setOnClickListener {
            banner_view.reset()
        }
    }

}