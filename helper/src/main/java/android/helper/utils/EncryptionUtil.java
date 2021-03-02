package android.helper.utils;

import android.util.Base64;

/**
 * 加密解密工具
 */
public class EncryptionUtil {
    
    /**
     * @param content 加密内容
     * @return 对数据进行Base64加密
     */
    public static String enCodeBase64(String content) {
        // Base64.NO_WRAP 不换行
        return Base64.encodeToString(content.getBytes(), Base64.NO_WRAP);
    }
    
    /**
     * @param content 解密内容
     * @return Base64解密
     */
    public static String decodeBase64(String content) {
        // Base64.NO_WRAP 不换行
        return new String(Base64.decode(content, Base64.NO_WRAP));
    }
    
}
