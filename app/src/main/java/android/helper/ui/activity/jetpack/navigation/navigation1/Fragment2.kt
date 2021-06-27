package android.helper.ui.activity.jetpack.navigation.navigation1

import android.annotation.SuppressLint
import android.helper.R
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.android.helper.base.BaseFragment
import com.android.helper.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_2.*

class Fragment2 : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_2
    }

    override fun initView(view: View?) {
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        LogUtil.e("当前是Fragment ---> 2,当前的地址是：${this.hashCode()}")

        arguments?.let {
            val bundle = Fragment1Args.fromBundle(it)

            tv_content2.text = "获取到的名字是：${bundle.name} 获取到的年龄为：${bundle.age}"
        }

        btn_2_3.setOnClickListener {
            findNavController().navigate(R.id.action_fragment2_to_fragment3)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.e("oncreate ----> ${this.toString()}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtil.e("onDestroyView ----> ${this.toString()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e("onDestroy ----> ${this.toString()}")
    }
}