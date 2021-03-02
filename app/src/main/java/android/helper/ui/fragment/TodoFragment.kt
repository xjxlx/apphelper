package android.helper.ui.fragment

import android.helper.R
import android.helper.base.BaseFragment
import android.helper.test.TestMapActivity
import android.helper.ui.activity.DemoMapTitleActivity
import android.helper.ui.activity.animation.AnimationMapActivity
import android.helper.ui.activity.java.JavaMapActivity
import android.helper.ui.activity.widget.ViewMapTitleActivity
import android.view.View

/**
 * 待办的fragment
 */
class TodoFragment : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_todo
    }

    override fun initListener() {
        super.initListener()

        setViewClickListener(
                R.id.tv_custom_widget,
                R.id.tv_animation_map,
                R.id.tv_java_map,
                R.id.tv_test_map,
                R.id.tv_other
        )
    }

    override fun initView(container: View?) {

    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.tv_custom_widget -> {
                startActivity(ViewMapTitleActivity::class.java)
            }

            R.id.tv_animation_map -> {
                startActivity(AnimationMapActivity::class.java)
            }

            R.id.tv_java_map -> {
                startActivity(JavaMapActivity::class.java)
            }

            R.id.tv_test_map -> {
                startActivity(TestMapActivity::class.java)
            }

            R.id.tv_other -> {
                startActivity(DemoMapTitleActivity::class.java)
            }
        }
    }

}
