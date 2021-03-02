package android.helper.ui.activity.widget

import android.helper.R
import android.helper.base.BaseTitleActivity
import android.helper.utils.CustomViewUtil
import kotlinx.android.synthetic.main.activity_multiple_list_view.*

/**
 * 多列表的listView
 */
class MultipleListViewActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_multiple_list_view
    }

    override fun initData() {
        super.initData()
        setTitleContent("多列表的ListView")

        val arrayListOf = arrayListOf<String>()

//        arrayListOf.add("house")
//        arrayListOf.add("kitchen")
//        arrayListOf.add("bathroom")
//        arrayListOf.add("bedroom")
//        arrayListOf.add("backyard")

//        us, right, upon, sit

        arrayListOf.add("us")
        arrayListOf.add("right")
        arrayListOf.add("upon")
        arrayListOf.add("sit")

        arrayListOf.add("us")
        arrayListOf.add("right")
        arrayListOf.add("upon")
        arrayListOf.add("sit")

        mlv_list.setList(arrayListOf)
    }
}