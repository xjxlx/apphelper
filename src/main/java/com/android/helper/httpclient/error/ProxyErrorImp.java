package com.android.helper.httpclient.error;

/**
 * @author dasouche
 * @CreateDate: 2021/9/24-4:21 下午
 * @Description: 动态代理的实现类，作为中间人使用
 */
public class ProxyErrorImp implements ProxyErrorListener {
    private ProxyErrorListener mListener;

    private static volatile ProxyErrorImp INSTANCE;

    /**
     * @author dasouche
     * @CreateDate: 2021/9/24
     * @Description: 获取单利的对象去使用
     */
    public static ProxyErrorImp getInstance() {
        if (INSTANCE == null) {
            synchronized (ProxyErrorImp.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProxyErrorImp();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param listener 设置具体的代理对象
     */
    public void setObject(ProxyErrorListener listener) {
        this.mListener = listener;
    }

    @Override
    public void errorCallBack(Throwable throwable) {
        // 调用具体回调对象的方法
        if (mListener != null) {
            mListener.errorCallBack(throwable);
        }
    }
}
