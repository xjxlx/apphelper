package android.helper.ui.activity.jetpack.navigation.navigation2

import android.annotation.SuppressLint
import android.helper.R
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_navigation2_1.*

class Navigation2_Fragment1 : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_navigation2_1
    }

    override fun initView(view: View?) {
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        arguments?.let {
            val bundle = Navigation2_Fragment3Args.fromBundle(it)
            tv_navigation2_args_1.text = "获取到的Fragment3的参数为：$bundle"
        }


        btn_navigation2_jump1.setOnClickListener {
            val bundle = Navigation2_Fragment1Args.Builder().setAge(12).setName("张三").build().toBundle()

            findNavController().navigate(R.id.action_Fragment1_to_Fragment2, bundle)
        }
    }
}