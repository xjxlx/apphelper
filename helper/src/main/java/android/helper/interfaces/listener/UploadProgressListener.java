package android.helper.interfaces.listener;

import retrofit2.Response;

public interface UploadProgressListener<T> {

    /**
     * 任务开始了
     */
    void onStart();

    /**
     * 进度改变中
     *
     * @param progress      当前的进度
     * @param contentLength 总的进度
     */
    void onProgress(long progress, long contentLength, String percentage);

    /**
     * 数据上传完成的标记，这个时候接口没有完成，只是上传到服务器完成了
     */
    void onUploadComplete();

    /**
     * 任务完成了
     */
    void onComplete(Response<T> response, T t);

    void onError(Throwable throwable);
}
