package android.helper.ui.activity.jetpack.room

import android.helper.R
import android.view.View
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_room.*

/**
 * room数据库的使用
 * 使用逻辑：
 *      一：Entity注解类： entity： [ˈentəti] 嗯特忒
 *         1：创建一个文件，使用entity去注解，标记为一个数据库为实体类,所有的字段都在表中存储着
 *
 *      二：创建数据库的操作方法，使用@Dao去注解
 *
 * 使用的好处：
 *
 */

class RoomActivity : BaseTitleActivity() {

    private val roomManager = RoomDataHelper.getInstance()

    override fun getTitleLayout(): Int {
        return R.layout.activity_room
    }

    override fun initView() {
        super.initView()

        setTitleContent("Room数据库的使用")
    }

    override fun initListener() {
        super.initListener()

        setonClickListener(
                btn_add_single, btn_add_list,
                btn_delete_single, btn_delete_list,
                btn_update_id, btn_update_entity,
                btn_query_single, btn_query_all
        )

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btn_add_single -> {
                val room = RoomEntity1()
                room.id = System.currentTimeMillis()
                room.name = "张三"
                val roomInsert = roomManager.dao1.roomInsert(room)
                ToastUtil.show("添加单个完成：$roomInsert")
            }

            R.id.btn_add_list -> {

            }

            R.id.btn_delete_single -> {
                val room = RoomEntity1()
                room.id = 1624189538223
                roomManager.dao1.roomDelete(room)
                ToastUtil.show("删除单个对象成功：!")
            }

            R.id.btn_delete_list -> {

            }

            R.id.btn_update_id -> {
                val room = RoomEntity1()
                room.id = 1624189513406
                room.name = "小飞飞"
                roomManager.dao1.roomUpdate(room)
                ToastUtil.show("更新单个对象成功：!")
            }

            R.id.btn_update_entity -> {

            }

            R.id.btn_query_single -> {
                val querySingle =  roomManager.dao1.roomQuery(1624189513406)
                ToastUtil.show("查询单个成功：$querySingle")
            }

            R.id.btn_query_all -> {
                val list = roomManager.dao1.roomQuery()
                ToastUtil.show("查询列表成功：$list")
            }
        }
    }
}