package com.android.helper.app;

import androidx.fragment.app.FragmentActivity;

/**
 * @author : 流星
 * @CreateDate: 2021/12/25-11:46
 * @Description: Application 的通用接口
 */
public interface ApplicationInterface {
    /**
     * @return 获取全局公用的liveData
     */
    FragmentActivity getCommonLivedata();

    /**
     * 设置全局公用的liveData
     */
    void setCommonLiveData(FragmentActivity fragmentActivity);

}
