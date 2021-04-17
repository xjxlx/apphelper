package android.helper.ui.activity.animation

import android.helper.R
import android.util.Log
import com.android.helper.base.BaseTitleActivity
import com.android.helper.interfaces.listener.BannerChangeListener
import com.android.helper.utils.ToastUtil
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

        banner_view.setDataList(intArrayOf(R.mipmap.icon_banner_1, R.mipmap.icon_banner_2,
                R.mipmap.icon_banner_3, R.mipmap.icon_banner_4))

        banner_view.setIndicatorView(fl_viewpager_indicator, 30, R.drawable.selector_banner_indicator_default)
        banner_view.setAutoLoop(true, 0)
        banner_view.setBannerChangeListener(object : BannerChangeListener {
            override fun onSelector(position: Int) {
                Log.e(TAG, "onSelector: $position")
            }
        })

        banner_view.setBannerClickListener { view, position ->
            ToastUtil.show("position:" + position)
        }


        btn_reset.setOnClickListener {
            banner_view.reset()
        }
    }

}