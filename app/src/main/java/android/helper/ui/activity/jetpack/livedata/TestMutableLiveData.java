package android.helper.ui.activity.jetpack.livedata;

import androidx.lifecycle.MutableLiveData;

/**
 *
 */
public class TestMutableLiveData extends MutableLiveData<TestMutableLiveData> {

    private String name;
    private boolean isSix;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        postValue(this);
    }

    public boolean isSix() {
        return isSix;
    }

    public void setSix(boolean six) {
        isSix = six;
        postValue(this);
    }

    @Override
    public String toString() {
        return "TestMutableLiveData{" +
                "name='" + name + '\'' +
                ", isSix=" + isSix +
                '}';
    }
}
