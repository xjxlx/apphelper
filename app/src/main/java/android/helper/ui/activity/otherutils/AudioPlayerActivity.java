package android.helper.ui.activity.otherutils;

import android.helper.R;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.LogUtil;
import android.helper.utils.media.audio.AudioPlayerCallBackListener;
import android.helper.utils.media.audio.AudioPlayerUtil;
import android.view.View;

/**
 * 音频播放工具类
 */
public class AudioPlayerActivity extends BaseTitleActivity {

    private android.widget.Button mBtnPlayer;
    private android.widget.Button mBtnPause;
    private android.widget.Button mBtnStop;
    private AudioPlayerUtil playerUtil;
    private String url = "http://dlfile.buddyeng.cn/sv/48717030-177bd5eff7d/48717030-177bd5eff7d.mp3";

    private final AudioPlayerCallBackListener audioPlayerCallBackListener = new AudioPlayerCallBackListener() {
        @Override
        public void onPrepared() {
            LogUtil.e("onPrepared");
        }

        @Override
        public void onStart() {
            LogUtil.e("onStart");
        }

        @Override
        public void onPause() {
            LogUtil.e("onPause");
        }

        @Override
        public void onStop() {
            LogUtil.e("onStop");
        }

        @Override
        public void onError(Exception e) {
            LogUtil.e("onError：" + e.getMessage());
        }

        @Override
        public void onComplete() {
            LogUtil.e("onComplete");
        }

        @Override
        public void onBufferProgress(int total, double current, int percent) {
            LogUtil.e("onBufferProgress:-->total:" + total + "  --->current:" + current + " --->percent:" + percent);
        }

        @Override
        public void onProgress(int total, int current, String percent) {
            LogUtil.e("onProgress:-->total:" + total + "  --->current:" + current + " --->percent:" + percent);

        }
    };

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_audio_player;
    }

    @Override
    protected void initView() {
        super.initView();

        setTitleContent("音乐播放器");

        mBtnPlayer = findViewById(R.id.btn_player);
        mBtnPause = findViewById(R.id.btn_pause);
        mBtnStop = findViewById(R.id.btn_stop);

        setonClickListener(mBtnPlayer, mBtnPause, mBtnStop);
    }

    @Override
    protected void initData() {
        super.initData();
        playerUtil = new AudioPlayerUtil(mContext);
        playerUtil.bindService(success -> {
            if (success) {
                playerUtil.setAutoPlayer(true);
                playerUtil.setAudioCallBackListener(audioPlayerCallBackListener);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_player:

                playerUtil.setResource(url);

                break;

            case R.id.btn_pause:
                playerUtil.pause();
                break;

            case R.id.btn_stop:
                playerUtil.stop();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerUtil.destroy();
    }
}