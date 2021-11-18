package com.android.helper.interfaces.listener;

import okhttp3.Response;

public interface ProgressListener {

    /**
     * 任务开始了
     *
     * @param contentLength 文件的总大小
     */
    void onStart(String id, long contentLength);

    /**
     * 进度改变中
     *
     * @param progress      当前的进度
     * @param contentLength 总的进度
     */
    void onProgress(String id, long progress, long contentLength, String percentage);

    void onError(String id, Throwable throwable);

    /**
     * 任务完成了
     */
    void onComplete(String id, String path, Response response);
}
