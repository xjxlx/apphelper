package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.text.TextUtils;

import com.android.helper.app.BaseApplication;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日志写入工具
 * <p>
 * 使用的时候不能重复性的去调用
 */
public class LogWriteUtil {

    private static final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";//日期格式
    private static File mParentFile; //公用的父类文件夹，使用当天的信息作为文件夹的名字
    private static String mParentPath; // 父类文件夹的名字
    private int mNumber = 0;// 每一行的编号

    private final static String WRITE_LOG_FILE_PARENT =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + BaseApplication.getLogTag()  // 写入文件的目标路径
                    + File.separator;

    public LogWriteUtil() {
        // 创建公共的父类文件夹
        createCommonParenFile();
    }

    /**
     * 获取当前时间
     */
    @SuppressLint("SimpleDateFormat")
    private static String getCurrentDateStr(String pattern) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    // 文件读操作函数
    public List<String> read(String fileName) {
        List sb = new ArrayList();
        File file = new File(mParentFile, fileName + ".txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                if (!TextUtils.isEmpty(tempString)) {
                    sb.add(tempString + "\n");
                }
            }
            return sb;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    PrintStream printStream;
    boolean mIsFirstWrite;// 是否首次写入收据

    public void write(@NotNull String fileName, String content) {
        String value; // 写入的具体数据

        // 创建自己的文件
        File file = checkFile(fileName);
        if (file != null && file.exists()) {
            try {
                // 打印流对象用于输出
                printStream = new PrintStream(new FileOutputStream(file, true)); // 追加文件

                // 获取当前的时间
                String currentDateStr = getCurrentDateStr(DATE_PATTERN_FULL);
                if (!mIsFirstWrite) {
                    value = "-----------------   " + currentDateStr + " 重新开始   -----------------" + "\n";
                    mIsFirstWrite = true;
                    printStream.println(value);
                }
                value = "[ " + currentDateStr + " ] " + fileName + ": " + (++mNumber + " ") + content + "\n";

                printStream.println(value);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (printStream != null) {
                    printStream.close(); // 关闭打印流
                }
            }
        }
    }

    /**
     * 创建一个公用的父类文件夹
     */
    private static void createCommonParenFile() {
        // sd卡根目录
        File file = new File(WRITE_LOG_FILE_PARENT);
        boolean exists = file.exists();
        if (!exists) {
            boolean mkdirs = file.mkdirs();
        }
        if (file.exists()) {
            // 获取当前天的路径
            if (TextUtils.isEmpty(mParentPath)) {
                Calendar calendar = Calendar.getInstance();
                //当前年
                int year = calendar.get(Calendar.YEAR);
                //当前月
                int month = (calendar.get(Calendar.MONTH)) + 1;
                //当前月的第几天：即当前日
                int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);

                mParentPath = file.getAbsolutePath()
                        + File.separator + year + "年"
                        + File.separator + (month) + "月"
                        + File.separator + day_of_month + "日";
            }

            // 根据文件名字去创建父类文件夹
            if (!TextUtils.isEmpty(mParentPath)) {
                File parentFile = new File(mParentPath);
                if (!parentFile.exists()) {
                    // 创建父类公用的文件夹
                    boolean mkdirs = parentFile.mkdirs();
                    if (mkdirs) {
                        mParentFile = parentFile;
                    }
                } else {
                    mParentFile = parentFile;
                }
            }
        }
    }

    /**
     * @param fileName 文件名字
     * @return 检测是否创建了文件夹
     */
    private File checkFile(@NotNull String fileName) {
        if (mParentFile != null && mParentFile.exists()) {
            File file = new File(mParentFile, (fileName + ".txt"));
            if (!file.exists()) {
                try {
                    // 创建一个新的文件
                    boolean newFile = file.createNewFile();
                    if (newFile) {
                        return file;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return file;
            }
        }
        return null;
    }

}
