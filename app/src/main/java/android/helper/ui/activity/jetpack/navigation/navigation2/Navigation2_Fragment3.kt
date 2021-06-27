package android.helper.ui.activity.jetpack.navigation.navigation2

import android.helper.R
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_navigation2_3.*

class Navigation2_Fragment3 : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_navigation2_3
    }

    override fun initView(view: View?) {
    }

    override fun initData() {
        arguments?.let {
            val bundle = Navigation2_Fragment2Args.fromBundle(it)
            tv_navigation2_args_3.text = "获取到的Fragment3的参数为：$bundle"
        }

        btn_navigation2_jump3.setOnClickListener {
            val bundle = Navigation2_Fragment3Args
                    .Builder()
                    .setAge3(34)
                    .setName3("王五")
                    .build()
                    .toBundle()

            findNavController().navigate(R.id.action_Fragment3_to_Fragment1, bundle)
        }
    }
}