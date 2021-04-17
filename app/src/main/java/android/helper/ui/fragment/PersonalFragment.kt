package android.helper.ui.fragment

import android.helper.R
import android.view.View
import com.android.helper.base.BaseFragment

/**
 * 个人中心的fragment
 */
class PersonalFragment<T> : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_personal
    }

    override fun initView(view: View?) {
    }

    override fun initData() {
    }

}
