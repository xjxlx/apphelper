package com.android.helper.utils;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.common.utils.LogUtil;
import com.android.common.utils.ToastUtil;
import com.android.helper.app.BaseApplication;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 使用说明：
 * 1：因为要适配Android 11，所以文件的路径，如果不给予全部文件访问的权限，那么就尽量使用应用内部的存储目录，否则就容易发生文件读写异常。
 * 2：在检测Android 11权限的时候，调用的流程为：
 * <ol>
 *     1：检测文件的全部访问权限，调用方法为{@link FileUtil#checkAllFilesPermission(FragmentActivity)}
 *        <ul>
 *            注意：如果需要进行所有文件访问页面的跳转的话，这个方法一定要卸载onCreate里面进行检测，这个是为了数据回调的必须步骤，否则不会跳转。
 *        </ul>
 * <p>
 *     2：打开所有文件访问权限的跳转方法，{@link FileUtil#jumpAllFiles()}
 *     3：如果要在Android 11上面进行所有文件访问权限的使用，必须要注册三个权限，并进行权限动态申请
 *           <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
 *           <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *           <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 *     4:其他方法看使用说明文档进行使用
 * </ol>
 */
public class FileUtil implements BaseLifecycleObserver {

    private final static String TAG = "FileUtil";
    private static FileUtil INSTANCE;
    private FragmentActivity mActivity;
    private Fragment mFragment;
    private ActivityResultLauncher<Intent> mRegister;

    public static FileUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (FileUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FileUtil();
                }
            }
        }
        return INSTANCE;
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
                                                LogUtil.e(TAG,
                                                        "新生成的文件名字大小大于0，小于文件的总大小，这个文件就是当前需要的文件：newFileName:" + newFileName);
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
     * @return 获取sd卡的根目录的File，此目录在Android 11以后不可用
     */
    public File getRootFileForSd() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * @param url 文件地址的url
     * @return 根据url 获取远程文件的大小
     */
    public long getFileSizeForUrl(String url) {
        final long[] contentLength = {0};
        Request.Builder builder = new Request.Builder().url(url);
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
     * <ol>
     *     1：从内部存储空间访问，可以使用，从内部存储空间访问不需要任何权限，如果文件存储在内部存储空间中的目录内，则不能访问
     *     2：例子：/data/user/0/com.android.app/files
     * </ol>
     *
     * @return 获取App目录下的File目录下的路径，该路径可以在Android 11 上面任意使用
     */
    public String getAppFilesPath() {
        String path = "";
        Application application = BaseApplication.getInstance()
                .getApplication();
        if (application != null) {
            File filesDir = application.getFilesDir();
            if (filesDir != null) {
                path = filesDir.getPath();
            }
        }
        return path;
    }

    /**
     * <ol>
     *     1：例子：/storage/emulated/0/Android/data/com.android.app/files/Movies
     *     2：从外部存储空间访问，从外部存储空间访问不需要任何权限，如果文件存储在外部存储空间中的目录内，则可以访问
     * </ol>
     *
     * @param type 指定的类型，type The type of files directory to return. May be {@code null}
     *             for the root of the files directory or one of the following
     *             constants for a subdirectory:
     *             {@link android.os.Environment#DIRECTORY_MUSIC},
     *             {@link android.os.Environment#DIRECTORY_PODCASTS},
     *             {@link android.os.Environment#DIRECTORY_RINGTONES},
     *             {@link android.os.Environment#DIRECTORY_ALARMS},
     *             {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *             {@link android.os.Environment#DIRECTORY_PICTURES}, or
     *             {@link android.os.Environment#DIRECTORY_MOVIES}
     * @return 返回App目录下，files目录下的指定路径，该路径可以在Android 11 上面任意使用。
     */
    public String getAppTypePath(String type) {
        String path = "";
        if ((BaseApplication.getInstance()
                .getApplication() != null) && (!TextUtils.isEmpty(type))) {
            path = BaseApplication.getInstance()
                    .getApplication()
                    .getExternalFilesDir(type)
                    .getPath();
        }
        return path;
    }

    /**
     * @return true:设备sd卡正在挂载中，false：sd卡异常不可用
     */
    private boolean checkSdStatus() {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * <ol>
     *      例子：/storage/emulated/0/Download
     * </ol>
     *
     * @param type The type of storage directory to return. Should be one of
     *             {@link Environment#DIRECTORY_MUSIC},
     *             {@link Environment#DIRECTORY_PODCASTS},
     *             {@link Environment#DIRECTORY_RINGTONES},
     *             {@link Environment#DIRECTORY_ALARMS},
     *             {@link Environment#DIRECTORY_NOTIFICATIONS},
     *             {@link Environment#DIRECTORY_PICTURES},
     *             {@link Environment#DIRECTORY_MOVIES},
     *             {@link Environment#DIRECTORY_DOWNLOADS},
     *             {@link Environment#DIRECTORY_DCIM}, or
     *             {@link Environment#DIRECTORY_DOCUMENTS}.
     * @return 获取SD卡下，指定公共目录的路径，该路径在android 11及以后，只能通过IO流的形式使用，直接读写不可用
     */
    public String getSdTypePublicPath(String type) {
        String path = "";
        if ((!TextUtils.isEmpty(type)) && checkSdStatus()) {
            File publicDirectory = Environment.getExternalStoragePublicDirectory(type);
            if (publicDirectory != null) {
                path = publicDirectory.getPath();
            }
        }
        return path;
    }

    /**
     * <ol>
     *     1:如果需要使用，则在mainfast.xml 中application下面加入：android:requestLegacyExternalStorage="true"
     *     2:给予全部的访问权限
     * </ol>
     *
     * @return 获取SD卡下的根目录路径，在Android 11及以上，不能直接使用，除非给予文件所有的访问权限，如果需要使用，则需要满足上面的两个条件
     */
    public String getSdRootPath() {
        String path = "";
        if (checkSdStatus()) {
            File storageDirectory = Environment.getExternalStorageDirectory();
            if (storageDirectory != null) {
                path = storageDirectory.getPath();
            }
        }
        return path;
    }

    /**
     * @param file        指定的文件
     * @param inputStream 输入流
     * @return 把一个IO流的内容，写入指定的文件夹内，如果是Android 11的版本，无法直接写入到Sd卡的目录中，除非给予足够的权限
     */
    public boolean writeInputStreamToFile(File file, InputStream inputStream) {
        boolean isSuccess = false;
        if ((file != null) && (inputStream != null)) {
            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = new BufferedInputStream(inputStream);
                out = new BufferedOutputStream(new FileOutputStream(file));
                int len = -1;
                byte[] b = new byte[1024];
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                isSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    /**
     * @param file    指定的文件
     * @param content 存储的内容，例如："123"
     * @return 把指定的内容以文件的形式保存到指定的位置中
     */
    public boolean writeContentToFile(File file, String content) {
        boolean isSuccess = false;
        if ((file != null) && (!TextUtils.isEmpty(content)) && (checkSdStatus())) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                outStream.write(content.getBytes());
                isSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                try {
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    /**
     * @param file 指定的文件
     * @return 获取指定路径中文件的内容，把内容转换为String字符串，适用于单纯的文本内容
     */
    public String getContentForFile(File file) {
        String result = "";
        if ((file != null) && (file.exists())) {
            FileInputStream mInputStream = null;
            BufferedReader mReader = null;
            try {
                mInputStream = new FileInputStream(file);
                mReader = new BufferedReader(new InputStreamReader(mInputStream));
                StringBuilder builder = new StringBuilder();
                String timeLine;
                while ((timeLine = mReader.readLine()) != null) {
                    builder.append(timeLine);
                }
                result = builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (mInputStream != null) {
                        mInputStream.close();
                    }
                    if (mReader != null) {
                        mReader.close();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    /**
     * @return 检测是否拥有文件的所有访问权限
     */
    public boolean checkAllFilesPermission(FragmentActivity activity) {
        mActivity = activity;
        boolean checkout = commonCheckAllFile(activity);
        if (!checkout) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);
        }
        return checkout;
    }

    private boolean commonCheckAllFile(FragmentActivity activity) {
        boolean isPermission = false;
        if (activity != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                // 先判断有没有权限
//                isPermission = Environment.isExternalStorageManager();
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                // 7.0 判断读权限和写权限
//                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                        ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    isPermission = true;
//                } else {
//                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
//                }
//            } else {
//                // 其他版本，默认拥有权限
//                isPermission = true;
//            }
        }
        return isPermission;
    }

    /**
     * 在Android11的版本上，跳转到设置页面去设置所有文件的访问权限，如果要使用这个功能，则必须要满足三个权限要求：
     * <ol>
     *     <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
     *     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     *     <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * </ol>
     */
    public void jumpAllFiles() {
        if (mActivity != null) {
            //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Build.VERSION.SDK_INT >= 30) {
                if (mRegister != null) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
//                    mRegister.launch(intent);
                }
            } else {
                ToastUtil.show("当前系统无需开启所有文件访问权限！");
            }
        }
    }

    /**
     * 针对Android版本的变化，不推荐使用Sd卡的根目录，所以直接写入到App的存储空间中，
     * 1:首先获取App的外部存储目录，如果外部存储目录获取失败，就获取App的内部存储目录
     * 2:不管是外部存储路径还是内部存储路径，默认获取的是App目录下的下载文件路径
     */
    public String getCommonPath() {
        String path = "";
        path = getAppTypePath(Environment.DIRECTORY_DOWNLOADS);
        LogUtil.e("获取App外部存储路径为：" + path);
        if (TextUtils.isEmpty(path)) {
            String appFilesPath = getAppFilesPath();
            if (!TextUtils.isEmpty(appFilesPath)) {
                path = appFilesPath + "/Download";
            }
            LogUtil.e("获取App内部存储路径为：" + path);
        }
        return path;
    }

    /**
     * @return 如果可以正常获取路径的话，会返回一个指定Tag的目录
     */
    public String getCommonTagPath() {
        String path = "";
        path = getCommonPath();
        if (!TextUtils.isEmpty(path)) {
            if (BaseApplication.getInstance()
                    .logTag() != null) {
                path = path + "/" + BaseApplication.getInstance()
                        .logTag();
            }
        }
        LogUtil.e("获取App指定存储路径为：" + path);
        return path;
    }

    /**
     * @param path 指定路径
     * @return 创建文件夹是否成功，成功返回true,否则返回false
     */
    public boolean createFolder(String path) {
        boolean mkdirs = false;
        if (!TextUtils.isEmpty(path)) {
            File folder = new File(path);
            mkdirs = folder.exists();
            if (!mkdirs) {
                mkdirs = folder.mkdirs();
            }
        }
        return mkdirs;
    }

    public boolean createFolder(File file) {
        boolean mkdirs = false;
        if (file != null) {
            mkdirs = file.exists();
            if (!mkdirs) {
                mkdirs = file.mkdirs();
            }
        }
        return mkdirs;
    }

    public File createFile(String parentPath, String childName) {
        File file = null;
        if ((!TextUtils.isEmpty(parentPath)) && (!TextUtils.isEmpty(childName))) {
            // 创建父目录
            createFolder(parentPath);
            // 创建子文件
            File fileChild = new File(parentPath, childName);
            boolean exists = fileChild.exists();
            if (exists) {
                file = fileChild;
            } else {
                try {
                    boolean newFile = fileChild.createNewFile();
                    if (newFile) {
                        file = fileChild;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * @param file       原始的文件，例如：/storage/emulated/0/Android/data/com.android.app/files/Download/AppHelper/apk/test_2.apk
     * @param rules      指定的匹配规则的字符串，例如：。
     * @param fileLength 文件的总长度
     * @return 根据一个原始的文件，使用匹配规则去生成一个新的文件，常用于下载的时候，如果多次下载的时候，用于改变文件的名字，重新生成一个加（1）的路径，
     * 例如：/storage/emulated/0/Android/data/com.android.app/files/Download/AppHelper/apk/test_2(1).apk
     */
    public String copyFilePath(File file, String rules, long fileLength) {
        String newPath = "";
        int pathIndex = 1;// 叠加的数据
        if (file != null) {
            boolean exists = file.exists();
            if (exists) {
                String path = file.getPath();
                if (!TextUtils.isEmpty(path)) {
                    if (path.contains(".")) {
                        // 最后一个出现的.
                        int index = path.lastIndexOf(rules);
                        String left = path.substring(0, index);
                        String right = path.substring(index);
                        LogUtil.e(".最后出现的位置：" + index + "  left:" + left + "  right:" + right);
                        // 组成一个新的路径
                        newPath = left + "(" + pathIndex + ")" + right;
                        File parentFile = file.getParentFile();
                        if (parentFile != null) {
                            File[] files = parentFile.listFiles();
                            if (files != null) {
                                for (File childFile : files) {
                                    if (childFile != null) {
                                        boolean childExists = childFile.exists();
                                        if (childExists) {
                                            String childPath = childFile.getPath();
                                            // 跳过本身的文件
                                            if (!TextUtils.equals(childPath, file.getPath())) {
                                                if (TextUtils.equals(childPath, newPath)) {
                                                    // 获取单个文件的大小
                                                    long length = childFile.length();
                                                    // 如果单个文件的大小比总的文件大小要小，则跳过，避免多次重复性创建
                                                    if (length >= fileLength) {
                                                        // 便利文件夹下面，如果有和新文件路径相同的，则重新生成
                                                        newPath = left + "(" + ++pathIndex + ")" + right;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return newPath;
    }

    @Override
    public void onCreate() {
        if (mActivity != null) {
            mRegister = mActivity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            boolean allFilesPermission = checkAllFilesPermission(mActivity);
                            // ToastUtil.show("是否拥有了全部的权限 ：+" + allFilesPermission);
                            LogUtil.e("permission: -----> 是否拥有了全部的权限 :" + allFilesPermission);
                        }
                    });
        }
        if (mFragment != null) {
            mRegister = mFragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            boolean allFilesPermission = checkAllFilesPermission(mActivity);
                            // ToastUtil.show("是否拥有了全部的权限 ：+" + allFilesPermission);
                            LogUtil.e("permission: -----> 是否拥有了全部的权限 :" + allFilesPermission);
                        }
                    });
        }
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
        if (mRegister != null) {
            mRegister = null;
        }
        if (mActivity != null) {
            mActivity = null;
        }
        if (mFragment != null) {
            mFragment = null;
        }
        if (INSTANCE != null) {
            INSTANCE = null;
        }
    }
}
