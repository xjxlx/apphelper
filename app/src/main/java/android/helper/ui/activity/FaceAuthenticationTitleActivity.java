package android.helper.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.core.content.ContextCompat;

import android.helper.R;
import android.helper.databinding.ActivityFaceAuthenticationBinding;
import android.helper.widget.GradientProgressBar;
import android.helper.base.BaseTitleActivity;
import android.helper.interfaces.listener.SinglePermissionsListener;
import android.helper.utils.FileUtil;
import android.helper.utils.LogUtil;
import android.helper.utils.RxPermissionsUtil;
import android.helper.utils.TextViewUtil;
import android.helper.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 补充信息---人脸认证的界面
 */
public class FaceAuthenticationTitleActivity extends BaseTitleActivity {
    
    private ActivityFaceAuthenticationBinding binding;
    private MediaRecorder mediaRecorder;
    private File mOutFile;//保存mp4的路径文件
    private boolean isRecording; // 是否在录制中
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private boolean mSurfaceCreated = false;
    private boolean isPermission;
    
    public final static String FACE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "ZHGJ"
            + File.separator
            + "活体认证";
    private FileUtil fileUtil;
    private Camera.Size sizeForVideo;
    
    @Override
    protected int getTitleLayout() {
        return 0;
    }
    
    @Override
    protected void initView() {
        binding = ActivityFaceAuthenticationBinding.inflate(getLayoutInflater());
        TextViewUtil.setTextFont(mContext, binding.tvCode1, "DINCondensedBold.ttf");
        TextViewUtil.setTextFont(mContext, binding.tvCode2, "DINCondensedBold.ttf");
        TextViewUtil.setTextFont(mContext, binding.tvCode3, "DINCondensedBold.ttf");
        TextViewUtil.setTextFont(mContext, binding.tvCode4, "DINCondensedBold.ttf");
        
        binding.tvCountNumber.setContent("3");
        binding.tvCountNumber.setFont(mContext, "DINCondensedBold.ttf");
        binding.tvCountNumber.setColors(new int[]{ContextCompat.getColor(mContext, R.color.purple_3), ContextCompat.getColor(mContext, R.color.purple_4)});
        binding.tvCountNumber.setPositions(new float[]{0f, 1f});
        binding.tvCountNumber.setVisibility(View.GONE);
    }
    
    @Override
    protected void initListener() {
        super.initListener();
        
        // 进度条按下的监听
        binding.progress.setProgressTouchListener(new GradientProgressBar.ProgressTouchListener() {
            @Override
            public void onDown() {
                LogUtil.e("手指按下了！");
                binding.tvCountNumber.setVisibility(View.VISIBLE);
                initMediaRecorder();
            }
            
            @Override
            public void onUp() {
                binding.tvCountNumber.setVisibility(View.GONE);
                LogUtil.e("手指抬起了！");
                // 停止录制视频
                stopRecorder();
            }
            
            @Override
            public void onValueChange(Object value) {
                LogUtil.e("手指按下了，数据变化中！");
                binding.tvCountNumber.setContent(String.valueOf(value));
            }
            
            @Override
            public void onCountdownFinish() {
                binding.tvCountNumber.setVisibility(View.GONE);
                LogUtil.e("手指按下了，倒计时结束了！");
                // 开始录制视频
                startRecorder();
            }
            
            @Override
            public void onFinish() {
                LogUtil.e("手指按下了，onFinish！");
                // 停止录制视频
                stopRecorder();
            }
        });
        
        binding.btnStart.setOnClickListener(v -> startRecorder());
        binding.btnStop.setOnClickListener(v -> stopRecorder());
    }
    
    @Override
    protected void initData() {
        // 检测是否有摄像头
        boolean hardware = checkCameraHardware(mContext);
        if (!hardware) {
            ToastUtil.show("您的设备没有摄像头，暂时无法使用");
            return;
        }
        
        fileUtil = new FileUtil();
        boolean exists = fileUtil.checkoutSdExists();
        if (!exists) {
            ToastUtil.show("当前您的设备没有内存卡，无法使用");
            return;
        }
        
        // 检测权限
        new RxPermissionsUtil(mContext,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).setSinglePermissionListener(new SinglePermissionsListener() {
            @Override
            public void onRxPermissions(boolean havePermission) {
                isPermission = havePermission;
                if (!isPermission) {
                    binding.progress.setCanTouch(false);
                } else {
                    getVideoFile();
                    // 初始化摄像头
                    initCamera();
                }
            }
        });
        
        // 初始化surfaceView的对象
        initSurfaceView();
    }
    
    private void initSurfaceView() {
        // 设置分辨率
        surfaceHolder = binding.sfv.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                LogUtil.e("----->surfaceCreated！");
                mSurfaceCreated = true;
            }
            
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                LogUtil.e("----->surfaceChanged！");
            }
            
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                LogUtil.e("----->surfaceDestroyed！");
                mSurfaceCreated = false;
                // 清空摄像机
                if (mCamera != null) {
                    try {
                        holder.removeCallback(this);
                        releaseCamera();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 设置该组件让屏幕不会自动关闭
        surfaceHolder.setKeepScreenOn(true);
    }
    
    private void initCamera() {
        // 必须拥有权限，并且surFace创建完成了在去初始化数据，否则会报错
        if (isPermission) {
            if (mCamera != null) { // 避免重复性的去创建摄像机，否则会崩溃
                return;
            }
            
            try {
                // 在surface被创建的时候去创建摄像机，避免重复性创建
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                if (mCamera == null) {
                    LogUtil.e("初始化摄像机失败！");
                    ToastUtil.show("初始化摄像机失败");
                }
                Camera.Parameters parameters = mCamera.getParameters();
                if (mCamera != null) {
                    mCamera.setDisplayOrientation(90);
                    mCamera.unlock();
                }
//                CamcorderProfile profile =
                
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                if (supportedPreviewSizes != null && supportedPreviewSizes.size() > 0) {
                
                }
                
                /*
                 * 1：设置预览尺寸,因为预览的尺寸和最终是录制视频的尺寸无关，所以我们选取最大的数值
                 * 2：获取手机支持的预览尺寸，这个一定不能随意去设置，某些手机不支持，一定会崩溃
                 * 3：通常最大的是手机的分辨率，这样可以让预览画面尽可能清晰并且尺寸不变形，前提是TextureView的尺寸是全屏或者接近全屏
                 */
                // 使用推荐的尺寸去处理
                sizeForVideo = parameters.getPreferredPreviewSizeForVideo();
                if (sizeForVideo != null) {
                    int width = sizeForVideo.width;
                    int height = sizeForVideo.height;
                    LogUtil.e("size:--->推荐尺寸的宽高为：width：" + width + "  height--->:" + height);
                    // 设置预览的尺寸
                    //设置图片尺寸  就拿预览尺寸作为图片尺寸,其实他们基本上是一样的
                    parameters.setPreviewSize(width, height);
                    parameters.setPictureSize(width, height);
                }
                //  parameters.set("orientation", Camera.Parameters.SCENE_MODE_PORTRAIT );//相片方向
                parameters.set("orientation", Camera.Parameters.SCENE_MODE_LANDSCAPE);//相片方向
                parameters.setRotation(90);//相片镜头角度转90度（默认摄像头是横拍）
                
                //缩短Recording启动时间
                parameters.setRecordingHint(true);
                
                //   是否支持影像稳定能力，支持则开启
                if (parameters.isVideoStabilizationSupported()) {
                    parameters.setVideoStabilization(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("error:" + e.getMessage());
                LogUtil.e("初始化相机失败！");
            }
        }
    }
    
    private void startRecorder() {
        if (!isRecording) {
            try {
                mediaRecorder.prepare();//准备
                mediaRecorder.start();//开启
                isRecording = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void stopRecorder() {
        if (isRecording) {
            // 如果正在录制，停止并释放资源
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            
            // 跳转到另一个页面
            Intent intent = new Intent(mContext, FaceVideoPlayerTitleActivity.class);
            intent.putExtra("videoPath", mOutFile.getAbsolutePath());
            startActivity(intent);

//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(mOutFile.getAbsolutePath());
//            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); //宽
//            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); //高
//            String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);//视频的方向角度
//            long duration = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;//视频的长度
//
//            LogUtil.e("width:" + width + "  height: " + height + "   rotation:" + rotation);
        
        }
    }
    
    private void releaseCamera() {
        // 清空摄像机
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param context context
     * @return 检测是否有摄像头，如果没有就给出提示
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    
    private void initMediaRecorder() {
        try {
            if (mOutFile.exists()) {
                // 如果文件存在，删除它，代码保证设备上只有一个录音文件
                mOutFile.delete();
                LogUtil.e("删除了之前存储的视频！");
            }
            
            // 创建录制对象
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            
            if (mCamera == null) {
                initCamera();
            }
            
            // mMediaRecorder.setCamera(camera);之前是需要将摄像头解除锁定 camera.unlock()
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);//设置音频输入源
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//设置视频输入源
            
            //设置视频的摄像头角度 只会改变录制的视频角度
            mediaRecorder.setOrientationHint(270);
            //设置记录会话的最大持续时间（毫秒）
            mediaRecorder.setMaxDuration(5 * 1000);
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            //设置最大录制的大小2M 单位，字节
            mediaRecorder.setMaxFileSize(2 * 1024 * 1024);
            
            // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//音频输出格式
            
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频的编码格式
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);//设置图像编码格式
            
            // 不使用系统给定的配置
            //  CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            //  mediaRecorder.setProfile(cProfile);
            
            /*
             * 1:设置视频尺寸，通常搭配码率一起使用，可调整视频清晰度
             * 2:此处一定不要胡乱设置，某些机型上不支持的话，会直接崩溃掉，最好直接用系统推荐的尺寸去处理
             */
            if (sizeForVideo != null) {
                // 设置视频的尺寸
                mediaRecorder.setVideoSize(sizeForVideo.width, sizeForVideo.height);
                //设置比特率,比特率是每一帧所含的字节流数量,比特率越大每帧字节越大,画面就越清晰,算法一般是 5 * 选择分辨率宽 * 选择分辨率高,一般可以调整5-10,比特率过大也会报错
                mediaRecorder.setVideoEncodingBitRate(sizeForVideo.width * sizeForVideo.height);//设置视频的比特率
            }
            
            //  mediaRecorder.setVideoFrameRate(60);//设置视频的帧率
            mediaRecorder.setOutputFile(mOutFile.getAbsolutePath());
            
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    // 发生错误，停止录制
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = false;
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("init:MediaRecorder --->Error:" + e.getMessage());
        }
    }
    
    /**
     * 获取录制视频的文件
     */
    private void getVideoFile() {
        // 检测父类文件
        if (!TextUtils.isEmpty(FACE_PATH)) {
            File file = new File(FACE_PATH);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (mkdirs) {
                    mOutFile = new File(file, "face.mp4");
                }
            } else {
                mOutFile = new File(file, "face.mp4");
            }
        }
        
        // 如果文件对象为null,说明之前的sd卡目录创建失败了
        if (mOutFile == null) {
            File rootFileForApp = fileUtil.getRootFileForApp(mContext);
            if (rootFileForApp != null) {
                mOutFile = new File(rootFileForApp, "face.mp4");
            }
        }
        
        LogUtil.e("当前的路径为：" + mOutFile.getAbsolutePath());
    }
    
    @Override
    protected void onDestroy() {
        stopRecorder();
        releaseCamera();
        super.onDestroy();
    }
    
}