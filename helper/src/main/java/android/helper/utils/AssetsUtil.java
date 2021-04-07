package android.helper.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.helper.interfaces.listener.CallBackListener;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsUtil {

    public static AssetsUtil assetsUtil;
    private Thread thread;

    private AssetsUtil() {
    }

    /**
     * @return 返回一个单利的对象
     */
    public static AssetsUtil getInstance() {
        if (assetsUtil == null) {
            assetsUtil = new AssetsUtil();
        }
        return assetsUtil;
    }

    /**
     * 异步线程获取数据
     */
    public void initJson(Context context, String fileName, CallBackListener<String> callBackListener) {
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    getJsonForAssets(context, fileName, callBackListener);
                }
            };
        }
        thread.start();
    }

    /**
     * @param context  上下文
     * @param fileName assets中的文件名字,全文见路径，例如：address.json
     */
    public void getJsonForAssets(Context context, String fileName, CallBackListener<String> callBackListener) {
        String result = "";  // 数据的结果

        if ((context != null) && (!TextUtils.isEmpty(fileName))) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                AssetManager assetManager = context.getAssets();
                BufferedReader bf = new BufferedReader(new InputStreamReader(
                        assetManager.open(fileName)));
                String line;
                while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();

                if (callBackListener != null) {
                    callBackListener.onBack(true, "数据获取成功", result);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (callBackListener != null) {
                    callBackListener.onBack(false, e.getMessage(), result);
                }
            }
        } else {
            if (callBackListener != null) {
                callBackListener.onBack(false, "对象为空", result);
            }
        }
    }

    /**
     * @param fileName 例如：c62.crt
     * @return 获取资源目录下的文件，返回一个字节数组
     */
    public static byte[] getAssetsResource(Context context, String fileName) {
        byte[] bytes = null;
        AssetManager assets = context.getAssets();
        try {
            InputStream inputStream = assets.open(fileName);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            bytes = output.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // assets.close();
        }
        return bytes;
    }

    public void clear() {
        if (thread != null) {
            thread = null;
        }
    }
}
