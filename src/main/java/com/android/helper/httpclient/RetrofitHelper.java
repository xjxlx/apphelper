package com.android.helper.httpclient;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.android.helper.app.BaseApplication;
import com.android.helper.utils.LogUtil;
import com.google.gson.Gson;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * retrofit 的封装类，后续待完善
 * 1：如果要设置BaseUrl和拦截器的话，一定要在BaseApplication的子类application中去设置，避免数据不及时
 * 2：常用的必须设置成单利的，避免重复的请求数据
 */
@SuppressLint("BadHostnameVerifier,TrustAllX509TrustManager")
public class RetrofitHelper {

    private static final int DEFAULT_CONNECT_TIMEOUT = 15; //默认的请求和超时、以及连接时间
    public static int CUSTOM_TIMEOUT = 35; // 默认超时时间为35秒

    /**
     * 这个是整个项目中的默认获取retrofit的方式，这里把他设置成静态变量，避免重复性的加载
     */
    private static final Retrofit retrofit = createRetrofit();

    /**
     * 这个是整个项目中自定义超时时间的retrofit的方式，这里把他设置成静态变量，避免重复性的加载
     */
    private static final Retrofit retrofitTimeOut = createRetrofitTimeOut();

    // 设置https 访问的时候对所有证书都进行信任
    private static SSLSocketFactory mSslSocketFactory;

    /**
     * @return 获取项目默认的retrofit对象
     */
    private static synchronized Retrofit createRetrofit() {
        LogUtil.e("获取createRetrofit");

        OkHttpClient.Builder httpBuilder = getHttpBuilder();

        httpBuilder
                .readTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);//设置连接超时时间

        // 获取拦截器
        Interceptor[] interceptors = BaseApplication.getInstance().getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                httpBuilder.addInterceptor(interceptor);
            }
        }

        // 设置SSL证书
        setSSLFactory(httpBuilder);

        return new Retrofit.Builder().client(httpBuilder.build())
                .baseUrl(BaseApplication.getInstance().getBaseUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * @return 自定义超时时间的retrofit对象，默认读写超时未35秒，如果是一个固定的超时时间的话，可以在程序的入口处直接修改 CUSTOM_TIMEOUT的固定时间
     */
    private static synchronized Retrofit createRetrofitTimeOut() {
        LogUtil.e("createTimeOutRetrofitTime");

        OkHttpClient timeOutClient = getTimeOutClient();

        return new Retrofit.Builder()
                .client(timeOutClient)
                .baseUrl(BaseApplication.getInstance().getBaseUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static RequestBody createBodyForJson(String json) {
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
    }

    public static RequestBody createBodyForMap(Map<String, Object> map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
    }

    /**
     * @param file 指定的文件
     * @return 返回一个Mp4视频格式的RequestBody
     */
    public static RequestBody createBodyForMp4(File file) {
        MediaType mediaType = MediaType.parse("video/mp4");
        return RequestBody.create(mediaType, file);
    }

    /**
     * @param file 指定的文件
     * @return 返回一个jpeg 或者png图片格式的RequestBody
     */
    public static RequestBody createBodyForJpg(File file) {
        MediaType mediaType = MediaType.parse("image/jpeg");
        return RequestBody.create(mediaType, file);
    }

    /**
     * @param url 文件的绝对路径，例如：file.getAbsolutePath()
     * @return 获取任何文件的mime类型
     */
    public static String getMimeTypeForUrl(String url) {
        String type = "";
        if (!TextUtils.isEmpty(url)) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        return type;
    }

    /**
     * @param file 具体的文件
     * @return 根据一个文件对象，去返回一个RequestBody的对象
     */
    public static RequestBody createBodyForUrl(File file) {
        if (file != null && file.exists()) {
            String mimeTypeForUrl = getMimeTypeForUrl(file.getAbsolutePath());
            MediaType mediaType = MediaType.parse(mimeTypeForUrl);
            if (mediaType != null) {
                return RequestBody.create(mediaType, file);
            }
        }
        return null;
    }

    /**
     * @param service 具体的接口class文件
     * @param <T>     ApiService的接口类型
     * @return 通过一个Class类去获取一个retrofit的对象
     */
    public static <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    /**
     * @return 通过一个Class类去获取一个固定超时时间的retrofit对象
     */
    public static <T> T createFixed(Class<T> service) {
        return retrofitTimeOut.create(service);
    }

    /**
     * @param time    自定义的超时时间
     * @param service 服务的对象
     * @param <T>     数据类型
     * @return 设置一个动态超时时间的retrofit对象，尽量不要使用这个，否则会导致每次加载很多数据
     */
    public static <T> T create(int time, Class<T> service) {
        CUSTOM_TIMEOUT = time;
        return createRetrofitTimeOut().create(service);
    }

    /**
     * @return 获取一个自定义超时时间的httpClient
     */
    public static OkHttpClient getTimeOutClient() {
        LogUtil.e("createTimeOutRetrofitTime");

        OkHttpClient.Builder httpBuilder = getHttpBuilder();

        // 获取拦截器
        Interceptor[] interceptors = BaseApplication.getInstance().getInterceptors();
        // 避免重复添加拦截器
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                httpBuilder.addInterceptor(interceptor);
            }
        }

        // 设置SSL证书
        setSSLFactory(httpBuilder);

        httpBuilder
                .readTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CUSTOM_TIMEOUT, TimeUnit.SECONDS);//设置连接超时时间
        return httpBuilder.build();
    }

    /**
     * @return 获取HttpBuilder
     */
    public static OkHttpClient.Builder getHttpBuilder() {
        // 构建HttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 添加日志的log
        if (BaseApplication.getInstance().isDebug()) {
            builder.addInterceptor(new HttpLogInterceptor());
        }

        return builder;
    }

    @SuppressLint("TrustAllX509TrustManager,BadHostnameVerifier")
    public static void setSSLFactory(OkHttpClient.Builder builder) {
        // 校验SSL
        X509TrustManager mX509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        // 校验主机名
        HostnameVerifier mHostnameVerifier = (hostname, session) -> true;

        // 设置https 访问的时候对所有证书都进行信任
        if (builder != null) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                if (sslContext != null) {
                    sslContext.init(null, new TrustManager[]{mX509TrustManager}, new SecureRandom());
                    mSslSocketFactory = sslContext.getSocketFactory();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 信任所有的证书
            if ((mSslSocketFactory != null) && (mX509TrustManager != null)) {
                builder.sslSocketFactory(mSslSocketFactory, mX509TrustManager);
            }

            // 不校验主机名
            if (mHostnameVerifier != null) {
                builder.hostnameVerifier(mHostnameVerifier);
            }
        }
    }

    /**
     * @return 带指定BaseUrl的创建方法
     */
    public static <T> T create(String baseUrl, Class<T> service) {

        OkHttpClient.Builder httpBuilder = getHttpBuilder();

        httpBuilder
                .readTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);//设置连接超时时间

        // 获取拦截器
        Interceptor[] interceptors = BaseApplication.getInstance().getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                httpBuilder.addInterceptor(interceptor);
            }
        }

        // 设置SSL证书
        setSSLFactory(httpBuilder);

        Retrofit build = new Retrofit.Builder().client(httpBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return build.create(service);
    }

}
