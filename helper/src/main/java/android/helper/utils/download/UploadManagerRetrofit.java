package android.helper.utils.download;

import android.text.TextUtils;

import androidx.annotation.IntDef;

import android.helper.app.BaseApplication;
import android.helper.httpclient.RetrofitHelper;
import android.helper.interfaces.listener.UploadProgressListener;
import android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 使用OkHttp封装的文件上传工具类
 */
public class UploadManagerRetrofit {

    private static final String TAG = "UploadManager";
    private static int mTimeOut = 60 * 10; // 默认10分钟，应该是足够了的，可以自定义

    /**
     * 封装Call请求对象的群，用来取消上传使用
     */
    private final HashMap<String, Call> mClientMap = new HashMap<>();
    /**
     * 参数的集合
     */
    private final List<MultipartBody.Part> mListPart = new ArrayList<>();// 参数的集合
    // 当前上传的状态
    public static int UP_LOAD_TYPE = 0;
    private static UploadManagerRetrofit manager;
    private Retrofit retrofit;

    /**
     * 当前下载的状态  1：正在上传中 ，2：上传完毕  3：上传错误  4：上传下载
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

    public static UploadManagerRetrofit getInstance() {
        if (manager == null) {
            manager = new UploadManagerRetrofit();
        }
        return manager;
    }

    public static UploadManagerRetrofit getInstance(int outTime) {
        if (manager == null) {
            manager = new UploadManagerRetrofit(outTime);
        }
        return manager;
    }

    /**
     * 普通的构造方法
     */
    private UploadManagerRetrofit() {
    }

    /**
     * @param timeOut 带超时时间的构造方法
     */
    private UploadManagerRetrofit(int timeOut) {
        mTimeOut = timeOut;
    }


    /**
     * 添加一个普通的参数，例如：添加文本数据
     *
     * @param name  key
     * @param value value
     */
    public UploadManagerRetrofit addParameter(String name, String value) {
        if ((!TextUtils.isEmpty(name)) && (value != null)) {
            mListPart.add(MultipartBody.Part.createFormData(name, value));
        }
        return this;
    }

    /**
     * 添加一个文件作为参数
     *
     * @param name key
     * @param file 具体的文件
     */
    public UploadManagerRetrofit addFileParameter(String name, File file) {
        if (file != null && file.exists()) {
            RequestBody bodyForUrl = RetrofitHelper.createBodyForUrl(file);
            if ((bodyForUrl != null) && (!TextUtils.isEmpty(name))) {
                mListPart.add(MultipartBody.Part.createFormData(name, file.getName(), bodyForUrl));
            }
        }
        return this;
    }

    /**
     * 清空参数
     */
    private void clearParameter() {
        mListPart.clear();
    }

    /**
     * @return 获取请求参数
     */
    public List<MultipartBody.Part> getParameter() {
        return mListPart;
    }

    /**
     * @param tag 该tag,必须和上传时候传入的tag相同，否则就会导致无法取消
     */
    public void cancel(@NotNull String tag) {
        if (mClientMap.size() > 0) {
            Call call = mClientMap.get(tag);
            if (call != null) {
                cancel(call, tag);
            }
        }
    }

    private void cancel(@NotNull Call call, @NotNull String tag) {
        if (!call.isCanceled()) {
            call.cancel();
            mClientMap.remove(tag);
        }
    }

    /**
     * @return 获取retrofit的对象
     */
    public <T> Retrofit getRetrofit(UploadProgressListener<T> listener) {
        // 如果不使用进度监听对象的话，那么retrofit没有必要每次去构造
        OkHttpClient.Builder httpBuilder = RetrofitHelper.getHttpBuilder();
        httpBuilder
                .readTimeout(mTimeOut, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(mTimeOut, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(mTimeOut, TimeUnit.SECONDS);//设置连接超时时间

        if (listener != null) {
            httpBuilder.addInterceptor(new UploadInterceptor<>(listener));
            retrofit = new Retrofit
                    .Builder()
                    .client(httpBuilder.build())
                    .baseUrl(BaseApplication.getBaseUrl())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else {
            if (retrofit == null) {
                retrofit = new Retrofit
                        .Builder()
                        .client(httpBuilder.build())
                        .baseUrl(BaseApplication.getBaseUrl())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }

    /**
     * 开始上传文件
     *
     * @param tag  目标的tag，用来取消正在上传的文件
     * @param call call对象
     */
    public <T> void uploadFiles(String tag, Call<T> call, UploadProgressListener<T> progressListener) {

        // 取消上一个对象
        if (mClientMap.size() > 0) {
            Call cancel = mClientMap.get(tag);
            if (cancel != null) {
                // 先取消上一个对象
                cancel(cancel, tag);
                return;
            }
        }

        if (mListPart.size() <= 0) {
            LogUtil.e("请设置完参数后再提交！");
            return;
        }

        // 开始请求数据
        if (call != null) {
            // 把call对象存到集合中去
            mClientMap.put(tag, call);

            // 开始异步执行
            call.enqueue(new BaseResponseCallback<T>() {
                @Override
                public void onSuccess(Call<T> call, Response<T> response, T t) {
                    LogUtil.e("--------->onSuccess" + t);
                    // 清空参数
                    clearParameter();

                    // 上传结束的标记
                    UP_LOAD_TYPE = UPLOAD_TYPE.UPLOAD_COMPLETE;
                    if (progressListener != null) {
                        progressListener.onComplete(response, t);
                    }
                    cancel(call, tag);
                }

                @Override
                public void onError(Call<T> call, Throwable t) {
                    LogUtil.e("--------->onError");
                    // 清空参数
                    clearParameter();
                    UP_LOAD_TYPE = UPLOAD_TYPE.UPLOAD_ERROR;
                    if (progressListener != null) {
                        progressListener.onError(t);
                    }
                    cancel(call, tag);
                }
            });
        }
    }
}
