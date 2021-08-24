package com.android.helper.httpclient;

import com.android.helper.base.BaseEntity;

/**
 * 返回类型的基类
 *
 * @param <T>
 */
public class BaseHttpResponse<T> extends BaseEntity {
    
    private int code;
    private String msg;
    private T data;
    
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
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "BaseHttpResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
