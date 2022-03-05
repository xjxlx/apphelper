package com.android.helper.httpclient;

import okhttp3.Response;

/**
 * @author : 流星
 * @CreateDate: 2022/3/5-0:52
 * @Description: 自定义的异常
 */
public class HttpException extends Throwable {

    private Throwable throwable;
    private Response response;

    public HttpException(Response response) {
        this.response = response;
    }

    public HttpException(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return "HttpException{" +
                "throwable=" + throwable +
                ", response=" + response +
                '}';
    }
}
