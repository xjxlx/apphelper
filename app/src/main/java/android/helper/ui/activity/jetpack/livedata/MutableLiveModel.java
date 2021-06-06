package android.helper.ui.activity.jetpack.livedata;

import androidx.lifecycle.ViewModel;

public class MutableLiveModel extends ViewModel {
    private TestMutableLiveData mData = new TestMutableLiveData();

    public TestMutableLiveData getData() {
        return mData;
    }

}
