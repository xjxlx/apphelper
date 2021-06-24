package android.helper.ui.activity.jetpack.room.room2

import android.helper.R
import android.view.View
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.helper.base.BaseTitleActivity
import com.android.helper.interfaces.room.RoomDeleteListener
import com.android.helper.interfaces.room.RoomExecuteListener
import com.android.helper.interfaces.room.RoomInsertListener
import com.android.helper.utils.LogUtil
import com.android.helper.utils.ToastUtil
import com.android.helper.utils.room.RoomUtil
import com.android.helper.utils.room.SqlEntity
import kotlinx.android.synthetic.main.activity_room2.*

class Room2Activity : BaseTitleActivity() {

    private val roomManager by lazy {
        return@lazy RoomManager.getInstance()
    }

    override fun getTitleLayout(): Int {
        return R.layout.activity_room2
    }

    override fun initListener() {
        super.initListener()

        setonClickListener(
                btn_table_1_insert,
                btn_table_1_delete,
                btn_table_1_update,
                btn_table_1_query,
                btn_table_2_create,
                btn_table_2_add,
                btn_table_2_insert
        )
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btn_table_1_insert -> {
                val room1 = RoomTable1()
                room1.id = System.currentTimeMillis()
                room1.name = "王语嫣"
                room1.isWanMan = true

                RoomUtil.getInstance().insert(object : RoomInsertListener {
                    override fun insert(): Long {
                        return roomManager.dao1.insert(room1)
                    }

                    override fun onResult(success: Boolean, id: Long, errorMsg: String?) {
                        val result = "success: $success id:$id  error:$errorMsg"
                        LogUtil.e("result:$result")
                        ToastUtil.show(result)
                    }
                })

            }

            R.id.btn_table_1_delete -> {
                val room1 = RoomTable1()
                room1.id = 1624516322548

                RoomUtil.getInstance().delete(object : RoomDeleteListener {
                    override fun delete(): Int {
                        return roomManager.dao1.delete(room1)
                    }

                    override fun onResult(success: Boolean, deleteRow: Int, errorMsg: String?) {
                        val result = "success: $success deleteRow:$deleteRow  error:$errorMsg"
                        LogUtil.e("result:$result")
                        ToastUtil.show(result)
                    }
                })
            }

            R.id.btn_table_1_update -> {
                val room1 = RoomTable1()
                room1.id = 1624519895314
                room1.name = "小龙女"
                room1.isWanMan = false

                RoomUtil.getInstance().execute(object : RoomExecuteListener<Int> {
                    override fun execute(): Int {
                        return roomManager.dao1.update(room1)
                    }

                    override fun onResult(success: Boolean, t: Int?, errorMsg: String?) {
                        val result = "success: $success deleteRow:$t  error:$errorMsg"
                        LogUtil.e("result:$result")
                        ToastUtil.show(result)
                    }
                })
            }

            R.id.btn_table_1_query -> {
                RoomUtil.getInstance().execute(object : RoomExecuteListener<RoomTable1> {
                    override fun execute(): RoomTable1 {
                        return roomManager.dao1.query(1624516572089)
                    }

                    override fun onResult(success: Boolean, t: RoomTable1?, errorMsg: String?) {
                        ToastUtil.show(t?.toString())
                    }
                })
            }

            R.id.btn_table_2_create -> {

                RoomUtil.getInstance().execute(object : RoomExecuteListener<Long> {
                    override fun execute(): Long {
                        val migration1_2 = object : Migration(1, 2) {
                            override fun migrate(database: SupportSQLiteDatabase) {

                                val map = hashMapOf<String, SqlEntity>()
                                map["id"] = SqlEntity(RoomUtil.UNIT.INTEGER)
                                map["name"] = SqlEntity(RoomUtil.UNIT.TEXT)
                                map["isMaser"] = SqlEntity(RoomUtil.UNIT.INTEGER)

                                val createTable = RoomUtil.getInstance().createTable("room_table_22", "id", RoomUtil.UNIT.INTEGER, false, map)

                                LogUtil.e("当前的版本是：" + database.version)
                                database.execSQL(createTable)
                            }
                        }

                        val room = RoomTable2()
                        room.id = System.currentTimeMillis()
                        room.name = "张三"
                        room.isMaser = true

                        return RoomManager.getInstance(migration1_2).dao2.insert(room)
                    }

                    override fun onResult(success: Boolean, t: Long?, errorMsg: String?) {

                    }
                })
            }

            R.id.btn_table_2_add -> {

            }

            R.id.btn_table_2_insert -> {

            }
        }
    }
}