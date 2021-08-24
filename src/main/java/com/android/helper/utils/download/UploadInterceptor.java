package com.android.helper.utils.download;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.helper.interfaces.listener.UploadProgressListener;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 上传进度的拦截器
 */
public class UploadInterceptor<T> implements Interceptor {

    private UploadProgressListener<T> mListener;

    public UploadInterceptor(@NotNull UploadProgressListener<T> listener) {
        mListener = listener;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        //封装request对象
        Request request = NetRequest(chain.request());
        return chain.proceed(request);
    }

    private Request NetRequest(Request request) {
        if (request == null || request.body() == null) {
            return request;
        }
        Request.Builder builder = request.newBuilder();
        //封装requestBody，传入参数，获取数据进度回调
        builder.method(request.method(), new ProgressRequestBody(request.body()) {
            @Override
            public void onStart() {
                // 开始上传的标记
                UploadManagerRetrofit.UP_LOAD_TYPE = UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_START;
                Message message = mHandler.obtainMessage();
                message.what = UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_START;
                mHandler.sendMessage(message);
            }

            @Override
            public void onProgress(long current, long contentLength, String percentage) {
                UploadManagerRetrofit.UP_LOAD_TYPE = UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_PROGRESS;
                Message message = mHandler.obtainMessage();
                message.what = UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_PROGRESS;
                Bundle bundle = new Bundle();
                bundle.putString("percentage", percentage);
                bundle.putLong("progress", current);
                bundle.putLong("contentLength", contentLength);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onComplete() {
                UploadManagerRetrofit.UP_LOAD_TYPE = UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_DATA_COMPLETE;
                Message message = mHandler.obtainMessage();
                message.what = UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_DATA_COMPLETE;
                mHandler.sendMessage(message);
            }
        });
        return builder.build();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mListener != null) {
                switch (msg.what) {
                    case UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_START:
                        mListener.onStart();
                        LogUtil.e("--------->onStart");
                        break;
                    case UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_PROGRESS:
                        Bundle data = msg.getData();
                        long progress = data.getLong("progress");
                        long contentLength = data.getLong("contentLength");
                        String percentage = data.getString("percentage");

                        mListener.onProgress(progress, contentLength, percentage);
                        break;
                    case UploadManagerRetrofit.UPLOAD_TYPE.UPLOAD_DATA_COMPLETE: // 数据上传完成，但是接口为完成
                        mListener.onUploadComplete();
                        LogUtil.e("--------->UPLOAD_DATA_COMPLETE");
                        break;
                }
            }
        }
    };
}
