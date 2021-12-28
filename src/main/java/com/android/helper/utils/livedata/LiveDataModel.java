package com.android.helper.utils.livedata;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

/**
 * @author : 流星
 * @CreateDate: 2021/12/28-3:04 下午
 * @Description:
 */
public class LiveDataModel extends AndroidViewModel {

    private final MutableLiveData<LiveDataMessage> liveData = new MutableLiveData<LiveDataMessage>();

    public LiveDataModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public MutableLiveData<LiveDataMessage> getLiveData() {
        return liveData;
    }

    /**
     * 设置数据
     */
    public void setLiveData(LiveDataMessage message) {
        if (message != null) {
            liveData.postValue(message);
        }
    }
}
