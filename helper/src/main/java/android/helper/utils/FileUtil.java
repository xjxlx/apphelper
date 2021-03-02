package android.helper.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FileUtil {
    
    private final static String TAG = "FileUtil";
    
    public final static String CONTROL_VEHICLE_LOG_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "ZHGJ"
            + File.separator
            + "控制车辆"
            + File.separator;
    
    /**
     * @return 检测sd卡是否存在，如果存在就返回tru，否则就返回false
     */
    public static boolean checkoutSdExists() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
    /**
     * @return 获取保存到sd卡中的文件内容
     */
    public String getContentForSd(String filename) {
        String result = "";
        boolean b = checkoutSdExists();
        if (b) {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            if (file.exists()) {
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String timeLine;
                    while (true) {
                        try {
                            if ((timeLine = reader.readLine()) == null) {
                                break;
                            }
                            builder.append(timeLine);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    result = builder.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                LogUtil.e(TAG, "文件不存在");
            }
        } else {
            LogUtil.e(TAG, "SD卡不存在！");
        }
        return result;
    }
    
    /**
     * @param fileName 文件名字,例如：device.text
     * @param content  具体的内容 "123"
     * @return 保存到SD卡根目录
     */
    public static boolean PutContentToSd(String fileName, String content) {
        boolean isSaveSuccess = false;
        if ((!TextUtils.isEmpty(fileName)) && (!TextUtils.isEmpty(content))) {
            boolean b = checkoutSdExists();
            if (b) {
                // 获取sd卡的根目录
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                FileOutputStream outStream;
                try {
                    outStream = new FileOutputStream(file);
                    try {
                        outStream.write(content.getBytes());
                        isSaveSuccess = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        isSaveSuccess = false;
                    }
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                LogUtil.e(TAG, "SD卡不存在！");
            }
        } else {
            LogUtil.e(TAG, "数据为空！");
        }
        return isSaveSuccess;
    }
    
    /**
     * @return 获取Sd卡的路径，因为7.0之后SD卡的路径可能会被拒绝访问，所以分为两种不同的情况去获取
     * 1:7.0之上的方式：获取的路径为App内部的路径，会随着App的删除而被删除掉，具体路径为：/storage/emulated/0/Android/data/com.xjx.helper.debug/files/Download
     * 如果需要使用，则在mainfast.xml 中application下面加入：android:requestLegacyExternalStorage="true"
     * <p>
     * 2:7.0以下的方式：获取的是SD卡真实的路径，不会随着App的删除而被删除掉，具体路径为：/storage/emulated/0
     */
    public static String getSdPath() {
        String path = "";
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (externalStorageDirectory != null) {
            boolean exists = externalStorageDirectory.exists();
            if (exists) {
                path = externalStorageDirectory.getAbsolutePath();
            }
        }
        LogUtil.e(TAG, "获取的Sd卡根路径为：" + path);
        return path;
    }
    
    /**
     * @return 保存到App路径下面
     */
    public static boolean putContentToApp(Context context, String fileName, String content) {
        boolean isSaveSuccess = false;
        boolean b = checkoutSdExists();
        if (b) {
            // 返回共享存储的路径
            File externalFilesDir = context.getExternalFilesDir(null);
            if (externalFilesDir != null) {
                String absolutePath = externalFilesDir.getAbsolutePath();
                File file = new File(absolutePath, fileName);
                FileOutputStream outStream;
                try {
                    outStream = new FileOutputStream(file);
                    try {
                        outStream.write(content.getBytes());
                        isSaveSuccess = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        isSaveSuccess = false;
                    }
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                LogUtil.e(TAG, "获取共享存储对象失败");
            }
        } else {
            LogUtil.e(TAG, "Sd卡不可用");
        }
        return isSaveSuccess;
    }
    
    /**
     * @return 获取保存在App里面的内容
     */
    public static String getContentForApp(Context context, String filName) {
        String result = "";
        boolean b = checkoutSdExists();
        if (b) {
            File externalFilesDir = context.getExternalFilesDir(null);
            if (externalFilesDir != null) {
                File file = new File(externalFilesDir, filName);
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder tStringBuffer = new StringBuilder();
                    String sTempOneLine;
                    while (true) {
                        try {
                            if ((sTempOneLine = tBufferedReader.readLine()) == null) {
                                break;
                            }
                            tStringBuffer.append(sTempOneLine);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    result = tStringBuffer.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        } else {
            LogUtil.e(TAG, "Sd卡不可用");
        }
        return result;
    }
    
    /**
     * @return 获取sd卡的根目录的File
     */
    public static File getRootFileForSd() {
        return Environment.getExternalStorageDirectory();
    }
    
    /**
     * @param context context
     * @return 获取app目录下的根目录
     */
    public static File getRootFileForApp(Context context) {
        return context.getExternalFilesDir(null);
    }
    
    /**
     * @return 根据一个原始的路径，在同一个目录下面去生成一个新的文件名字，例如：/storage/emulated/0/a_pdf_list/test_2_abc.pdf，
     * 经过优化之后，会在/storage/emulated/0/a_pdf_list/目录下，生成另外一个文件/storage/emulated/0/a_pdf_list/test_2_abc(0).pdf
     * 文件是从角标0开始生成的。
     */
    public static String getPathForOriginalPath(String OriginalPath, long contentLength) {
        String path = OriginalPath;
        // 原始文件
        File file = new File(OriginalPath);
        // 如果本地不存在，就返回原始的路径
        if (file.exists()) {
            long length = file.length();
            // 只有本地的文件大小，大于等于文件的总大小的时候，才有必要去创建另一个路径
            if (length >= contentLength) {
                LogUtil.e(TAG, "本地文件和文件总大小一致，需要重新命名文件名字！");
                String newFileName = "";// 新文件的名字
                
                if (OriginalPath.contains(".")) {
                    // 最后一个.的index位置
                    int index = OriginalPath.lastIndexOf(".");
                    
                    // 获取前半部分的名字
                    String beginIndex = OriginalPath.substring(0, index);
                    // 获取.后面的文件格式
                    String endIndex = OriginalPath.substring(index);
                    
                    // 这里要去判断该文件夹下面，有没有已经命名过的文件了
                    File parentFile = file.getParentFile();
                    if (parentFile != null) {
                        File[] files = parentFile.listFiles();
                        // 便利文件夹下的子文件，找出和文件名字相同的文件
                        for (File childFile : files) {
                            if (childFile != null) {
                                String name = childFile.getAbsolutePath();
                                // 找到包含了前半段名字的文件
                                if (name.contains(beginIndex)) {
                                    // 包含了修改过文件的情况
                                    if ((name.contains("(")) && (name.contains(")"))) {
                                        LogUtil.e(TAG, "包含了修改过文件名字的对象，文件名字为：" + name);
                                        
                                        // 从角标0开始便利文件名字
                                        for (int i = 0; i < Long.MAX_VALUE; i++) {
                                            // 重新构建文件的名字
                                            newFileName = beginIndex + "(" + (i) + ")" + endIndex;
                                            File newFile = new File(newFileName);
                                            long newFileLength = newFile.length();
                                            
                                            // 如果新文件的大小小于等于0，或者新文件的大小小于文件的总大小，那么就找到了我们需要的文件,并且要停掉整个轮询
                                            if ((newFileLength <= 0) || (newFileLength < contentLength)) {
                                                LogUtil.e(TAG, "新生成的文件名字大小大于0，小于文件的总大小，这个文件就是当前需要的文件：newFileName:" + newFileName);
                                                break;
                                            }
                                        }
                                        LogUtil.e(TAG, "包含了修改过文件名字的对象，最后文件名字为：" + newFileName);
                                        // 只要进入到这里，就可以直接断掉整个循环了，因为会在这里从角标0，轮询文件数据
                                        break;
                                    } else {
                                        // 给出默认的新文件名字，但是这里不能直接断掉轮询，因为可能第一个文件就是原始的文件，避免文件错乱
                                        newFileName = beginIndex + "(0)" + endIndex;
                                        LogUtil.e(TAG, "不包含了修改过文件名字的对象，文件名字为：" + newFileName);
                                    }
                                }
                            }
                        }
                    }
                    LogUtil.e(TAG, "文件的新名字为：" + newFileName);
                    path = newFileName;
                } else {
                    throw new IllegalArgumentException("文件的名字中不包含。会导致下面的程序全部异常！");
                }
            }
        }
        return path;
    }
    
    /**
     * @param url 文件地址的url
     * @return 根据url 获取远程文件的大小
     */
    public static long getFileSizeForUrl(String url) {
        final long[] contentLength = {0};
        
        Request.Builder builder = new Request
                .Builder()
                .url(url);
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            
            }
            
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        contentLength[0] = body.contentLength();
                        response.close();
                    }
                }
            }
        });
        
        return contentLength[0];
    }
    
    /**
     * @param context context
     * @param uri     一个uri类型的字符串
     * @return 把一个uri类型的字符串转换成一个正常的真实路径，如果是一个文件路径或者本身就是一个真实路径，就会直接转换成一个url
     */
    public static String UriToPath(Context context, String uri) {
        String data = "";
        if (!TextUtils.isEmpty(uri)) {
            try {
                Uri parse = Uri.parse(uri);
                final String scheme = parse.getScheme();
                if (TextUtils.isEmpty(scheme)) {
                    return parse.getPath();
                } else if (TextUtils.equals(ContentResolver.SCHEME_FILE, scheme)) {
                    data = parse.getPath();
                } else if (TextUtils.equals(ContentResolver.SCHEME_CONTENT, scheme)) {
                    Cursor cursor = context.getContentResolver().query(parse, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    if (null != cursor) {
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            if (index > -1) {
                                return cursor.getString(index);
                            }
                        }
                        cursor.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("转换Uri失败！");
            }
        }
        return data;
    }
}
