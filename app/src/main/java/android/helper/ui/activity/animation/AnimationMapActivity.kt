package android.helper.ui.activity.animation

import android.helper.R
import android.helper.base.BaseTitleActivity
import android.helper.utils.ToastUtil
import android.view.View

class AnimationMapActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_animation_map
    }

    override fun initData() {
        super.initData()
        setTitleContent("自定义动画的集合")

        setonClickListener(
                R.id.tv_gif,
                R.id.tv_radiation_animation,
                R.id.tv_selector_time_dialog,
                R.id.tv_custom_viewpager
        )
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.tv_gif -> {
                startActivity(GifViewActivity::class.java)
            }
            R.id.tv_radiation_animation -> {
                startActivity(RadiationAnimationActivity::class.java)
            }
            R.id.tv_selector_time_dialog -> {
                ToastUtil.show("自定义时间选择器的dialog")
            }
            R.id.tv_custom_viewpager -> {
                startActivity(ViewPagerActivity::class.java)
            }
        }
    }

}