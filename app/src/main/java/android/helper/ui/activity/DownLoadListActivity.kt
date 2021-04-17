package android.helper.ui.activity

import android.Manifest
import androidx.recyclerview.widget.LinearLayoutManager
import android.helper.R
import android.helper.adapters.DownloadAdapter
import android.helper.bean.DownLoadBean
import com.android.helper.base.BaseTitleActivity
import com.android.helper.utils.EncryptionUtil
import com.android.helper.utils.FileUtil
import com.android.helper.utils.LogUtil
import com.android.helper.utils.RxPermissionsUtil
import kotlinx.android.synthetic.main.activity_down_load_list.*
import java.io.File
import java.util.*

class DownLoadListActivity : BaseTitleActivity() {

    private val mList = arrayListOf<DownLoadBean>()

    private lateinit var file: File

    override fun getTitleLayout(): Int {
        return R.layout.activity_down_load_list
    }

    override fun initView() {
        super.initView()
        setTitleContent("带进度条的下载列表")

        val permissionsUtil = RxPermissionsUtil(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsUtil.setSinglePermissionListener { havePermission ->
            if (!havePermission) {
                return@setSinglePermissionListener
            }
            val rootFileForSd = FileUtil.getRootFileForSd()
            var rootFile: File? = null
            for (index in 0..19) {
                val downLoadBean = DownLoadBean()
                downLoadBean.url = "http://cdn.smartservice.bjev.com.cn/dplus/xnyapp/3dacac8e71d6da7f"
                if (rootFileForSd.exists()) {
                    rootFile = File(rootFileForSd, "a_pdf_list")
                    if (!rootFile.exists()) {
                        rootFile.mkdirs();
                    }
                }
                file = File(rootFile, ("test_" + index + "_abc.pdf"))


                downLoadBean.outputPath = file.absolutePath
                val enCodeBase64 = EncryptionUtil.enCodeBase64(downLoadBean.url + downLoadBean.outputPath)
                downLoadBean.id = enCodeBase64

                mList.add(downLoadBean)
            }

            val adapter = DownloadAdapter(mContext, mList)

            adapter.setOnLoadComplete { _, _ ->
                LogUtil.e("------------->")
            }
            rv_download_list.adapter = adapter
            rv_download_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)



            LogUtil.e(Arrays.toString(mList.toArray()))
        }
    }

}