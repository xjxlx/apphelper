package android.helper.ui.activity.jetpack.livedata;

import android.annotation.SuppressLint;
import android.helper.R;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.FragmentUtil;
import com.android.helper.utils.LogUtil;

/**
 * Mutable: /ˈmjuːtəbl/  缪特保
 * 使用好处：
 * 1:在需要观察的界面使用viewModel的对象去观察数据源的变化，可以一个地方设置，所有地方公用，已经离不开这种模式了，
 * 只要一个地方设置了数据，其他地方只有监听对象，就能立马更新数据，可以做到数据的实时同步。
 * 2：liveData 的内部方法onChanged,只有在页面可见的时候发生作用，避免了一些子线程或者页面不可见时候更新界面导致的
 * 崩溃，减少了很多代码的检查操作。
 * <p>
 * 逻辑：
 * <p>
 * 测试：
 */
public class LiveDataActivity extends BaseTitleActivity {

    private LiveDataModel mLiveDataModel;
    private TextView mTvLiveDateContent;
    private TextView mMTvHint;
    private MutableLiveModel mMutableLiveModel;
    private TextView mTvMutableLiveDateContent;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_live_data;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("LiveData");

        mTvLiveDateContent = findViewById(R.id.tv_live_date_content);
        mTvMutableLiveDateContent = findViewById(R.id.tv_mutable_live_date_content);
        mMTvHint = findViewById(R.id.tv_hint);
    }

    @Override
    protected void initListener() {
        super.initListener();
        setonClickListener(R.id.bt_live_data, R.id.bt_mutable_live_data);
    }

    @Override
    protected void initData() {
        super.initData();

        mMTvHint.setText("使用好处：\n" +
                "1:在需要观察的界面使用viewModel的对象去观察数据源的变化，可以一个地方设置，所有地方公用，已经离不开这种模式了，" +
                "只要一个地方设置了数据，其他地方只有监听对象，就能立马更新数据，可以做到数据的实时同步。" + "\r\n" +
                "2：liveData 的内部方法onChanged,只有在页面可见的时候发生作用，避免了一些子线程或者页面不可见时候更新界面导致的" +
                "崩溃，减少了很多代码的检查操作。");

        testLiveData();
        testMutableLiveData();

        FragmentUtil fragmentUtil = new FragmentUtil(mContext);
        fragmentUtil.add(R.id.fl_live_data_1, LiveData1Fragment.newInstance(), "", (successful, tag, o) -> {
        });
        fragmentUtil.add(R.id.fl_live_data_2, LiveData2Fragment.newInstance(), "", (successful, tag, o) -> {
        });
    }

    private void testLiveData() {
        // liveData的测试
        mLiveDataModel = ViewModelProviders.of(mContext).get(LiveDataModel.class);
        mLiveDataModel.getLiveData().observe(this, new Observer<TestLiveData>() {
            @Override
            public void onChanged(TestLiveData testLiveData) {
                int age = testLiveData.getAge();
                String name = testLiveData.getName();
                LogUtil.e("name:" + name + "  age:" + age);
                mTvLiveDateContent.setText(name);
            }
        });
    }

    private void testMutableLiveData() {
        mMutableLiveModel = ViewModelProviders.of(this).get(MutableLiveModel.class);
        mMutableLiveModel.getData().observe(this, new Observer<TestMutableLiveData>() {
            @Override
            public void onChanged(TestMutableLiveData testMutableLiveData) {
                LogUtil.e(getTag(), testMutableLiveData.toString());
                mTvMutableLiveDateContent.setText(testMutableLiveData.getName());
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_live_data:
                String anotherName = "John Doe";
                mLiveDataModel.getLiveData().setName(anotherName);
                break;

            case R.id.bt_mutable_live_data:
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mMutableLiveModel.getData().setName("赵六");
                    }
                };
                thread.start();

                break;
        }
    }

}