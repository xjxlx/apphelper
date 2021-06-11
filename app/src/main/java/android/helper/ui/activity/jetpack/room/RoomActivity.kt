package android.helper.ui.activity.jetpack.room

import android.helper.R
import com.android.helper.base.BaseTitleActivity

/**
 * room数据库的使用
 * 使用逻辑：
 *      一：Entity注解类： entity： [ˈentəti] 嗯特忒
 *         1：创建一个文件，使用entity去注解，标记为一个数据库为实体类
 *         2：
 */

class RoomActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_room
    }

    override fun initView() {
        super.initView()
        setTitleContent("Room数据库的使用")
    }
}