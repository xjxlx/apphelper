package android.helper.ui.activity.jetpack.room

import android.helper.R
import android.view.View
import androidx.lifecycle.Observer
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

    private val observer = object : Observer<RoomEntityLiveData> {
        override fun onChanged(t: RoomEntityLiveData?) {
            ToastUtil.show("返回的数据为：" + t)
        }
    }

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
                btn_query_single, btn_query_all,

                btn_install1, btn_delete2, btn_update2, btn_query2,

                btn_live_data_install, btn_live_data_delete, btn_live_data_update, btn_live_data_query
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
                val querySingle = roomManager.dao1.roomQuery(1624189513406)
                ToastUtil.show("查询单个成功：$querySingle")
            }

            R.id.btn_query_all -> {
                val list = roomManager.dao1.roomQuery()
                ToastUtil.show("查询列表成功：$list")
            }

            // 增加
            R.id.btn_install1 -> {
                val room = RoomEntity2()
                room.id = System.currentTimeMillis()
                val roomInsert = roomManager.dao2.roomInsert(room)
                room.name = "王语嫣"
                ToastUtil.show("添加成功：$roomInsert")
            }

            // 删除
            R.id.btn_delete2 -> {
                val room = RoomEntity2()
                room.id = 1624194999032
                roomManager.dao2.roomDelete(room)

                ToastUtil.show("删除成功：$")
            }

            // 更新
            R.id.btn_update2 -> {
                val room = RoomEntity2()
                room.id = 1624195185217
                room.name = "李若彤"
                room.age = 18
                roomManager.dao2.roomUpdate(room)
                ToastUtil.show("修改成功：$")
            }

            // 查询
            R.id.btn_query2 -> {
                val roomQuery = roomManager.dao2.roomQuery(1624195185217)
                ToastUtil.show("查询成功：$roomQuery")
            }

            /****************************** LiveData ***************************/
            // 增加
            R.id.btn_live_data_install -> {
                val room = RoomEntityLiveData()
                room.id = System.currentTimeMillis()
                room.name = "王语嫣"
                val roomInsert = roomManager.liveData.roomInsert(room)
                ToastUtil.show("添加成功：$roomInsert")
            }

            // 删除
            R.id.btn_live_data_delete -> {
                val room = RoomEntityLiveData()
                room.id = 1624197808653
                roomManager.liveData.roomDelete(room)

                ToastUtil.show("删除成功：$")
            }

            // 更新
            R.id.btn_live_data_update -> {
                val room = RoomEntityLiveData()
                room.id = 1624197857729
                room.name = "李若彤"
                room.age = 18
                roomManager.liveData.roomUpdate(room)
                ToastUtil.show("修改成功：$")
            }

            // 查询
            R.id.btn_live_data_query -> {
                val roomQuery = roomManager.liveData.roomQuery(1624199401956)
                ToastUtil.show("查询成功：${roomQuery}")
                window.decorView.postDelayed(Runnable {

                    // 发送给
                    roomQuery.observe(this, observer)

                }, 2000)
            }
        }
    }
}