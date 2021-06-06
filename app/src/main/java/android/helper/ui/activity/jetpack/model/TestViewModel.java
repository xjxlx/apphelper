package android.helper.ui.activity.jetpack.model;

import androidx.lifecycle.ViewModel;

import com.android.helper.utils.LogUtil;

public class TestViewModel extends ViewModel {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtil.e("数据被销毁了！");
    }

    @Override
    public String toString() {
        return "TestViewModel{" +
                "name='" + name + '\'' +
                '}';
    }
}
