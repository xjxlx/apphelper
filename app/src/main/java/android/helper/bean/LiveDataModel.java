package android.helper.bean;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataModel extends ViewModel {
    
    private MutableLiveData<String> name;
    
    public MutableLiveData<String> getName() {
        if (name == null) {
            name = new MutableLiveData<String>();
        }
        return name;
    }
    
}
