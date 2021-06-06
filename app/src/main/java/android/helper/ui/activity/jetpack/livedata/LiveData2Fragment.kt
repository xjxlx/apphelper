package android.helper.ui.activity.jetpack.livedata

import android.helper.R
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.helper.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_live_data1.*
import kotlinx.android.synthetic.main.fragment_live_data2.*

class LiveData2Fragment : BaseFragment() {

    override fun getBaseLayout(): Int {
        return R.layout.fragment_live_data2
    }

    override fun initView(view: View?) {
    }

    override fun initData() {
        // liveData
        val viewModel = ViewModelProviders.of(mContext).get(LiveDataModel::class.java)
        viewModel.liveData.observe(this, object : Observer<TestLiveData> {
            override fun onChanged(t: TestLiveData?) {
                tv_live_date_text_2.text = t?.name
            }
        })

        // mutableLiveModel
        val mutableLiveModel = ViewModelProviders.of(mContext).get(MutableLiveModel::class.java)
        mutableLiveModel.data.observe(this, object : Observer<TestMutableLiveData> {
            override fun onChanged(t: TestMutableLiveData?) {
                tv_live_date_text_2.text = t?.name
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = LiveData2Fragment()
    }
}