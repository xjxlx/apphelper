package android.helper.ui.activity.jetpack.navigation

import android.helper.R
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_1.*

class Fragment1 : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_1
    }

    override fun initView(view: View?) {

    }

    override fun initData() {
        btn_1_2.setOnClickListener {

            val bundle = Fragment1Args
                    .Builder()
                    .setName("张三")
                    .setAge(11)
                    .build()
                    .toBundle()

            findNavController().navigate(R.id.action_fragment1_to_fragment2, bundle)
        }
    }

}