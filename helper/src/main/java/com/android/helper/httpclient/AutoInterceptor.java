package com.android.helper.httpclient;

import android.text.TextUtils;

import com.android.helper.app.BaseApplication;
import com.android.helper.utils.AppUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by spc on 2017/6/17.
 */

public class AutoInterceptor implements Interceptor {

    private String encode;
    private String mToken = "";
    private static String mOneId = "";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (TextUtils.isEmpty(encode)) {
            AppUtil appUtil = new AppUtil(BaseApplication.getApplication());
            String appInfo = appUtil.getAppInfo();
            encode = URLEncoder.encode(appInfo, "UTF-8");
        }

        // 智慧管家
        if (!TextUtils.isEmpty(mToken) && !TextUtils.isEmpty(mToken)) {
            request = request.newBuilder()
                    .addHeader("authorization", "Bearer " + mToken)
                    .addHeader("versionInfo", encode)
                    .build();
        }

        // 添加请求头  一丰的项目
        String token = "39d5bf3e9b5fc7e41f949063fb139309";
        if (!TextUtils.isEmpty(token)) {
            request = request.newBuilder()
                    .addHeader("Authorization", token)
                    .addHeader("plat_number", "1")
                    .build();
        }

        if (request.method().equals("GET")) {
            request = addGetParams(request);
        } else if (request.method().equals("POST")) {
//            request = addPostJsonParams(request);
        }

        return chain.proceed(request);
    }

    //get请求 添加公共参数 签名
    private static Request addGetParams(Request request) {
        //添加公共参数
        HttpUrl httpUrl = request.url()
                .newBuilder()
                .addQueryParameter("oneId", mOneId)
                .build();
        request = request.newBuilder().url(httpUrl).build();
        return request;
    }

    //post 添加签名和公共参数
    private Request addPostParams(Request request) throws UnsupportedEncodingException {
        if (request.body() instanceof FormBody) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            FormBody formBody = (FormBody) request.body();

            //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
            for (int i = 0; i < formBody.size(); i++) {
                bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
            }
            formBody = bodyBuilder
                    .addEncoded("oneId", mOneId)
                    .build();
            request = request.newBuilder().post(formBody).build();
        }
        return request;
    }

    //body 转 字节
    public static byte[] toByteArray(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        InputStream inputStream = buffer.inputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] bufferWrite = new byte[1024];
        int n;
        while (-1 != (n = inputStream.read(bufferWrite))) {
            output.write(bufferWrite, 0, n);
        }
        return output.toByteArray();
    }
}
