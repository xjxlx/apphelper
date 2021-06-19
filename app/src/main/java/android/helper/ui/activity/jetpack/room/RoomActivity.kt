package android.helper.ui.activity.jetpack.room

import android.helper.R
import android.view.View
import androidx.room.Room
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_room.*

/**
 * room数据库的使用
 * 使用逻辑：
 *      一：Entity注解类： entity： [ˈentəti] 嗯特忒
 *         1：创建一个文件，使用entity去注解，标记为一个数据库为实体类
 *
 *      二：创建数据库的操作方法，使用@Dao去注解
 *
 * 使用的好处：
 *
 */

class RoomActivity : BaseTitleActivity() {

    private val mRoomDb: RoomDao by lazy {
        return@lazy Room
                .databaseBuilder(mContext, RoomDataManager::class.java, RoomDataManager.DATA_BASE_FILE_NAME)
                .allowMainThreadQueries()//允许在主线程中查询
                .build()
                .dao
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
                btn_query_single, btn_query_all
        )
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btn_add_single -> {
                val room = RoomEntity()
                room.name = "张三"
                room.time = System.currentTimeMillis()

                mRoomDb.roomInsert(room)

                ToastUtil.show("添加单个完成!")
            }

            R.id.btn_add_list -> {
                val room = RoomEntity()
                room.name = "李四"
                room.time = System.currentTimeMillis()

                val list = arrayListOf<RoomEntity>()
                list.add(room)
                list.add(room)
                list.add(room)
                list.add(room)
                val roomInsert = mRoomDb.roomInsert(list)

                ToastUtil.show("添加列表完成:${roomInsert.distinct()}")
            }

            R.id.btn_delete_single -> {
                val room = RoomEntity()
                room.id = 3;
                mRoomDb.roomDelete(room)
                ToastUtil.show("删除单个对象成功！")
            }

            R.id.btn_delete_list -> {
                val list = arrayListOf<RoomEntity>()
                val room1 = RoomEntity()
                room1.id = 3;
                list.add(room1)

                val room2 = RoomEntity()
                room2.id = 4;
                list.add(room2)

                val room3 = RoomEntity()
                room3.id = 5;
                list.add(room3)

                mRoomDb.roomDelete(list)
                ToastUtil.show("删除列表成功！")
            }

            R.id.btn_update_id -> {

                val room = RoomEntity()
                room.id = 6
                room.name = "小飞飞"

                mRoomDb.roomUpdate(room)
            }

            R.id.btn_update_entity -> {
                val list = arrayListOf<RoomEntity>()
                val room = RoomEntity()
                room.id = 7
                room.name = "王语嫣"

                list.add(room)
                mRoomDb.roomUpdate(list)
            }

            R.id.btn_query_single -> {
                val querySingle = mRoomDb.querySingle(7)
                ToastUtil.show("查询单个成功：$querySingle")
            }

            R.id.btn_query_all -> {
                val list = mRoomDb.queryList()
                ToastUtil.show("查询列表成功：$list")
            }
        }
    }
}