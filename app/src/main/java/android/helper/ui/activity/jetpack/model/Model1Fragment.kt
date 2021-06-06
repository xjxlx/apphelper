package android.helper.ui.activity.jetpack.model

import android.helper.R
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_model1.*

class Model1Fragment : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_model1
    }

    override fun initView(view: View?) {

    }

    override fun initData() {
        val get = ViewModelProviders.of(mContext).get(TestViewModel::class.java)
        tv_content_fr_1.text = get.name

        btn_f1_change.setOnClickListener {
            get.name = "李四"
            tv_content_fr_1.text = get.name
        }
    }

    companion object {

        private val fragment: Model1Fragment by lazy {
            return@lazy Model1Fragment()
        }

        fun newInstance(): Model1Fragment {
            return fragment
        }
    }
}