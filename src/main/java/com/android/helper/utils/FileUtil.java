package com.android.helper.utils;

import android.app.Application;
import android.content.Intent;
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
import com.android.helper.app.BaseApplication;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 使用说明： 1：因为要适配Android 11，所以文件的路径，如果不给予全部文件访问的权限，那么就尽量使用应用内部的存储目录，否则就容易发生文件读写异常。 2：在检测Android
 * 11权限的时候，调用的流程为：
 *
 * <ol>
 *   1：检测文件的全部访问权限，调用方法为{@link FileUtil#checkAllFilesPermission(FragmentActivity)}
 *   <ul>
 *     注意：如果需要进行所有文件访问页面的跳转的话，这个方法一定要卸载onCreate里面进行检测，这个是为了数据回调的必须步骤，否则不会跳转。
 *   </ul>
 *   <p>
 * </ol>
 */
public class FileUtil implements BaseLifecycleObserver {

  private static final String TAG = "FileUtil";
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
   *
   *
   * <ol>
   *   1：从内部存储空间访问，可以使用，从内部存储空间访问不需要任何权限，如果文件存储在内部存储空间中的目录内，则不能访问
   *   2：例子：/data/user/0/com.android.app/files
   * </ol>
   *
   * @return 获取App目录下的File目录下的路径，该路径可以在Android 11 上面任意使用
   */
  public String getAppFilesPath() {
    String path = "";
    Application application = BaseApplication.getInstance().getApplication();
    if (application != null) {
      File filesDir = application.getFilesDir();
      if (filesDir != null) {
        path = filesDir.getPath();
      }
    }
    return path;
  }

  /**
   *
   *
   * <ol>
   *   1：例子：/storage/emulated/0/Android/data/com.android.app/files/Movies
   *   2：从外部存储空间访问，从外部存储空间访问不需要任何权限，如果文件存储在外部存储空间中的目录内，则可以访问
   * </ol>
   *
   * @param type 指定的类型，type The type of files directory to return. May be {@code null} for the root
   *     of the files directory or one of the following constants for a subdirectory: {@link
   *     android.os.Environment#DIRECTORY_MUSIC}, {@link android.os.Environment#DIRECTORY_PODCASTS},
   *     {@link android.os.Environment#DIRECTORY_RINGTONES}, {@link
   *     android.os.Environment#DIRECTORY_ALARMS}, {@link
   *     android.os.Environment#DIRECTORY_NOTIFICATIONS}, {@link
   *     android.os.Environment#DIRECTORY_PICTURES}, or {@link
   *     android.os.Environment#DIRECTORY_MOVIES}
   * @return 返回App目录下，files目录下的指定路径，该路径可以在Android 11 上面任意使用。
   */
  public String getAppTypePath(String type) {
    String path = "";
    if ((BaseApplication.getInstance().getApplication() != null) && (!TextUtils.isEmpty(type))) {
      path = BaseApplication.getInstance().getApplication().getExternalFilesDir(type).getPath();
    }
    return path;
  }

  /**
   * @return true:设备sd卡正在挂载中，false：sd卡异常不可用
   */
  private boolean checkSdStatus() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  /**
   *
   *
   * <ol>
   *   例子：/storage/emulated/0/Download
   * </ol>
   *
   * @param type The type of storage directory to return. Should be one of {@link
   *     Environment#DIRECTORY_MUSIC}, {@link Environment#DIRECTORY_PODCASTS}, {@link
   *     Environment#DIRECTORY_RINGTONES}, {@link Environment#DIRECTORY_ALARMS}, {@link
   *     Environment#DIRECTORY_NOTIFICATIONS}, {@link Environment#DIRECTORY_PICTURES}, {@link
   *     Environment#DIRECTORY_MOVIES}, {@link Environment#DIRECTORY_DOWNLOADS}, {@link
   *     Environment#DIRECTORY_DCIM}, or {@link Environment#DIRECTORY_DOCUMENTS}.
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
   * @param file 指定的文件
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
      //                if (ActivityCompat.checkSelfPermission(activity,
      // Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
      //                        ContextCompat.checkSelfPermission(activity,
      // Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      //                    isPermission = true;
      //                } else {
      //                    ActivityCompat.requestPermissions(activity, new
      // String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
      // Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
      //                }
      //            } else {
      //                // 其他版本，默认拥有权限
      //                isPermission = true;
      //            }
    }
    return isPermission;
  }

  /**
   * 针对Android版本的变化，不推荐使用Sd卡的根目录，所以直接写入到App的存储空间中， 1:首先获取App的外部存储目录，如果外部存储目录获取失败，就获取App的内部存储目录
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

  @Override
  public void onCreate() {
    if (mActivity != null) {
      mRegister =
          mActivity.registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
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
      mRegister =
          mFragment.registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
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
  public void onStart() {}

  @Override
  public void onResume() {}

  @Override
  public void onPause() {}

  @Override
  public void onStop() {}

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
