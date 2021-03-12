package android.helper.ui.activity.widget

import android.helper.R
import android.helper.base.BaseTitleActivity
import android.helper.utils.ToastUtil
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_scroll_page_view.*

/**
 * 滑动的pageView
 */
class ScrollPageViewActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_scroll_page_view
    }

    override fun initData() {
        super.initData()

        setTitleContent("滑动的pageView")

        val resources = intArrayOf(
                R.drawable.c62_control_air_control_normal,
                R.drawable.c62_control_automatic_parking_normal,
                R.drawable.c62_control_back_window_normal,
                R.drawable.c62_control_car_lock_normal,
                R.drawable.c62_control_car_window_normal,
                R.drawable.c62_control_charge_un_selector,
                R.drawable.c62_control_engine_normal,
                R.drawable.c62_control_find_car_normal,
                R.drawable.c62_control_one_cold_normal,
                R.drawable.c62_control_one_hot_normal,
                R.drawable.c62_control_seat_cold_normal,
                R.drawable.c62_control_seat_hot_normal,
                R.drawable.c62_control_sky_light_normal,
                R.drawable.c62_control_truck_normal
        )

        pv_group.setLayout(R.layout.item_page_view)
        pv_group.setDataList(resources)
        pv_group.setOnItemClickListener { v ->
            val position = v?.tag
            ToastUtil.show("position:$position")

            if (position == 3) {
                val textview = v.findViewById<TextView>(R.id.tv_content)
                textview.setText("sss")
            }
        }
    }

}