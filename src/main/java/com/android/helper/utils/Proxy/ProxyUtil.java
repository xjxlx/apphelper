package com.android.helper.utils.Proxy;

import com.android.common.utils.LogUtil;

import java.lang.reflect.Proxy;

/**
 * 动态代理的工具类
 */
public class ProxyUtil {

    private static Object mObject;
    private ProxyInterface mProxyInterface;

    public static void setObject(Object object) {
        ProxyUtil.mObject = object;
    }

    /**
     * @return 反射代理的工具类
     */
    public ProxyInterface getInstance() {
        if (mObject != null) {
            ProxyHandler proxyHandler = new ProxyHandler(mObject);
            try {
                // 此处只能返回接口，不能使用对象
                mProxyInterface = (ProxyInterface) Proxy.newProxyInstance(mObject.getClass().getClassLoader(),
                        mObject.getClass().getInterfaces(), proxyHandler);
            } catch (Exception e) {
                LogUtil.e("动态代理反射失败！");
            }
        }
        return mProxyInterface;
    }

}
