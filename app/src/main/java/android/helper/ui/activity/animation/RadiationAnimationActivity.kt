package android.helper.ui.activity.animation

import android.helper.R
import android.widget.Button
import com.android.helper.base.BaseTitleActivity
import kotlinx.android.synthetic.main.activity_radiation_animation.*

/**
 * 自定义放射动画
 */
class RadiationAnimationActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_radiation_animation
    }

    override fun initData() {
        super.initData()
        setTitleContent("放射动画")

        findViewById<Button>(R.id.btn).setOnClickListener {
            cst.startAnim()
        }
    }
}