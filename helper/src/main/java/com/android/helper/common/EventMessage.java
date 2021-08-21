package com.android.helper.common;

import android.os.Bundle;

import com.android.helper.base.BaseEntity;

/**
 * EventBus的消息类
 */
public class EventMessage extends BaseEntity {

    private int code;
    private String msg;
    private Bundle bundle;
    private Object object;

    public EventMessage(int code) {
        this.code = code;
    }

    public EventMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public EventMessage(int code, Object object) {
        this.code = code;
        this.object = object;
    }

    public EventMessage(int code, Bundle bundle) {
        this.code = code;
        this.bundle = bundle;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "EventMessage{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", bundle=" + bundle +
                ", object=" + object +
                '}';
    }
}
