package com.android.helper.interfaces.room;

public interface RoomQueryListener<T> {

    T query();

    /**
     * @param t room数据库查询的结果，如果更新失败，则返回null
     */
    void onResult(boolean success, T t);
}
