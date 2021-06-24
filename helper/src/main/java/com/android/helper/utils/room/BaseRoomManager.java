package com.android.helper.utils.room;

import androidx.room.RoomDatabase;

/**
 * Room数据库管理对象的基类
 * 使用：
 * 1：继承本类的对象，也必须是一个抽象到了类型
 * 2：必须使用@Database 去注解，并指定版本号和 对应的实体类
 * 3：必须返回一个抽象方法，返回对象必须是一个@Dao注解的实体类
 * 4：version的值：可以自己定义，最后是写在gradle中，例如：在android 的 defaultConfig 里面 写入 buildConfigField("int", "ROOM_VERSION", "1")
 * 5：数据库的文件名字和version一样，最好是写在gradle中，例如： buildConfigField("String", "ROOM_VERSION_FILE_NAME", "\"room_table.db\"")
 */
// @Database(version = RoomManager.ROOM_VERSION, entities = {RoomTable1.class, RoomTable2.class})
public abstract class BaseRoomManager extends RoomDatabase {

    /*
     * public static RoomManager getInstance() {
     *      if (INSTANCE == null) {
     *          synchronized (RoomManager.class) {
     *              if (INSTANCE == null) {
     *                  INSTANCE = Room.databaseBuilder(
     *                          App.getInstance().getApplicationContext(),
     *                          RoomManager.class,
     *                          "room_table_2.db"
     *                          ).build();
     *                  }
     *              }
     *          }
     *      return INSTANCE;
     * }
     *
     * @return 获取一个实例的对象
     */

}
