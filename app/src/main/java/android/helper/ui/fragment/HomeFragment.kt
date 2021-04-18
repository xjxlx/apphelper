package android.helper.ui.fragment

import android.helper.BuildConfig
import android.helper.R
import android.helper.adapters.TestAdapter
import android.helper.bean.HomeBean
import android.helper.http.service.CommonApiService
import android.view.View
import com.android.helper.base.BaseRefreshFragment
import com.android.helper.httpclient.BaseException
import com.android.helper.httpclient.RetrofitHelper
import com.android.helper.utils.LogUtil
import com.android.helper.utils.RecycleUtil
import com.android.helper.utils.ToastUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.expandable_gpc_child.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Response

/**
 * 首页
 */
class HomeFragment : BaseRefreshFragment<Response<HomeBean>, HomeBean.ReturnDataList.Data>() {

    private val mAdapter: TestAdapter by lazy {
        return@lazy TestAdapter(mContext)
    }

    override fun getRefreshLayout(): Int {
        return R.layout.fragment_home
    }

    override fun getApiService(): Flowable<Response<HomeBean>>? {
        val map = HashMap<String, Any>()
        map["angent_id"] = "ff808081647099c101648d5526980084"
        return RetrofitHelper.create("http://api-app.yqft.hi-cloud.net/", CommonApiService::class.java).getHomeData(pageControl(map))
    }

    override fun initView(container: View?) {
    }

    override fun initData() {
        super.initData()
        RecycleUtil
                .getInstance(mContext, rv_home_list)
                .setVertical()
                .setAdapter(mAdapter)
    }

    override fun filterForPage(t: Response<HomeBean>): Boolean {
        val body = t.body()
        if (body != null) {
            val returnDataList = body.returnDataList
            if (returnDataList != null) {
                val size = returnDataList.data.size
                if (size < pageSize()) {
                    return true
                }
            }
        }
        return super.filterForPage(t)
    }

    override fun onSuccess(t: Response<HomeBean>) {
        val body = t.body()
        val returnDataList = body?.returnDataList
        val data = returnDataList?.data

        data?.let {
            mList.addAll(it)
            mAdapter.setList(mList)
        }
    }

    override fun onFailure(throwable: BaseException?) {
        LogUtil.e("error:" + throwable?.message)
        ToastUtil.show(throwable?.message)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        LogUtil.e("load:" + BuildConfig.APP_DEBUG)
    }

}
