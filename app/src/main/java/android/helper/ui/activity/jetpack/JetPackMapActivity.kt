package android.helper.ui.activity.jetpack

import android.helper.R
import android.helper.ui.activity.jetpack.livedata.LiveDataActivity
import android.helper.ui.activity.jetpack.lifecycle.LifecycleActivity
import android.helper.ui.activity.jetpack.model.ViewModelActivity
import android.view.View
import com.android.helper.base.BaseTitleActivity

/**
 * JetPack的集合
 */
class JetPackMapActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_jet_pack_map
    }

    override fun initView() {
        super.initView()
        setTitleContent("JetPack的集合")
    }

    override fun initListener() {
        super.initListener()
        setonClickListener(R.id.tv_lifecycle, R.id.tv_view_model, R.id.tv_live_date)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.tv_lifecycle -> {
                startActivity(LifecycleActivity::class.java)
            }

            R.id.tv_view_model -> {
                intent.putExtra("key", "123")
                startActivity(ViewModelActivity::class.java)
            }

            R.id.tv_live_date -> {
                startActivity(LiveDataActivity::class.java)
            }
        }
    }

}