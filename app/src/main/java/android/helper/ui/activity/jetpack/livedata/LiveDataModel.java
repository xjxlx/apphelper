package android.helper.ui.activity.jetpack.livedata;

import androidx.lifecycle.ViewModel;

public class LiveDataModel extends ViewModel {

    private TestLiveData testData = new TestLiveData();

    public TestLiveData getLiveData() {
        return testData;
    }

}
