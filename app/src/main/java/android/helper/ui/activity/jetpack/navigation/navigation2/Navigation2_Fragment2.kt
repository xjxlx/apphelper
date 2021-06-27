package android.helper.ui.activity.jetpack.navigation.navigation2

import android.helper.R
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_navigation2_2.*

class Navigation2_Fragment2 : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_navigation2_2
    }

    override fun initView(view: View?) {
    }

    override fun initData() {
        arguments?.let {
            val bundle = Navigation2_Fragment1Args.fromBundle(it)
            tv_navigation2_args_2.text = "获取到的Fragment3的参数为：$bundle"
        }


        btn_navigation2_jump2.setOnClickListener {
            val bundle = Navigation2_Fragment2Args
                    .Builder()
                    .setAge2(23)
                    .setName2("李四")
                    .build()
                    .toBundle()

            findNavController().navigate(R.id.action_Fragment2_to_Fragment3, bundle)
        }
    }
}