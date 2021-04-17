package android.helper.ui.activity

import android.helper.R
import android.view.View
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.FileUtil
import com.android.helper.utils.LogUtil
import com.android.helper.utils.photo.GlideEngine
import com.android.helper.utils.photo.GlideUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_selector_image.*

class SelectorImageActivity : BaseTitleActivity() {

    override fun getTitleLayout(): Int {
        return R.layout.activity_selector_image
    }

    override fun initView() {
        super.initView()
        setTitleContent("图片选择器")

        setonClickListener(R.id.btn_selector_image, R.id.btn_selector_video)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btn_selector_image -> {
                selectorImage()
            }

            R.id.btn_selector_video -> {
                selectorVideo()
            }
        }
    }

    private fun selectorImage() {
        val createGlideEngine = GlideEngine.createGlideEngine()
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(createGlideEngine)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: List<LocalMedia?>) {
                        // 结果回调
                        LogUtil.e("result:$result")
                        val media = result[0]
                        var urls: String?
                        if (media != null) {
                            val compressed: Boolean = media.isCompressed()
                            if (compressed) {
                                urls = media.compressPath
                                LogUtil.e("压缩选择图片的路径为：$urls")
                            } else {
                                urls = media.path
                                LogUtil.e("没有压缩选的图片的路径为：$urls")
                            }

                            val uriToPath = FileUtil.UriToPath(mContext, urls)
                            LogUtil.e("转换后的图片路径为：：$uriToPath")

                            GlideUtil.loadView(mContext, uriToPath, iv_image)
                        }
                    }

                    override fun onCancel() {
                        // 取消
                    }
                })
    }

    private fun selectorVideo() {
        val createGlideEngine = GlideEngine.createGlideEngine()
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())
                .imageEngine(createGlideEngine)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: List<LocalMedia?>) {
                        // 结果回调
                        LogUtil.e("result:$result")
                        val media = result[0]
                        var urls: String? = ""
                        if (media != null) {
                            val compressed: Boolean = media.isCompressed()
                            if (compressed) {
                                urls = media.compressPath
                                LogUtil.e("压缩选择视频的路径为：$urls")
                            } else {
                                urls = media.path
                                LogUtil.e("没有压缩选择视频的路径为：$urls")
                            }

                            val uriToPath = FileUtil.UriToPath(mContext, urls)
                            LogUtil.e("转换后的视频路径为：：$uriToPath")

                            GlideUtil.loadView(mContext, uriToPath, iv_image)
                        }
                    }

                    override fun onCancel() {
                        // 取消
                    }
                })
    }

}