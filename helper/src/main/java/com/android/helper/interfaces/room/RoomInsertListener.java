package com.android.helper.interfaces.room;

public interface RoomInsertListener {

    long insert();

    /**
     * @param id room插入正确返回的id,如果id为-1，则表示失败
     */
    void onResult(boolean success, long id);
}
