package com.android.helper.httpclient;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * 网络错误的过滤类，用来过滤一些常见的错误类型
 */
public class BaseException extends Throwable {

    private String message;
    private Throwable throwable;

    /**
     * 捕获异常的分离处理，主要是为了处理网络连接失败的错误，和网络超时的错误，其他的错误能展示出来的就尽量的展示出来，便于分析问题，后续如果有需要就再去添加各类的异常
     *
     * @param throwable 错误的原因
     */
    public BaseException(Throwable throwable) {
        this.throwable = throwable;

        if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {   //   连接错误
            this.message = "网络连接异常";

        } else if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {   //  连接超时
            this.message = "网络连接超时";

        } else {
            // 其他的异常
            this.message = throwable.getMessage();
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
