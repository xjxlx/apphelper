package android.helper.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * xml的数据写入控制局
 */
public class XmlUtil {
    
    /**
     * 批量写入数据到文本中
     *
     * @param filePath 写入数据的文本地址
     */
    public void writeDat(String filePath, String start, String middle, String end, List<Float> list) {
        
        FileOutputStream outputStream = null;
        
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            
            if (list == null || list.size() <= 0) {
                ToastUtil.show("写入长度异常，无法写入！");
                return;
            }
            
            try {
                /*
                 * 往文件<.txt>中一行一行的写入数据
                 * append 为true表示追加内容，false表示覆盖内容
                 */
                outputStream = new FileOutputStream(file);
                // 往文件中写入10行数据进行测试
                for (int i = 0; i < list.size(); i++) {
                    // \n  表示换行
                    Float value = list.get(i);
                    String decimal = NumberUtil.FloatToZeros(value);
                    String data = start + decimal + middle + decimal + end;
                    try {
                        outputStream.write(data.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                ToastUtil.show("数据写入完毕！");
            }
        }
    }
}
