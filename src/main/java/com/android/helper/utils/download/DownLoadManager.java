package com.android.helper.utils.download;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.interfaces.listener.ProgressListener;
import com.android.helper.utils.FileUtil;
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
import java.util.Map;
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
public class DownLoadManager implements BaseLifecycleObserver {

    private static final String TAG = "DownLoadManager";
    private static final String KEY_DOWNLOAD_FILE_CONTENT_LENGTH = "key_download_file_content_length";

    /**
     * 封装Request请求对象的群，用来取消下载使用
     */
    private HashMap<String, Call> mClientMap = new HashMap<>();
    private OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
            .readTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();

    private HashMap<String, Integer> mMapStatus = new HashMap<>();// 当前下载状态的集合
    private HashMap<String, ProgressListener> mMapListener = new HashMap<>();//回调对象的集合

    /**
     * 已经下载文件的长度
     */
    private long mTempDownloadLength;

    /**
     * 文件的总长度
     */
    private long mContentLong;

    private boolean mRepeatDownload;
    private FragmentActivity mActivity;
    private Fragment mFragment;

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

        // 停止所有的请求
        cancelAll();

        // 清空状态集合
        if (mMapStatus != null) {
            mMapStatus.clear();
            mMapStatus = null;
        }

        // 清空回调对象
        if (mMapListener != null) {
            mMapListener.clear();
            mMapListener = null;
        }

        if (okHttpClient != null) {
            okHttpClient = null;
        }

        if (mActivity != null) {
            mActivity = null;
        }

        if (mFragment != null) {
            mFragment = null;
        }

        LogUtil.e(TAG, "清空所有的数据！");
    }

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
     * 私有化构造
     */
    private DownLoadManager(Builder builder) {
        if (builder != null) {
            mRepeatDownload = builder.mRepeatDownload;
            mActivity = builder.mActivity;
            mFragment = builder.mFragment;

            if (mActivity != null) {
                Lifecycle lifecycle = mActivity.getLifecycle();
                lifecycle.addObserver(this);
            }

            if (mFragment != null) {
                Lifecycle lifecycle = mFragment.getLifecycle();
                lifecycle.addObserver(this);
            }
        }
    }

    /**
     * @param download         下载的对象信息
     * @param progressListener 回调
     */
    public void download(Download download, @NotNull ProgressListener progressListener) {
        if (download == null || (TextUtils.isEmpty(download.getId())) || (TextUtils.isEmpty(download.getUrl())) || (TextUtils.isEmpty(download.getOutputPath()))) {
            LogUtil.e(TAG, "下载的信息异常，停止下载！");
            return;
        }

        String id = download.getId();
        String outFilePath = download.getOutputPath();
        String url = download.getUrl();

        int currentStatus = getCurrentStatus(id);
        // 如果状态是在开始下载 或者下载中，就停止下载
        if (currentStatus == DOWNLOAD_TYPE.DOWNLOADING || currentStatus == DOWNLOAD_TYPE.DOWNLOAD_START) {
            LogUtil.e(TAG, "正在下载中，停止重复性下载！");
            return;
        }

        // 存入回调对象的集合
        if (mMapListener != null) {
            mMapListener.put(id, progressListener);
        }

        // 数据置空
        mContentLong = 0;
        // 初始化临时的状态
        mTempDownloadLength = 0;

        // 获取总文件的大小
        String maxLength = SpUtil.getStringForMap(KEY_DOWNLOAD_FILE_CONTENT_LENGTH, id);
        if (!TextUtils.isEmpty(maxLength)) {
            // 获取long类型的总文件大小
            assert maxLength != null;
            mContentLong = Long.parseLong(maxLength);
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
                long length = file.length();
                LogUtil.e(TAG, "文件的总大小为：" + mContentLong + "  当前文件的大小为：" + length);
                if ((length > 0) && (mContentLong > 0)) {
                    if (length < mContentLong) {
                        LogUtil.e(TAG, "满足断点续传的条件，开始执行断点续传！");
                        // 下载的文件小于总文件，获取对象，用于断点续传
                        mTempDownloadLength = length;
                        // 增加断点续传的节点
                        builder.addHeader("RANGE", "bytes=" + mTempDownloadLength + "-" + mContentLong);
                    } else if (length == mContentLong) {
                        LogUtil.e(TAG, "文件已经下载成功了！");

                        // 重复性下载的操作
                        if (mRepeatDownload) {
                            // 重新生成一个新的文件名字
                            String copyFilePath = FileUtil.getInstance().copyFilePath(file, ".", mContentLong);
                            if (!TextUtils.isEmpty(copyFilePath)) {
                                // 重新生成文件
                                file = new File(copyFilePath);
                                LogUtil.e(TAG, "重新生成的文件路径为：" + copyFilePath);
                                // 重新加入断点续传的操作
                                long copyLength = file.length();
                                if ((copyLength > 0) && (copyLength < mContentLong)) {
                                    mTempDownloadLength = copyLength;
                                    // 增加断点续传的节点
                                    builder.addHeader("RANGE", "bytes=" + mTempDownloadLength + "-" + mContentLong);
                                    LogUtil.e("重新加入断点续传的操作！");
                                }
                            }
                        } else {
                            // 单一文件，只认原始的文件
                            /****************回调文件下载完成*********************/

                            // 下载完成的回调
                            setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_COMPLETE);

                            // 构建bundle的对象
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("path", outFilePath);

                            Message message = mHandler.obtainMessage();
                            message.what = DOWNLOAD_TYPE.DOWNLOAD_COMPLETE;
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                            LogUtil.e(TAG, "单一模式，文件直接返回下载成功！");
                            return;
                        }
                    }
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
        if (mClientMap != null) {
            mClientMap.put(id, call);
        }

        // 构建bundle的对象
        Bundle bundle = new Bundle();
        bundle.putString("id", id);

        File finalFile = file;
        LogUtil.e(TAG, "最终的文件路径为：" + finalFile.getPath());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e(TAG, "下载错误：" + e.getMessage());
                call.cancel();

                // 下载错误的状态
                setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_ERROR);

                // 发送错误的数据
                Message message = mHandler.obtainMessage();
                message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                message.obj = e;
                message.setData(bundle);
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
                    int code = response.code();
                    LogUtil.e(TAG, "code:" + code);

                    if (code == 200) {
                        // 只记录原始文件长度
                        long contentLength = body.contentLength();
                        // 存入文件的总体长度
                        SpUtil.putMap(KEY_DOWNLOAD_FILE_CONTENT_LENGTH, id, String.valueOf(contentLength));
                        mContentLong = contentLength;
                    }

                    // 文件输出流
                    try {
                        // "rw": 打开以便读取和写入。
                        accessFile = new RandomAccessFile(finalFile, "rw");
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
                        setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_START);
                        Message message = mHandler.obtainMessage();
                        message.what = DOWNLOAD_TYPE.DOWNLOAD_START;
                        message.obj = mContentLong;
                        message.setData(bundle);
                        mHandler.sendMessage(message);

                        // 文件下载过程中变化的进度
                        long progress = mTempDownloadLength;

                        // 一次性读取2048个字节
                        byte[] buf = new byte[2048];
                        int len = 0;

                        // 正常来讲，这里应该是放入到循环里面的，但是为了减轻代码的繁琐程度，写在了外边
                        // 下载中的状态
                        setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOADING);
                        String format = "0";
                        LogUtil.e(TAG, "下载中...");

                        while ((len = inputStream.read(buf)) != -1) {
                            // 写入到内存中
                            accessFile.write(buf, 0, len);

                            // 进度累计
                            progress += len;
                            // 格式化数据，并小数保留两位
                            if (progress > 0 && mContentLong > 0) {
                                double v = (progress * 0.1) / mContentLong;
                                format = String.format(Locale.CHINA, "%.2f", (v * 1000));
                            }

                            // 进度的回调
                            Message message1 = mHandler.obtainMessage();
                            message1.what = DOWNLOAD_TYPE.DOWNLOADING;

                            bundle.putLong("progress", progress);
                            bundle.putLong("contentLength", mContentLong);
                            bundle.putString("percentage", format);
                            message1.setData(bundle);
                            mHandler.sendMessage(message1);
                        }

                        // 下载完成的回调
                        setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_COMPLETE);

                        Message message2 = mHandler.obtainMessage();
                        message2.what = DOWNLOAD_TYPE.DOWNLOAD_COMPLETE;
                        message2.obj = response;
                        bundle.putString("path", finalFile.getPath());
                        message2.setData(bundle);
                        mHandler.sendMessage(message2);

                    } catch (Exception e) {
                        setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_ERROR);

                        Message message = mHandler.obtainMessage();
                        message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                        message.obj = e;
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } finally {
                        try {
                            if (accessFile != null) {
                                accessFile.close();
                            }
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_ERROR);
                            Message message = mHandler.obtainMessage();
                            message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                            message.obj = e;
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }
                } else {
                    // code 不正确的流程
                    setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_ERROR);
                    Message message = mHandler.obtainMessage();
                    message.what = DOWNLOAD_TYPE.DOWNLOAD_ERROR;
                    message.obj = new Throwable(response.message());
                    message.setData(bundle);
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
        if ((mMapStatus != null) && (mMapStatus.size() > 0) && (!TextUtils.isEmpty(id))) {
            Integer tempStatus = mMapStatus.get(id);
            if (tempStatus != null) {
                status = tempStatus;
            }
        }
        return status;
    }

    /**
     * 设置当前的状态
     */
    public void setCurrentStatus(String id, int status) {
        if (mMapStatus != null) {
            mMapStatus.put(id, status);
        }
    }

    /**
     * 取消下载
     *
     * @param id 唯一的标记
     */
    public void cancel(@NonNull String id) {
        if ((mClientMap != null) && (mClientMap.size() > 0)) {
            Call call = mClientMap.get(id);
            if (call != null) {
                // 如果该对象已经取消了，就不用在去再次取消了
                boolean canceled = call.isCanceled();
                if (canceled) {
                    setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_ERROR);
                } else {
                    call.cancel();
                    // 移除该请求对象
                    mClientMap.remove(id);
                }
                LogUtil.e(TAG, "下载取消了！");
            }
        }
    }

    private void rangeFile(Request.Builder builder, long length, long contentLong) {

        if (contentLong > 0) {
            LogUtil.e(TAG, "文件的总大小为：" + mContentLong + "  当前文件的大小为：" + length);
            if ((length > 0) && (mContentLong > 0)) {
                if (length < mContentLong) {
                    LogUtil.e(TAG, "满足断点续传的条件，开始执行断点续传！");
                    // 下载的文件小于总文件，获取对象，用于断点续传
                    mTempDownloadLength = length;
                    // 增加断点续传的节点
                    builder.addHeader("RANGE", "bytes=" + mTempDownloadLength + "-" + mContentLong);
                }
            } else {
                LogUtil.e(TAG, "不满足断点续传的条件，无法执行断点续传！");
            }
        } else {
            LogUtil.e(TAG, "文件的总大小小于0，不走断点续传的流程！");
        }
    }

    public void cancelAll() {
        if (mClientMap.size() > 0) {
            for (Map.Entry<String, Call> entry : mClientMap.entrySet()) {
                String id = entry.getKey();
                Call call = mClientMap.get(id);
                if (call != null) {
                    // 如果该对象已经取消了，就不用在去再次取消了
                    boolean canceled = call.isCanceled();
                    if (canceled) {
                        setCurrentStatus(id, DOWNLOAD_TYPE.DOWNLOAD_ERROR);
                    } else {
                        call.cancel();
                    }
                    LogUtil.e(TAG, "全部下载取消了！");
                }
            }
        }
        mClientMap.clear();
        mClientMap = null;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mMapListener != null) {
                Bundle data = msg.getData();
                String id = data.getString("id");
                ProgressListener mListener = mMapListener.get(id);
                assert mListener != null;

                switch (msg.what) {
                    case DOWNLOAD_TYPE.DOWNLOAD_START:
                        LogUtil.e(TAG, "开始下载!");

                        mListener.onStart(id, (long) msg.obj);
                        break;

                    case DOWNLOAD_TYPE.DOWNLOAD_ERROR:
                        Throwable throwable = (Throwable) msg.obj;
                        LogUtil.e(TAG, " 下载异常：" + throwable.getMessage());
                        mListener.onError(id, throwable);
                        break;

                    case DOWNLOAD_TYPE.DOWNLOADING:
                        long progress = data.getLong("progress");
                        long contentLength = data.getLong("contentLength");
                        String percentage = data.getString("percentage");
                        mListener.onProgress(id, progress, contentLength, percentage);

                        break;

                    case DOWNLOAD_TYPE.DOWNLOAD_COMPLETE:
                        LogUtil.e(TAG, "下载结束!");
                        Object obj = msg.obj;
                        Response response = null;
                        if (obj != null) {
                            response = (Response) obj;
                        }
                        String path = data.getString("path");

                        mListener.onComplete(id, path, response);
                        break;
                }
            }
        }
    };

    public static class Builder {
        /**
         * 是否重复性下载，默认重复性下载
         */
        private boolean mRepeatDownload = true;

        /**
         * 是否绑定布局，在页面销毁的时候去停止所有的操作，并释放对象
         */
        private FragmentActivity mActivity;
        private Fragment mFragment;

        /**
         * 是否绑定页面，如果绑定了页面，就会在页面关闭的时候，同时取消所有的下载，并释放对象，默认不绑定
         */
        public Builder setRepeatDownload(boolean repeatDownload) {
            this.mRepeatDownload = repeatDownload;
            return this;
        }

        /**
         * @return 绑定页面，如果绑定了页面，就会在页面关闭的时候，同时取消所有的下载，并释放对象，默认不绑定
         */
        public Builder bindDownload(FragmentActivity activity) {
            this.mActivity = activity;
            return this;
        }

        /**
         * @return 绑定页面，如果绑定了页面，就会在页面关闭的时候，同时取消所有的下载，并释放对象，默认不绑定
         */
        public Builder bindDownload(Fragment fragment) {
            this.mFragment = fragment;
            return this;
        }

        public DownLoadManager build() {
            return new DownLoadManager(this);
        }

    }

}