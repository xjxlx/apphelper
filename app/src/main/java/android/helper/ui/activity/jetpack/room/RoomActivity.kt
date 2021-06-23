package android.helper.ui.activity.jetpack.room

import android.helper.R
import android.view.View
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.helper.base.BaseTitleActivity
import com.android.helper.interfaces.room.RoomDeleteListener
import com.android.helper.interfaces.room.RoomInsertListener
import com.android.helper.interfaces.room.RoomQueryListener
import com.android.helper.interfaces.room.RoomUpdateListener
import com.android.helper.utils.LogUtil
import com.android.helper.utils.ToastUtil
import com.android.helper.utils.room.RoomUtil
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
    private lateinit var roomManager: RoomDataBaseHelper

    private val observer = Observer<RoomEntityLiveData> { t -> ToastUtil.show("返回的数据为：" + t) }

    private lateinit var versionRoom: RoomDataBaseHelper

    private val mRoomUtil by lazy {
        return@lazy RoomUtil.getInstance()
    }

    override fun getTitleLayout(): Int {
        return R.layout.activity_room
    }

    override fun initView() {
        super.initView()

        setTitleContent("Room数据库的使用")

        // roomManager = RoomDataBaseHelper.getInstance()
    }

    override fun initListener() {
        super.initListener()

        setonClickListener(
                btn_add_single, btn_add_list,
                btn_delete_single, btn_delete_list,
                btn_update_id, btn_update_entity,
                btn_query_single, btn_query_all,
                btn_install1, btn_delete2, btn_update2, btn_query2,
                btn_live_data_install, btn_live_data_delete, btn_live_data_update, btn_live_data_query,
                btn_rxjava, btn_database_update, btn_database_update_insert
        )
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btn_add_single -> {
                val room = RoomEntity1()
                room.id = System.currentTimeMillis()
                room.name = "张三"

                mRoomUtil.insert(object : RoomInsertListener {
                    override fun insert(): Long {
                        return roomManager.dao1.roomInsert(room)
                    }

                    override fun onResult(success: Boolean, id: Long, errorMsg: String?) {
                        ToastUtil.show("添加单个完成：$id")
                    }

                })
            }
            R.id.btn_add_list -> {

                val room = RoomEntityTest()
                room.uid = System.currentTimeMillis().toString()
                room.name = "王五"
                room.six = 1

                val insert = roomManager.daoTest.insert(room)
                LogUtil.e("insert:" + insert)
            }

            R.id.btn_delete_single -> {
                val room = RoomEntity1()
                room.id = 1624340340101
                mRoomUtil.delete(object : RoomDeleteListener {
                    override fun delete(): Int {
                        return roomManager.dao1.roomDelete(room)
                    }

                    override fun onResult(success: Boolean, deleteRow: Int, errorMsg: String?) {
                        ToastUtil.show("删除单个对象成功：$deleteRow")
                    }

                })
            }

            R.id.btn_delete_list -> {
                val room = RoomEntityTest()
                room.uid = "1624350446562"

                val delete = roomManager.daoTest.delete(room)
                LogUtil.e("delete:" + delete)
            }

            R.id.btn_update_id -> {
                val room = RoomEntity1()
                room.id = 1624340347931
                room.name = "小飞飞"

                mRoomUtil.update(object : RoomUpdateListener {
                    override fun update(): Int {
                        return roomManager.dao1.roomUpdate(room)
                    }

                    override fun onResult(success: Boolean, updateRow: Int, errorMsg: String?) {
                        ToastUtil.show("更新单个对象成功：$updateRow")
                    }

                })
            }

            R.id.btn_update_entity -> {
            }
            R.id.btn_query_single -> {
                mRoomUtil.query(object : RoomQueryListener<RoomEntity1> {
                    override fun query(): RoomEntity1 {
                        return roomManager.dao1.roomQuery(1624340347931)
                    }

                    override fun onResult(success: Boolean, t: RoomEntity1?, errorMsg: String?) {
                        sequenceOf(ToastUtil.show("查询单个结果：$success querySingle:$t"))
                    }
                })

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
                val room1 = RoomEntityLiveData()
                room1.id = 1624364327773

                val room2 = RoomEntityLiveData()
                room2.id = 1624364328392

                val list = ArrayList<RoomEntityLiveData>()
                list.add(room1)
                list.add(room2)

                val roomDelete = roomManager.liveData.roomDelete(list)
                LogUtil.e("delete:$roomDelete")


                ToastUtil.show("删除成功：$$roomDelete")
            }
            // 更新
            R.id.btn_live_data_update -> {
                val room = RoomEntityLiveData()
                room.id = 1624364331827
                room.name = "李若彤"
                room.age = 18
                val roomUpdate = roomManager.liveData.roomUpdate(room)

                LogUtil.e("roomUpdate:$roomUpdate")

                ToastUtil.show("修改成功：$roomUpdate")
            }
            // 查询
            R.id.btn_live_data_query -> {
                val roomQuery = roomManager.liveData.roomQuery(1624199401956)
                ToastUtil.show("查询成功：${roomQuery}")
            }

            R.id.btn_rxjava -> {
                // rxjava 的查询
                mRoomUtil.insert(object : RoomInsertListener {
                    override fun insert(): Long {

                        val room = RoomEntityLiveData()
                        room.id = System.currentTimeMillis()
                        room.name = "王语嫣"

                        return roomManager.liveData.roomInsert(room)
                    }

                    override fun onResult(success: Boolean, id: Long, errorMsg: String?) {
                        ToastUtil.show("返回的结果为：$success   id：$id")
                    }

                })
            }

            R.id.btn_database_update -> {

                LogUtil.e("重新构建了对象!")

                val migration = object : Migration(3, 4) {
                    override fun migrate(database: SupportSQLiteDatabase) {

                        val version = database.version

                        LogUtil.e("version:$version")

                        val sql = mRoomUtil.addColumn("room_test", "time", RoomUtil.UNIT.TEXT)

                        database.execSQL(sql)

                    }
                }

                // 数据库更新
                versionRoom = Room
                        .databaseBuilder(mContext, RoomDataBaseHelper::class.java, RoomDataBaseHelper.ROOM_DB_NAME)
//                        .addMigrations(migration)
                        .build()
            }

            R.id.btn_database_update_insert -> {

                val room = RoomEntityTest()
                room.uid = System.currentTimeMillis().toString()
                room.name = "哈哈"

                mRoomUtil.insert(object : RoomInsertListener {
                    override fun insert(): Long {
                        return versionRoom.daoTest.insert(room)
                    }

                    override fun onResult(success: Boolean, id: Long, errorMsg: String?) {

                        ToastUtil.show("插入成功：$success  插入的对象：$id  错误的原因：$errorMsg")
                    }
                })

            }
        }
    }

}