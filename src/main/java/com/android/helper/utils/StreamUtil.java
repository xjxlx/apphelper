package com.android.helper.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 数据流的工具类
 */
public class StreamUtil {

    /**
     * @param inputStream 输入流的数据
     * @return 把一个InputStream中的数据转换成一个字符串数据
     */
    public static String InputStreamToString(@NotNull InputStream inputStream) {
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        try {
            streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(streamReader);
            String line = null;
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
