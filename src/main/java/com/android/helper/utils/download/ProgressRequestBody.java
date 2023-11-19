package com.android.helper.utils.download;

import com.android.common.utils.LogUtil;
import com.android.helper.utils.NumberUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public abstract class ProgressRequestBody extends RequestBody {
    private final String Tag = "ProgressRequestBody";

    // 实际的待包装响应体
    private final RequestBody requestBody;
    // 包装完成的BufferedSink
    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     */
    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) {
        bufferedSink = Okio.buffer(sink(sink));
        try {
            // 写入
            requestBody.writeTo(bufferedSink);
            // 必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();
            // 数据写入完成的回调
            onComplete();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(Tag, "--------->onError:" + e.getMessage());
        }
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        // 开始上传数据
        onStart();
        return new ForwardingSink(sink) {
            // 当前写入字节数
            long bytesWritten = 0L;
            // 总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    // 获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                // 增加当前写入的字节数
                bytesWritten += byteCount;
                // 转换格式
                String digits = NumberUtil.formatDigitsForDouble(((bytesWritten * 0.1 / contentLength * 1000)), BigDecimal.ROUND_DOWN, 2);
                LogUtil.e("bytesWritten  :" + bytesWritten + "  contentLength :" + contentLength + "  digits:" + digits);
                // 进度上传回调
                onProgress(bytesWritten, contentLength, digits);
            }
        };
    }

    public abstract void onStart();

    public abstract void onProgress(long current, long contentLength, String percentage);

    public abstract void onComplete();

}
