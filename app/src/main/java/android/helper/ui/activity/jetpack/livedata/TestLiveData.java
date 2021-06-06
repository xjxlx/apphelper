package android.helper.ui.activity.jetpack.livedata;

import androidx.lifecycle.LiveData;

public class TestLiveData extends LiveData<TestLiveData> {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        postValue(this);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        postValue(this);
    }

    @Override
    public String toString() {
        return "TestLiveData{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
