package android.helper.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.helper.R;
import android.helper.databinding.ActivityFaceVideoPlayerBinding;
import android.helper.base.BaseTitleActivity;
import android.helper.utils.LogUtil;
import android.helper.utils.ToastUtil;

/**
 * 人脸识别的视频播放页面
 */
public class FaceVideoPlayerTitleActivity extends BaseTitleActivity {
    
    private ActivityFaceVideoPlayerBinding binding;
    private String videoWidth;
    private String videoHeight;
    private float mResultWidth;
    
    @Override
    protected int getTitleLayout() {
        return R.layout.activity_face_video_player;
    }
    
    @Override
    protected void initView() {
        super.initView();
        binding = ActivityFaceVideoPlayerBinding.inflate(getLayoutInflater());
    }
    
    @Override
    protected void initListener() {
        super.initListener();
        
        // 提交
        binding.tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
            }
        });
        
        // 冲洗录制
        binding.tvReRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 继续播放
        binding.ivPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean playing = binding.video.isPlaying();
                if (!playing) {
                    binding.video.resume();
                    // 控件不可见
                    binding.ivPlayer.setVisibility(View.GONE);
                } else {
                    ToastUtil.show("视频播放中，请稍后！");
                }
            }
        });
    }
    
    @Override
    protected void initData() {
        super.initData();
        
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String path = intent.getStringExtra("videoPath");
        
        if (TextUtils.isEmpty(path)) {
            ToastUtil.show("获取的视频路径为空！");
            return;
        }
        
        // 获取视频的参数
        getVideoParameter(path);
        
        // 重新设置视频的宽高
        changeVideoSize();
        
        binding.video.setVideoPath(path);//设置视频文件
        binding.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //视频加载完成,准备好播放视频的回调
                mp.start();
            }
        });
        binding.video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //视频播放完成后的回调
                
                // 播放的按钮可见
                binding.ivPlayer.setVisibility(View.VISIBLE);
            }
        });
        binding.video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // 播放的按钮可见
                binding.ivPlayer.setVisibility(View.VISIBLE);
                //异常回调
                return false;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
            }
        });
        
        binding.video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //信息回调
//                what 对应返回的值如下
//                public static final int MEDIA_INFO_UNKNOWN = 1;  媒体信息未知
//                public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700; 媒体信息视频跟踪滞后
//                public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3; 媒体信息\视频渲染\开始
//                public static final int MEDIA_INFO_BUFFERING_START = 701; 媒体信息缓冲启动
//                public static final int MEDIA_INFO_BUFFERING_END = 702; 媒体信息缓冲结束
//                public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703; 媒体信息网络带宽（703）
//                public static final int MEDIA_INFO_BAD_INTERLEAVING = 800; 媒体-信息-坏-交错
//                public static final int MEDIA_INFO_NOT_SEEKABLE = 801; 媒体信息找不到
//                public static final int MEDIA_INFO_METADATA_UPDATE = 802; 媒体信息元数据更新
//                public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901; 媒体信息不支持字幕
//                public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902; 媒体信息字幕超时
                return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
            }
        });
    }
    
    private void getVideoParameter(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        //宽
        videoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        //高
        videoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        Bitmap frameAtTime = retriever.getFrameAtTime();
        LogUtil.e("videoWidth:" + videoWidth + "  videoHeight: " + videoHeight);
    }
    
    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    public void changeVideoSize() {
        int width = binding.video.getWidth();
        int height = binding.video.getHeight();
        
        LogUtil.e("s-width:" + width + "  s-height: " + height);
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = getResources().getDisplayMetrics().heightPixels;
        
        LogUtil.e("s-width:" + width + "  s-height: " + height + "  p:w:" + deviceWidth + "  p:h:" + deviceHeight);
        
        float devicePercent = 0; // 获取比值
        //下面进行求屏幕比例,因为横竖屏会改变屏幕宽度值,所以为了保持更小的值除更大的值.
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
            devicePercent = (float) deviceWidth / (float) deviceHeight; //竖屏状态下宽度小与高度,求比
        }
        
        if ((!TextUtils.isEmpty(videoWidth)) && (!TextUtils.isEmpty(videoHeight))) {
            float parseVideoWidth = Float.parseFloat(videoWidth);
            float parseVideoHeight = Float.parseFloat(videoHeight);
            
            float videoPercent = (float) parseVideoWidth / (float) parseVideoHeight;//求视频比例 注意是宽除高 与 上面的devicePercent 保持一致
            
            LogUtil.e("devicePercent:" + devicePercent + "  videoPercent:" + videoPercent);
            
            float differenceValue = Math.abs(videoPercent - devicePercent);//相减求绝对值
            if (differenceValue < 0.3) { //如果小于0.3比例,那么就放弃按比例计算宽度直接使用屏幕宽度
                mResultWidth = deviceWidth;
            } else {
                mResultWidth = (int) (parseVideoWidth / devicePercent);//注意这里是用视频宽度来除
            }
            mResultWidth = (int) (parseVideoWidth / devicePercent);//注意这里是用视频宽度来除
        }
        
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.video.getLayoutParams();
        layoutParams.width = (int) mResultWidth;
        layoutParams.verticalBias = 0.5f;
        layoutParams.horizontalBias = 0.5f;
        binding.video.setLayoutParams(layoutParams);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.video.stopPlayback();//停止播放视频,并且释放
        binding.video.suspend();//在任何状态下释放媒体播放器
    }
    
}