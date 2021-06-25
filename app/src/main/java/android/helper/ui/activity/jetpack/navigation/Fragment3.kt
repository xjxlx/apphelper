package android.helper.ui.activity.jetpack.navigation

import android.helper.R
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_3.*

class Fragment3 : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_3
    }

    override fun initView(view: View?) {
    }

    override fun initData() {

        btn_3_1.setOnClickListener {
            findNavController().navigate(R.id.action_fragment3_to_fragment1)
        }
    }

}