package android.helper.test

import android.helper.adapters.HighWordExpandableAdapter
import android.helper.bean.ExpandableBean
import android.helper.databinding.ActivityExpandableBinding
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.helper.base.BaseBindingActivity
import com.android.helper.utils.ExpandableUtil
import com.android.helper.utils.StreamUtil
import com.google.gson.Gson

/**
 * 测试拓展listView的加载顺序
 */
class ExpandableActivity : BaseBindingActivity<ActivityExpandableBinding>() {

//    override fun getTitleLayout(): Int {
//        return R.layout.activity_expandable
//    }

    override fun initData() {
        super.initData()

        // setTitleContent("测试ExpandableList的加载顺序")

        val assets = mContext.assets
        val inputStream = assets.open("expandable.json")

        val json = StreamUtil.InputStreamToString(inputStream)

        if (!TextUtils.isEmpty(json)) {

            val gson = Gson()
            val bean = gson.fromJson(json, ExpandableBean::class.java)
            // LogUtil.e("bean:$bean")

            val content = bean.data.content
            val adapter2 = HighWordExpandableAdapter(mContext, content)
//            val adapter = TestExpandableAdapter(mContext, content)
            mBinding.evlLists.setAdapter(adapter2)

            ExpandableUtil.openSelfCloseOther(mBinding.evlLists)
            ExpandableUtil.openCurrent(mBinding.evlLists, 0)

//            evl_lists.setOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
//                if (parent.isGroupExpanded(groupPosition)) {
//                    parent.collapseGroup(groupPosition)
//                } else {
//                    //第二个参数false表示展开时是否触发默认滚动动画
//                    parent.expandGroup(groupPosition, false)
//                }
//            })
        }
    }
//
//    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ActivityExpandableBinding {
//
//    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): ActivityExpandableBinding {
        return ActivityExpandableBinding.inflate(inflater)
    }

}