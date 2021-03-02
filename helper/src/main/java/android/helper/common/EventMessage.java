package android.helper.common;

import android.os.Bundle;

import android.helper.base.BaseEntity;

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
