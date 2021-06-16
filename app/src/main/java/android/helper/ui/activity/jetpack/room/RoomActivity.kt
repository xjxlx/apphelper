package android.helper.ui.activity.jetpack.room

import android.helper.R
import androidx.room.Room
import com.android.helper.base.BaseTitleActivity
import kotlinx.android.synthetic.main.activity_room.*

/**
 * room数据库的使用
 * 使用逻辑：
 *      一：Entity注解类： entity： [ˈentəti] 嗯特忒
 *         1：创建一个文件，使用entity去注解，标记为一个数据库为实体类
 *         2：创建数据库的操作方法，使用@Dao去注解
 */

class RoomActivity : BaseTitleActivity() {

    private val dataUtil: RoomData by lazy {
        return@lazy Room.databaseBuilder(
                mContext,
                RoomData::class.java,
                "table_test"
        )
                .allowMainThreadQueries()
                .build()
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

        btn_room_add.setOnClickListener {

            val room = RoomEntity()
            room.id = "123456"
            room.name = "张三"
            room.realName = "李四"
            room.six = 1

            val roomInsert = dataUtil.dao.roomInsert(room)

        }

        btn_room_delete.setOnClickListener {

        }

        btn_room_update.setOnClickListener {

        }

        btn_room_query.setOnClickListener {

        }

    }
}