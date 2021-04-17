package android.helper.ui.activity;

import android.helper.R;
import android.helper.databinding.ActivityDemoMapBinding;
import android.helper.ui.activity.otherutils.AudioPlayerActivity;
import android.view.View;

import com.android.helper.base.BaseTitleActivity;

public class DemoMapTitleActivity extends BaseTitleActivity {

    private ActivityDemoMapBinding binding;

    @Override
    protected void initView() {
        super.initView();
        binding = ActivityDemoMapBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
        super.initListener();

        setonClickListener(R.id.tv_live_data, R.id.tv_open_qywx, R.id.tv_receive_map_result,
                R.id.tv_xml_write_data, R.id.tv_rxjava2, R.id.tv_download, R.id.tv_uploading,
                R.id.tv_selector_image, R.id.tv_send_sms,R.id.tv_audio_player);
    }

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_demo_map;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_live_data:
                startActivity(LiveDataTitleActivity.class);
                break;

            case R.id.tv_open_qywx:
                startActivity(WorkWxTitleActivity.class);
                break;

            case R.id.tv_receive_map_result:
                startActivity(WorkWxTitleActivity.class);
                break;

            case R.id.tv_xml_write_data:
                startActivity(WriteXmlTitleActivity.class);
                break;
            case R.id.tv_rxjava2:
                startActivity(RxJava2Activity.class);
                break;

            case R.id.tv_download:
                startActivity(DownLoadListActivity.class);
                break;

            case R.id.tv_uploading:
                startActivity(UploadingActivity.class);
                break;

            case R.id.tv_selector_image:
                startActivity(SelectorImageActivity.class);
                break;

            case R.id.tv_send_sms:
                startActivity(SendSmsActivity.class);
                break;

            case R.id.tv_audio_player:
                startActivity(AudioPlayerActivity.class);
                break;
        }
    }

}