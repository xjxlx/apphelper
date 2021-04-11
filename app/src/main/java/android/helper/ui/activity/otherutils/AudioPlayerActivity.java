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
    private android.widget.SeekBar mSeekbar;
    private android.widget.TextView mTvLeft;
    private android.widget.TextView mTvRight;

    private AudioPlayerUtil playerUtil;
    private String url = "http://dlfile.buddyeng.cn/sv/48717030-177bd5eff7d/48717030-177bd5eff7d.mp3";

    private final AudioPlayerCallBackListener audioPlayerCallBackListener = new AudioPlayerCallBackListener() {
        @Override
        public void onPrepared() {
            LogUtil.e("onPrepared");
        }

        public void onStart() {
            LogUtil.e("onStart");
        }

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

        public void onComplete() {
            LogUtil.e("onComplete");
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
        mSeekbar = findViewById(R.id.seekbar);

        setonClickListener(mBtnPlayer, mBtnPause, mBtnStop);
        mTvLeft = findViewById(R.id.tv_left);
        mTvRight = findViewById(R.id.tv_right);
    }

    @Override
    protected void initData() {
        super.initData();
        playerUtil = new AudioPlayerUtil(mContext);
        playerUtil.bindService(success -> {
            if (success) {
                playerUtil.setSeekBar(mSeekbar);
                playerUtil.setAudioCallBackListener(audioPlayerCallBackListener);

                playerUtil.setSeekBarProgressTime(mTvLeft);
                playerUtil.setSeekBarTotalTime(mTvRight);
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
        LogUtil.e("onDestroy");
    }
}