package com.android.helper.utils.download;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.android.helper.interfaces.listener.ProgressListener;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.SpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.android.helper.httpclient.RetrofitHelper.CUSTOM_TIMEOUT;

/**
 * 带进度的上传和下载的工具类
 */
public class DownLoadManager {

    private static final String TAG = "DownLoadManager";
    private static final String KEY_DOWNLOAD_FILE_CONTENT_LENGTH = "key_download_file_content_length";

    // 当前下载的状态，默认为闲置的状态
    private int mDownloadType = DOWNLOAD_TYPE.DOWNLOAD_IDLE;

    private static DownLoadManager manager;
    private ProgressListener mListener;

    /**
     * 当前下载的状态  1：正在下载中 ，2：下载完毕  3：下载错误  4：取消下载
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DOWNLOAD_TYPE.DOWNLOADING, DOWNLOAD_TYPE.DOWNLOAD_COMPLETE, DOWNLOAD_TYPE.DOWNLOAD_ERROR})
    public @interface DOWNLOAD_TYPE {
        int DOWNLOAD_START = 1; // 开始下载
        int DOWNLOADING = 2;  // 下载中
        int DOWNLOAD_COMPLETE = 3; // 下载完成
        int DOWNLOAD_ERROR = 4; // 下载异常
        int DOWNLOAD_IDLE = 6;// 闲置的状态
    }

    /**
     * 封装Request请求对象的群，用来取消下载使用
     */
    private final HashMap<String, Call> mClientMap;
    private final OkHttpClient okHttpClient;
    private final HashMap<String, Integer> mMapStatus = new HashMap<>();// 当前下载状态的集合




    /**
     * 已经下载文件的长度
     */
    private long mTempDownloadLength;

    /**
     * 文件的总长度
     */
    private long mContentLong;

    /**
     * 私有化构造
     */
    private DownLoadManager() {
        mClientMap = new HashMap<>();
        okHttpClient = new OkHttpClient
                .Builder()
                .readTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .build();
    }

    /**
     * @return 获取一个单利的下载器对象
     */
    public synchronized static DownLoadManager getSingleInstance() {
        if (manager == null) {
            manager = new DownLoadManager();
        }
        return manager;
    }

    /**
     * @param download         下载的对象信息
     * @param progressListener 回调
     */
    public void download(Download download, @NotNull ProgressListener progressListener) {
        mListener = progressListener;
        if (download == null || (TextUtils.isEmpty(download.getId())) || (TextUtils.isEmpty(download.getUrl())) || (TextUtils.isEmpty(download.getOutputPath()))) {
            LogUtil.e("下载的信息异常，停止下载！");
            return;
        }

        String id = download.getId();
        String outFilePath = download.getOutputPath();
        String url = download.getUrl();

        // 获取总文件的大小
        String maxLength = SpUtil.getStringForMap(KEY_DOWNLOAD_FILE_CONTENT_LENGTH, id);
        if (!TextUtils.isEmpty(maxLength)) {
            // 获取long类型的总文件大小
            mContentLong = Long.parseLong(maxLength);
        }

        // 初始化临时的状态
        mTempDownloadLength = 0;

        // 获取车辆控制状态
        int currentStatus = getCurrentStatus(id);
        // 如果状态是在开始下载 或者下载中，就停止下载
        if (currentStatus == DOWNLOAD_TYPE.DOWNLOADING || currentStatus == DOWNLOAD_TYPE.DOWNLOAD_START) {
            return;
        }

        // 判断下载地址的url
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("下载的路径不能为空！");
        }

        Request.Builder builder = new Request
                .Builder()
                .url(url)
                .tag(id);

        // 获取文件
        File file = new File(outFilePath);
        if (file.exists()) {
            LogUtil.e(TAG, "下载路径的文件存在！");
            if (mContentLong > 0) {
                LogUtil.e(TAG, "文件的总大小为：" + mContentLong);
                long length = file.length();
                if ((length > 0) && (length < mContentLong)) {
                    LogUtil.e(TAG, "满足断点续传的条件，开始执行断点续传！");
                    // 下载的文件小于总文件，获取对象，用于断点续传
                    mTempDownloadLength = length;
                    // 增加断点续传的节点
                    builder.addHeader("RANGE", "bytes=" + mTempDownloadLength + "-" + mContentLong);
                } else {
                    LogUtil.e(TAG, "不满足断点续传的条件，无法执行断点续传！");
                }
            } else {
                LogUtil.e(TAG, "文件的总大小小于0，不走断点续传的流程！");
            }
        } else {
            LogUtil.e(TAG, "下载路径的文件不存在！");
        }

        Call call = okHttpClient.newCall(builder.build());

        // 把请求对象存入集合中，用于取消数据使用
        mClientMap.put(id, call);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e(TAG, "下载错误：" + e.getMessage());
                call.cancel();

                // 下载错误的状态
                mDownloadType = DOWNLOAD_TYPE.DOWNLOAD_ERROR;

                // 发送错误的数据
                Message message = mHandler.obtainMessage();
                message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                message.obj = e;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                // 可以移动写入位置的类，用于多线程断点点在
                RandomAccessFile accessFile = null;
                boolean successful = response.isSuccessful();
                ResponseBody body = response.body();
                if (successful && (body != null)) {
                    // 获取输入流
                    InputStream inputStream = body.byteStream();
                    LogUtil.e(TAG, "code等于200,开始存入文件的总大小！");

                    if (response.code() == 200) {
                        // 只记录原始文件长度
                        long contentLength = body.contentLength();
                        // 存入文件的总体长度
                        SpUtil.putMap(KEY_DOWNLOAD_FILE_CONTENT_LENGTH, id, String.valueOf(contentLength));
                        mContentLong = contentLength;
                    }

                    // 文件输出流
                    try {
                        // "rw": 打开以便读取和写入。
                        accessFile = new RandomAccessFile(file, "rw");
                        // 跳过
                        if ((mTempDownloadLength > 0) && (mContentLong > 0) && (mTempDownloadLength < mContentLong)) {
                            try {
                                accessFile.seek(mTempDownloadLength);
                                LogUtil.e(TAG, "条件满足，跳过之前下载的数据!");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // 开始正式的下载
                        Message message = mHandler.obtainMessage();
                        message.what = DOWNLOAD_TYPE.DOWNLOAD_START;
                        message.obj = mContentLong;
                        mHandler.sendMessage(message);

                        // 文件下载过程中变化的进度
                        long progress = mTempDownloadLength;

                        // 一次性读取2048个字节
                        byte[] buf = new byte[2048];
                        int len = 0;
                        while ((len = inputStream.read(buf)) != -1) {
                            // 下载错误的状态
                            mDownloadType = DOWNLOAD_TYPE.DOWNLOADING;
                            // 写入到内存中
                            accessFile.write(buf, 0, len);

                            // 进度累计
                            progress += len;
                            // 格式化数据，并小数保留两位

                            double v = (progress * 0.1) / mContentLong;
                            String format = String.format(Locale.CHINA, "%.2f", (v * 1000));
                            LogUtil.e("v:" + v + "   form:" + format);
                            // 进度的回调
                            Message message1 = mHandler.obtainMessage();
                            message1.what = DOWNLOAD_TYPE.DOWNLOADING;
                            Bundle bundle = new Bundle();
                            bundle.putDouble("progress", progress);
                            bundle.putLong("contentLength", mContentLong);
                            bundle.putString("percentage", format);
                            message1.setData(bundle);
                            mHandler.sendMessage(message1);
                        }

                        // 下载完成的回调
                        mDownloadType = DOWNLOAD_TYPE.DOWNLOAD_COMPLETE;
                        Message message2 = mHandler.obtainMessage();
                        message2.what = DOWNLOAD_TYPE.DOWNLOAD_COMPLETE;
                        message2.obj = response;
                        mHandler.sendMessage(message2);

                    } catch (Exception e) {
                        mDownloadType = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                        Message message = mHandler.obtainMessage();
                        message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                        message.obj = e;
                        mHandler.sendMessage(message);
                    } finally {
                        try {
                            if (accessFile != null) {
                                accessFile.close();
                            }
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            mDownloadType = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                            Message message = mHandler.obtainMessage();
                            message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                            message.obj = e;
                            mHandler.sendMessage(message);
                        }
                    }
                } else {
                    // code 不正确的流程
                    mDownloadType = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                    Message message = mHandler.obtainMessage();
                    message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                    message.obj = new Throwable(response.message());
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    /**
     * @return 获取当前车辆的状态： 1：正在下载中 ，2：下载完毕  3：下载错误  4：取消下载
     */
    public int getCurrentStatus(String id) {
        int status = DOWNLOAD_TYPE.DOWNLOAD_IDLE;
        if (mMapStatus.size() > 0) {
            status = mMapStatus.get(id);
        }
        return status;
    }

    /**
     * 取消下载
     *
     * @param id 唯一的标记
     */
    public void cancel(@NonNull String id) {
        if (mClientMap.size() > 0) {
            Call call = mClientMap.get(id);
            if (call != null) {
                // 如果该对象已经取消了，就不用在去再次取消了
                boolean canceled = call.isCanceled();
                if (canceled) {
                    mDownloadType = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                } else {
                    call.cancel();
                    // 移除该请求对象
                    mClientMap.remove(id);
                }
                LogUtil.e("下载取消了！");
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case DOWNLOAD_TYPE.DOWNLOAD_START:
                    mListener.onStart((long) msg.obj);
                    break;
                case DOWNLOAD_TYPE.DOWNLOAD_ERROR:
                    mListener.onError((Exception) msg.obj);
                    break;
                case DOWNLOAD_TYPE.DOWNLOADING:
                    Bundle data = msg.getData();
                    if (data != null) {
                        double progress = data.getDouble("progress");
                        long contentLength = data.getLong("contentLength");
                        String percentage = data.getString("percentage");
                        mListener.onProgress(progress, contentLength, percentage);
                    }
                    break;
                case DOWNLOAD_TYPE.DOWNLOAD_COMPLETE:
                    mListener.onComplete((Response) msg.obj);
                    break;
            }
        }
    };

}
