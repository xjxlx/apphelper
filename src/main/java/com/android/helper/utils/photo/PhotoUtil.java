package com.android.helper.utils.photo;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.FileUtil;
import com.android.helper.utils.LogUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import org.jetbrains.annotations.NotNull;

import kotlin.Suppress;

/**
 * 照片的工具类
 */
public class PhotoUtil implements BaseLifecycleObserver {
    private static PhotoUtil mPhotoUtil;

    public static PhotoUtil getInstance() {
        if (mPhotoUtil == null) {
            mPhotoUtil = new PhotoUtil();
        }
        return mPhotoUtil;
    }

    /**
     * @param context    context
     * @param localMedia localMedia
     * @return 依赖于图片选择库的一个工具，用来返回选中的对象地址
     */
    @Suppress(names = "MISSING_DEPENDENCY_CLASS")
    public static String getPathForSelectorPicture(@NotNull Context context, @NotNull LocalMedia localMedia) {
        String url = "";
        boolean compressed = localMedia.isCompressed();

        if (compressed) {
            url = localMedia.getCompressPath();
            LogUtil.e("压缩拍摄视频的路径为：$mPhoto_path");
        } else {
            // android Q 版本数据的获取
            // if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT == 29) {
                String androidQToPath = localMedia.getAndroidQToPath();
                if (!TextUtils.isEmpty(androidQToPath)) {
                    url = androidQToPath;
                }
            } else {
                // 其他版本数据的获取
                String realPath = localMedia.getRealPath();
                if (!TextUtils.isEmpty(realPath)) {
                    url = realPath;
                }
            }

            // 如果还获取不到，就获取path路径
            if (TextUtils.isEmpty(url)) {
                String path = localMedia.getPath();
                if (!TextUtils.isEmpty(path)) {
                    url = path;
                }
            }

            // 如果还获取不到，就获取path路径
            if (TextUtils.isEmpty(url)) {
                String originalPath = localMedia.getOriginalPath();
                if (!TextUtils.isEmpty(originalPath)) {
                    url = originalPath;
                }
            }
            LogUtil.e("没有压缩拍摄视频的路径为：$mPhoto_path");
        }
        return FileUtil.getInstance().UriToPath(context, url);
    }

    /**
     * 选择拍照
     *
     * @param activity     activity的上下文
     * @param isCamera     是否显示拍照的按钮
     * @param maxSelectNum 最大选择数量
     * @param listener     选择图片后返回的结果
     */
    public void SelectorImage(FragmentActivity activity, boolean isCamera, int maxSelectNum,
                              OnResultCallbackListener<LocalMedia> listener) {
//        PictureSelector.create(this)
//                .openGallery()//相册 媒体类型 PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
//                //.openCamera()//单独使用相机 媒体类型 PictureMimeType.ofImage()、ofVideo()
//                .theme()// xml样式配制 R.style.picture_default_style、picture_WeChat_style or 更多参考Demo
//                .imageEngine()// 图片加载引擎 需要 implements ImageEngine接口
//                .compressEngine() // 自定义图片压缩引擎
//                .selectionMode()//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
//                .isPageStrategy()//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
//                .isSingleDirectReturn()//PictureConfig.SINGLE模式下是否直接返回
//                .isWeChatStyle()//开启R.style.picture_WeChat_style样式
//                .setPictureStyle()//动态自定义相册主题
//                .setPictureCropStyle()//动态自定义裁剪主题
//                .setPictureWindowAnimationStyle()//相册启动退出动画
//                .isCamera()//列表是否显示拍照按钮
//                .isZoomAnim()//图片选择缩放效果
//                .imageFormat()//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
//                .setCameraImageFormat(PictureMimeType.JPEG)// 相机图片格式后缀,默认.jpeg
//                .setCameraVideoFormat(PictureMimeType.MP4)// 相机视频格式后缀,默认.mp4
//                .setCameraAudioFormat(PictureMimeType.AMR)// 录音音频格式后缀,默认.amr
//                .maxSelectNum()//最大选择数量,默认9张
//                .minSelectNum()// 最小选择数量
//                .maxVideoSelectNum()//视频最大选择数量
//                .minVideoSelectNum()//视频最小选择数量
//                .videoMaxSecond()// 查询多少秒以内的视频
//                .videoMinSecond()// 查询多少秒以内的视频
//                .imageSpanCount()//列表每行显示个数
//                .openClickSound()//是否开启点击声音
//                .selectionMedia()//是否传入已选图片
//                .recordVideoSecond()//录制视频秒数 默认60s
//                .filterMinFileSize() // 过滤最小的文件
//                .filterMaxFileSize() // 过滤最大的文件
//                .queryMimeTypeConditions(PictureMimeType.ofJPEG()) // 只查询什么类型的文件
//                .previewEggs()//预览图片时是否增强左右滑动图片体验
//                .cropCompressQuality()// 注：已废弃 改用cutOutQuality()
//                .isGif()//是否显示gif
//                .previewImage()//是否预览图片
//                .previewVideo()//是否预览视频
//                .enablePreviewAudio()//是否预览音频
//                .enableCrop()//是否开启裁剪
//                .cropWH()// 裁剪宽高比,已废弃，改用. cropImageWideHigh()方法
//                .cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
//                .withAspectRatio()//裁剪比例
//                .cutOutQuality()// 裁剪输出质量 默认100
//                .freeStyleCropEnabled()//裁剪框是否可拖拽
//                .freeStyleCropMode(OverlayView.DEFAULT_FREESTYLE_CROP_MODE)// 裁剪框拖动模式
//                .isCropDragSmoothToCenter(true)// 裁剪框拖动时图片自动跟随居中
//                .circleDimmedLayer()// 是否开启圆形裁剪
//                .setCircleDimmedColor()//设置圆形裁剪背景色值
//                .setCircleDimmedBorderColor()//设置圆形裁剪边框色值
//                .setCircleStrokeWidth()//设置圆形裁剪边框粗细
//                .showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                .showCropGrid()//是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                .rotateEnabled()//裁剪是否可旋转图片
//                .scaleEnabled()//裁剪是否可放大缩小图片
//                .isDragFrame()//是否可拖动裁剪框(固定)
//                .hideBottomControls()//显示底部uCrop工具栏
//                .basicUCropConfig()//对外提供ucrop所有的配制项
//                .compress()//是否压缩
//                .compressEngine()// 自定义压缩引擎
//                .compressFocusAlpha()//压缩后是否保持图片的透明通道
//                .minimumCompressSize()// 小于多少kb的图片不压缩
//                .videoQuality()//视频录制质量 0 or 1
//                .compressQuality()//图片压缩后输出质量
//                .synOrAsy()//开启同步or异步压缩
//                .queryMaxFileSize()//查询指定大小内的图片、视频、音频大小，单位M
//                .compressSavePath()//自定义压缩图片保存地址，注意Q版本下的适配
//                .sizeMultiplier()//glide加载大小，已废弃
//                .glideOverride()//glide加载宽高，已废弃
//                .isMultipleSkipCrop()//多图裁剪是否支持跳过
//                .isMultipleRecyclerAnimation()// 多图裁剪底部列表显示动画效果
//                .querySpecifiedFormatSuffix()//只查询指定后缀的资源，PictureMimeType.ofJPEG() ...
//                .isReturnEmpty()//未选择数据时按确定是否可以退出
//                .isAndroidQTransform()//Android Q版本下是否需要拷贝文件至应用沙盒内
//                .setRequestedOrientation()//屏幕旋转方向 ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ...
//                .isOriginalImageControl()//开启原图选项
//                .bindCustomPlayVideoCallback()//自定义视频播放拦截
//                .bindCustomCameraInterfaceListener()//自定义拍照回调接口
//                .bindCustomPreviewCallback()// 自定义图片预览回调接口
//                .cameraFileName()//自定义拍照文件名，如果是相册内拍照则内部会自动拼上当前时间戳防止重复
//                .renameCompressFile()//自定义压缩文件名，多张压缩情况下内部会自动拼上当前时间戳防止重复
//                .renameCropFileName()//自定义裁剪文件名，多张裁剪情况下内部会自动拼上当前时间戳防止重复
//                .setRecyclerAnimationMode()//列表动画效果,AnimationType.ALPHA_IN_ANIMATION、SLIDE_IN_BOTTOM_ANIMATION
//                .isUseCustomCamera()// 开启自定义相机
//                .setButtonFeatures()// 自定义相机按钮状态,CustomCameraView.BUTTON_STATE_BOTH
//                .setLanguage()//国际化语言 LanguageConfig.CHINESE、ENGLISH、JAPAN等
//                .isWithVideoImage()//图片和视频是否可以同选,只在ofAll模式下有效
//                .isMaxSelectEnabledMask()//选择条件达到阀时列表是否启用蒙层效果
//                .isAutomaticTitleRecyclerTop()//图片列表超过一屏连续点击顶部标题栏快速回滚至顶部
//                .loadCacheResourcesCallback()//获取ImageEngine缓存图片，参考Demo
//                .setOutputCameraPath()// 自定义相机输出目录只针对Android Q以下版本，具体参考Demo
//                .forResult();//结果回调分两种方式onActivityResult()和OnResultCallbackListener方式

        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector
                    .create(activity)
                    .openGallery(PictureMimeType.ofImage())
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isCamera(isCamera) // 是否显示拍照的按钮
                    .maxSelectNum(maxSelectNum) // 最大选择数量
                    .isPreviewEggs(true) // 是否预览图片
                    .isCompress(true) // 是否压缩
                    .minimumCompressSize(30) //  小于多少kb的图片不压缩
                    .synOrAsy(true)// 异步压缩
                    .forResult(listener); // 返回结果
        }
    }

    /**
     * @param fragment     fragment上下文
     * @param isCamera     是否显示拍照的按钮
     * @param maxSelectNum 最大选择数量
     * @param listener     选择图片后返回的结果
     */
    public void SelectorImage(Fragment fragment, boolean isCamera, int maxSelectNum, OnResultCallbackListener<LocalMedia> listener) {
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector
                    .create(fragment)
                    .openGallery(PictureMimeType.ofImage())
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isCamera(isCamera) // 是否显示拍照的按钮
                    .maxSelectNum(maxSelectNum) // 最大选择数量
                    .isPreviewEggs(true) // 是否预览图片
                    .isCompress(true) // 是否压缩
                    .minimumCompressSize(30) //  小于多少kb的图片不压缩
                    .synOrAsy(true)// 异步压缩
                    .forResult(listener); // 返回结果
        }
    }

    /**
     * 打开照相机
     */
    public void openCamera(FragmentActivity activity, OnResultCallbackListener<LocalMedia> listener) {
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector.create(activity)
                    .openCamera(PictureMimeType.ofImage())
                    .isCompress(true) // 打开压缩
                    .selectionMode(PictureConfig.SINGLE)
                    .imageEngine(GlideEngine.createGlideEngine())
                    .forResult(listener);
        }
    }

    /**
     * 打开照相机
     */
    public void openCamera(Fragment fragment, OnResultCallbackListener<LocalMedia> listener) {
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector.create(fragment)
                    .openCamera(PictureMimeType.ofImage())
                    .isCompress(true) // 打开压缩
                    .selectionMode(PictureConfig.SINGLE)
                    .imageEngine(GlideEngine.createGlideEngine())
                    .forResult(listener);
        }
    }

    /**
     * @param activity          fragment上下文
     * @param isCamera          是否显示拍照的按钮
     * @param maxVideoSelectNum 视频最大选择数量
     * @param listener          选择图片后返回的结果
     */
    public void SelectorVideo(FragmentActivity activity, boolean isCamera, int maxVideoSelectNum,
                              OnResultCallbackListener<LocalMedia> listener) {
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector
                    .create(activity)
                    .openGallery(PictureMimeType.ofVideo())
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isCamera(isCamera) // 是否显示拍照的按钮
                    .maxVideoSelectNum(maxVideoSelectNum) // 最大选择数量
                    .maxSelectNum(maxVideoSelectNum)
                    .isPreviewVideo(true) //是否预览视频
                    .isCompress(true) // 是否压缩
                    .minimumCompressSize(30) //  小于多少kb的图片不压缩
                    .synOrAsy(true)// 异步压缩
                    .forResult(listener); // 返回结果
        }
    }

    /**
     * @param fragment          fragment上下文
     * @param isCamera          是否显示拍照的按钮
     * @param maxVideoSelectNum 视频最大选择数量
     * @param listener          选择图片后返回的结果
     */
    public void SelectorVideo(Fragment fragment, boolean isCamera, int maxVideoSelectNum,
                              OnResultCallbackListener<LocalMedia> listener) {

        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector
                    .create(fragment)
                    .openGallery(PictureMimeType.ofVideo())
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isCamera(isCamera) // 是否显示拍照的按钮
                    .maxVideoSelectNum(maxVideoSelectNum) // 最大选择数量
                    .maxSelectNum(maxVideoSelectNum)
                    .isPreviewVideo(true) //是否预览视频
                    .isCompress(true) // 是否压缩
                    .minimumCompressSize(30) //  小于多少kb的图片不压缩
                    .synOrAsy(true)// 异步压缩
                    .forResult(listener); // 返回结果
        }
    }

    /**
     * 打开录像机
     */
    public void openVideo(FragmentActivity activity, int recordVideoSecond, OnResultCallbackListener<LocalMedia> listener) {
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector.create(activity)
                    .openCamera(PictureMimeType.ofVideo())
                    .isCompress(true) // 打开压缩
                    .selectionMode(PictureConfig.SINGLE)
                    .recordVideoSecond(recordVideoSecond)//录制视频秒数 默认60s
                    .videoMaxSecond(recordVideoSecond)
                    .imageEngine(GlideEngine.createGlideEngine())
                    .forResult(listener);
        }
    }

    /**
     * 打开录像机
     */
    public void openVideo(Fragment fragment, int recordVideoSecond, OnResultCallbackListener<LocalMedia> listener) {
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);

            PictureSelector.create(fragment)
                    .openCamera(PictureMimeType.ofVideo())
                    .isCompress(true) // 打开压缩
                    .selectionMode(PictureConfig.SINGLE)
                    .recordVideoSecond(recordVideoSecond)//录制视频秒数 默认60s
                    .imageEngine(GlideEngine.createGlideEngine())
                    .forResult(listener);
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
//        //包括裁剪和压缩后的缓存，要在上传成功后调用，type 指的是图片or视频缓存取决于你设置的ofImage或ofVideo 注意：需要系统sd卡权限
//        PictureCacheManager.deleteCacheDirFile(this,type);
//        // 清除所有缓存 例如：压缩、裁剪、视频、音频所生成的临时文件
//        PictureCacheManager.deleteAllCacheDirFile(this);
//        // 清除缓存且刷新图库
//        PictureCacheManager.deleteAllCacheDirRefreshFile(this);

    }
}
