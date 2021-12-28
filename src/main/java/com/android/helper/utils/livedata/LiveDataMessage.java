package com.android.helper.utils.livedata;

import android.os.Bundle;

/**
 * @author : 流星
 * @CreateDate: 2021/12/28-3:05 下午
 * @Description: liveData 数据发送的Bean
 */
public class LiveDataMessage {

    private int code;
    private String msg;
    private Bundle data;

    public LiveDataMessage() {
    }

    public LiveDataMessage(int code) {
        this.code = code;
    }

    public LiveDataMessage(String msg) {
        this.msg = msg;
    }

    public LiveDataMessage(Bundle data) {
        this.data = data;
    }

    public LiveDataMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public LiveDataMessage(int code, Bundle data) {
        this.code = code;
        this.data = data;
    }

    public LiveDataMessage(String msg, Bundle data) {
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LiveDataMessage{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
