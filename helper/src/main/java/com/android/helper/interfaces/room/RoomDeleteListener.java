package com.android.helper.interfaces.room;

public interface RoomDeleteListener {

    int delete();

    /**
     * @param row room删除成功后返回的行数，表示删除了几条数据，如果删除失败，则返回0
     */
    void onResult(boolean success, int row);

}
