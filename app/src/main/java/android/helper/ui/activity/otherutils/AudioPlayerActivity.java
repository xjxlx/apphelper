package android.helper.ui.activity.otherutils;

import android.annotation.SuppressLint;
import android.helper.R;
import android.helper.ui.activity.java.JavaMapActivity;
import android.view.View;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.media.audio.AudioConstant;
import com.android.helper.utils.media.audio.AudioPlayerCallBackListener;
import com.android.helper.utils.media.audio.AudioPlayerUtil;

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
    private android.widget.ImageView mIvStart;

    private AudioPlayerUtil playerUtil;
    private String url = "http://dlfile.buddyeng.cn/sv/316606-177bd6adfb0/316606-177bd6adfb0.mp3";

    private final AudioPlayerCallBackListener audioPlayerCallBackListener = new AudioPlayerCallBackListener() {
        public void onComplete() {
            playerUtil.setResource(url);
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
        mIvStart = findViewById(R.id.iv_start);
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.e(AudioConstant.TAG, "initData");

        playerUtil = new AudioPlayerUtil(mContext);
        playerUtil.bindService(success -> {
            playerUtil.autoPlayer(false);
            playerUtil.setSeekBar(mSeekbar);
            playerUtil.setSeekBarProgressTime(mTvLeft);
            playerUtil.setSeekBarTotalTime(mTvRight);
            playerUtil.setStartButton(mIvStart);
            playerUtil.setPendingIntentActivity(JavaMapActivity.class);
            playerUtil.setNotificationIcon(R.drawable.icon_music_start, R.drawable.icon_music_pause, R.drawable.icon_music_left, R.drawable.icon_music_right);
            playerUtil.setAudioCallBackListener(audioPlayerCallBackListener);
        });
    }

    @SuppressLint("NonConstantResourceId")
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
        LogUtil.e(AudioConstant.TAG, "onDestroy");

        playerUtil.destroy();
    }
}