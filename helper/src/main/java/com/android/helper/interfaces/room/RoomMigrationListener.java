package com.android.helper.interfaces.room;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

/**
 * Room数据库升级、迁移的回调
 */
public interface RoomMigrationListener<T extends RoomDatabase> {

    RoomDatabase.Builder<T> addMigration(@NonNull Migration... migrations);

}
