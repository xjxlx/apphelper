package com.android.helper.utils.download;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.android.common.utils.LogUtil;
import com.android.helper.httpclient.RetrofitHelper;
import com.android.helper.interfaces.listener.UploadProgressOkHttpListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 使用OkHttp封装的文件上传工具类
 */
public class UploadManagerOkHttp {

    private static final String TAG = "UploadManager";
    private static UploadManagerOkHttp manager;
    private MultipartBody.Builder builder;
    private OkHttpClient mHttpClient;
    /**
     * 封装Request请求对象的群，用来取消下载使用
     */
    private final HashMap<String, Call> mClientMap;
    private UploadProgressOkHttpListener mListener;

    // 当前上传的状态
    private int mUploadType = 0;

    /**
     * 当前下载的状态 1：正在上传中 ，2：上传完毕 3：上传错误 4：上传下载
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UPLOAD_TYPE.UPLOAD_START, UPLOAD_TYPE.UPLOAD_COMPLETE, UPLOAD_TYPE.UPLOAD_ERROR, UPLOAD_TYPE.UPLOAD_CANCEL})
    public @interface UPLOAD_TYPE {
        int UPLOAD_START = 1;
        int UPLOAD_PROGRESS = 2;
        int UPLOAD_DATA_COMPLETE = 3;
        int UPLOAD_COMPLETE = 4;
        int UPLOAD_ERROR = 5;
        int UPLOAD_CANCEL = 6;
    }

    /**
     * @return 获取一个单利的下载器对象
     */
    public synchronized static UploadManagerOkHttp getSingleInstance() {
        if (manager == null) {
            manager = new UploadManagerOkHttp();
        }
        return manager;
    }

    public UploadManagerOkHttp() {
        mClientMap = new HashMap<>();
        mHttpClient = RetrofitHelper.getTimeOutClient();
    }

    /**
     * @return 获取多文件上传参数的封装类
     */
    private MultipartBody.Builder getBuilder() {
        if (builder == null) {
            builder = new MultipartBody.Builder();
            // 一定要设置这句
            builder.setType(MultipartBody.FORM);
        }
        return builder;
    }

    /**
     * 添加一个普通的参数，例如：添加文本数据
     *
     * @param name  key
     * @param value value
     */
    public void addParameter(String name, String value) {
        getBuilder().addFormDataPart(name, value);
    }

    /**
     * 添加一个文件作为参数
     *
     * @param name     key
     * @param filename 文件名字
     * @param body     RequestBody
     */
    public void addParameter(String name, String filename, RequestBody body) {
        getBuilder().addFormDataPart(name, filename, body);
    }

    /**
     * 添加一个文件作为参数
     *
     * @param name key
     * @param file 具体的文件
     */
    public void addFileParameter(String name, File file) {
        if (file != null && file.exists()) {
            RequestBody bodyForUrl = RetrofitHelper.createBodyForUrl(file);
            if (bodyForUrl != null) {
                getBuilder().addFormDataPart(name, file.getName(), bodyForUrl);
            }
        }
    }

    /**
     * 开始上传文件，监听文件的上传进度
     *
     * @param url              上传的url地址
     * @param tag              上传文件的tag，用于取消文件的上传，如果不用取消，可以设置对象为null
     * @param progressListener 进度监听的对象
     */
    public void uploadFiles(@NotNull String url, @NotNull UploadProgressOkHttpListener progressListener, @Nullable String tag) {
        if ((TextUtils.isEmpty(url)) || (progressListener == null)) {
            return;
        }
        mListener = progressListener;

        if (builder == null) {
            throw new NullPointerException("请设置完参数后再提交！");
        }

        try {
            // 获取一个RequestBody的对象
            MultipartBody multipartBody = builder.build();

            Request.Builder builder = new Request.Builder().url(url)// 请求的url
                    .post(new ProgressRequestBody(multipartBody) {
                        @Override
                        public void onStart() {
                            // 开始上传的标记
                            mUploadType = UPLOAD_TYPE.UPLOAD_START;
                            Message message = mHandler.obtainMessage();
                            message.what = UPLOAD_TYPE.UPLOAD_START;
                            mHandler.sendMessage(message);
                            LogUtil.e("--------->onStart");
                        }

                        @Override
                        public void onProgress(long current, long contentLength, String percentage) {
                            // mUploadType = UPLOAD_TYPE.UPLOAD_PROGRESS;
                            mUploadType = 2;
                            Message message = mHandler.obtainMessage();
                            // message.what = UPLOAD_TYPE.UPLOAD_PROGRESS;
                            message.what = 2;
                            Bundle bundle = new Bundle();
                            bundle.putString("percentage", percentage);
                            bundle.putLong("progress", current);
                            bundle.putLong("contentLength", contentLength);
                            message.setData(bundle);
                            mHandler.sendMessage(message);

                            LogUtil.e("--------->onProgress ：" + percentage);
                        }

                        @Override
                        public void onComplete() {
                            // mUploadType = UPLOAD_TYPE.UPLOAD_DATA_COMPLETE;
                            mUploadType = 3;
                            Message message = mHandler.obtainMessage();
                            // message.what = UPLOAD_TYPE.UPLOAD_DATA_COMPLETE;
                            message.what = 3;
                            mHandler.sendMessage(message);
                            LogUtil.e("--------->UPLOAD_DATA_COMPLETE");
                        }
                    });

            // 如果不使用取消功能，可以不传tag
            if (!TextUtils.isEmpty(tag)) {
                // 根据tag,存入对应的请求对象，用于取消请求使用
                builder.tag(tag);
            }

            Call call = mHttpClient.newCall(builder.build());
            if (!TextUtils.isEmpty(tag)) {
                mClientMap.put(tag, call);
            }

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LogUtil.e("--------->onFailure");
                    // 上传错误的标记
                    mUploadType = UPLOAD_TYPE.UPLOAD_ERROR;
                    Message message = mHandler.obtainMessage();
                    message.what = UPLOAD_TYPE.UPLOAD_ERROR;
                    message.obj = e;
                    mHandler.sendMessage(message);
                    call.cancel();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    LogUtil.e("--------->onResponse");
                    UploadManagerOkHttp.this.builder = null;
                    clearParameter();

                    // 上传结束的标记
                    mUploadType = UPLOAD_TYPE.UPLOAD_COMPLETE;
                    Message message = mHandler.obtainMessage();
                    message.what = UPLOAD_TYPE.UPLOAD_COMPLETE;
                    message.obj = response;
                    mHandler.sendMessage(message);

                }
            });
        } catch (Exception e) {
            // 上传错误的标记
            mUploadType = UPLOAD_TYPE.UPLOAD_ERROR;
            Message message = mHandler.obtainMessage();
            message.what = UPLOAD_TYPE.UPLOAD_ERROR;
            message.obj = e;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 清空参数
     */
    private void clearParameter() {
        if (builder != null) {
            builder = null;
        }
    }

    /**
     * @param tag 该tag,必须和上传时候传入的tag相同，否则就会导致无法取消
     */
    public void cancel(@NotNull String tag) {
        if (mClientMap != null && mClientMap.size() > 0) {
            Call call = mClientMap.get(tag);
            if (call != null) {
                if (!call.isCanceled()) {
                    call.cancel();
                    mClientMap.remove(tag);
                }
            }
        }
    }

    /**
     * @return 获取当前文件上传的进度
     */
    public int getCurrentStatus() {
        return mUploadType;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_TYPE.UPLOAD_START:
                    mListener.onStart();
                    break;
                // case UPLOAD_TYPE.UPLOAD_PROGRESS:
                case 2:
                    Bundle data = msg.getData();

                    long progress = data.getLong("progress");
                    long contentLength = data.getLong("contentLength");
                    String percentage = data.getString("percentage");

                    mListener.onProgress(progress, contentLength, percentage);
                    break;
                // case UPLOAD_TYPE.UPLOAD_DATA_COMPLETE: // 数据上传完成，但是接口为完成
                case 3: // 数据上传完成，但是接口为完成
                    mListener.onUploadComplete();
                    break;
                case UPLOAD_TYPE.UPLOAD_COMPLETE:
                    Response response = (Response) msg.obj;
                    mListener.onComplete(response);
                    break;
                case UPLOAD_TYPE.UPLOAD_ERROR:
                    Exception exception = (Exception) msg.obj;
                    mListener.onError(exception);
                    break;
            }
        }
    };
}
