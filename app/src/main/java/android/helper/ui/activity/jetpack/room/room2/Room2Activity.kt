package android.helper.ui.activity.jetpack.room.room2

import android.helper.R
import android.view.View
import com.android.helper.base.BaseTitleActivity
import kotlinx.android.synthetic.main.activity_room2.*

class Room2Activity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_room2
    }

    override fun initListener() {
        super.initListener()

        setonClickListener(btn_table_1_insert)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btn_table_1_insert -> {

            }

            R.id.btn_table_1_delete -> {

            }

            R.id.btn_table_1_update -> {

            }

            R.id.btn_table_1_query -> {

            }

            R.id.btn_table_2_create -> {

            }

            R.id.btn_table_2_add -> {

            }

            R.id.btn_table_2_insert -> {

            }
        }
    }
}