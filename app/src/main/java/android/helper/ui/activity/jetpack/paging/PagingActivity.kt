package android.helper.ui.activity.jetpack.paging

import android.helper.databinding.ActivityPagingBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.helper.base.viewbinding.BaseBindingTitleActivity

/**
 * 分页加载的paging
 */
class PagingActivity : BaseBindingTitleActivity<ActivityPagingBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): ActivityPagingBinding {
        return ActivityPagingBinding.inflate(layoutInflater, mTitleBinding.root, true)
    }

    override fun initData() {
        setTitleContent("分页加载的Paging")
    }
}