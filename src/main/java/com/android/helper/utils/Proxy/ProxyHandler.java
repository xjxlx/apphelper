package com.android.helper.utils.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理对象的回调
 */
public class ProxyHandler implements InvocationHandler {
    /**
     * 具体执行的对象
     */
    private Object mObject;

    public ProxyHandler(Object object) {
        mObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = null;
        if (mObject != null) {
            // 具体对象的执行方法
            invoke = method.invoke(mObject, args);
        }
        return invoke;
    }
}
