package com.android.helper.interfaces.room;

public interface RoomExecuteListener<T> {

    T execute();

    /**
     * @param t 返回的参数，如果是删改，则返回变动的行数，如果是增加的操作，则返回当前的地址，如果是查询的执行，则返回查询的数据
     */
    void onResult(boolean success, T t, String errorMsg);

}
