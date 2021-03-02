package android.helper.httpclient;

import android.text.TextUtils;

import android.helper.app.BaseApplication;
import android.helper.utils.LogUtil;
import com.google.gson.JsonObject;

import java.io.EOFException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 自定义的日志拦截器
 */
public class HttpLogInterceptor implements Interceptor {

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private JsonObject mHeards;// 请求头
    private String mRequestBody;// 请求到的数据
    private String mParameter = "";// 请求参数
    private String mPath;// 请求路径
    private String decodeHeard;
    private String mMethod;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        // 请求方法
        mMethod = request.method();
        HttpUrl url = request.url();
        String path = url.encodedPath();
        if (!TextUtils.isEmpty(path)) {
            String baseUrl = BaseApplication.getBaseUrl();
            if (path.contains(baseUrl)) {
                mPath = path.replace(baseUrl, "");
            } else {
                mPath = path;
            }
        }

        String host = url.host();
        // 请求参数
        String parameters = url.encodedQuery();
        if (!TextUtils.isEmpty(parameters)) {
            assert parameters != null;
            mParameter = "{ " + parameters.replace("&", " , ") + " }";
        }

        // 获取请求头信息
        Headers headers = request.headers();
        if (headers.size() > 0) {
            mHeards = new JsonObject();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    String value = headers.value(i);
                    this.mHeards.addProperty(name, value);
                }
            }
        }

        if (!hasRequestBody) {
        } else if (bodyEncoded(request.headers())) {
            LogUtil.e("----net6", "----> END " + "  " + request.method() + " (encoded body omitted)");
        } else {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                mParameter = buffer.readString(charset);
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            LogUtil.e("----net10", "<-- 连接错误: " + e.getMessage());
            throw e;
        }

        // 请求时长
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        assert responseBody != null;
        long contentLength = responseBody.contentLength();
        // 装填码
        int code = response.code();

        if (!HttpHeaders.hasBody(response)) {
            LogUtil.e("----net12", "<---- END " + "HTTP");
        } else if (bodyEncoded(response.headers())) {
            LogUtil.e("----net13", "<---- END " + "HTTP (encoded body omitted)");
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                return response;
            }

            if (contentLength != 0) {
                assert charset != null;
                mRequestBody = buffer.clone().readString(charset);
            }
        }

        //  解析成最新的请求头
        try {
            if (mHeards != null && mHeards.size() > 0) {
                decodeHeard = URLDecoder.decode(mHeards.toString(), "UTF-8");
            } else {
                decodeHeard = "";
            }
        } catch (Exception ignored) {
        }

        LogUtil.e(String.format(Locale.CHINA,
                "请求方式:【 %s 】" +
                        "%n请求地址:【 %s 】" +
                        "%n请求域名:【 %s 】" +
                        "%n请求路径:【 %s 】" +
                        "%n请求头  :【 %s 】" +
                        "%n请求参数:【 %s 】" +
                        "%n响应时间:【 %s ms 】" +
                        "%n响应码: 【 %s 】" +
                        "%n返回内容:【 %s 】 ",
                mMethod,
                url,
                host,
                mPath,
                decodeHeard,
                mParameter,
                tookMs,
                code,
                mRequestBody
        ));

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}
