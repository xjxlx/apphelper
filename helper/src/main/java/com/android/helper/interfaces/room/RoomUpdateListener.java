package com.android.helper.interfaces.room;

public interface RoomUpdateListener {
    int update();

    /**
     * @param updateRow room数据库改动数据的行数，如果更新失败，则返回0
     */
    void onResult(boolean success, int updateRow);
}
