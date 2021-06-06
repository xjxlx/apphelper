package android.helper.ui.activity.jetpack.model

import android.helper.R
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_model1.*
import kotlinx.android.synthetic.main.fragment_model2.*

class Model2Fragment : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_model2
    }

    override fun initView(view: View?) {
    }

    override fun initData() {

        btn_f2_change.setOnClickListener {
            val get = ViewModelProviders.of(mContext).get(TestViewModel::class.java)
            tv_content_fr_2.text = get.name
        }
    }

    companion object {
        private val fragment by lazy {
            return@lazy Model2Fragment()
        }

        fun getInstance(): Model2Fragment {
            return fragment;
        }
    }

}