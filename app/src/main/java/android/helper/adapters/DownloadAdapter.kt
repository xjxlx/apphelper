package android.helper.adapters

import android.helper.R
import android.helper.base.BaseActivity
import android.helper.base.BaseRecycleAdapter
import android.helper.base.BaseVH
import android.helper.bean.DownLoadBean
import android.helper.interfaces.listener.ProgressListener
import android.helper.utils.LogUtil
import android.helper.utils.download.DownLoadManager
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import okhttp3.Response
import java.util.*

class DownloadAdapter(mContext: BaseActivity, mList: ArrayList<DownLoadBean>) : BaseRecycleAdapter<DownLoadBean, DownloadAdapter.DlHolder>(mContext, mList) {

    val downLoadManager: DownLoadManager by lazy {
        return@lazy DownLoadManager.getSingleInstance()
    }

    override fun createViewHolder(inflate: View?): DlHolder {
        return DlHolder(inflate!!)
    }

    override fun onBindViewHolder(holder: DlHolder, position: Int) {
        val bean = mList[position]

        val tempFileLength = bean.tempFileLength
        val contentLength = bean.contentLength

        if ((tempFileLength > 0) && (tempFileLength < contentLength)) {
            holder.tv_download.text = "继续下载"
            val fl = tempFileLength.toFloat() / contentLength * 100
            // 进度条
            holder.progress.progress = fl.toInt()

            // 百分比进度
            holder.tv_current_progress.text = fl.toString()
        }

        holder.tv_download.setOnClickListener {

            downLoadManager.download(bean.url!!, bean.outputPath!!, object : ProgressListener {
                override fun onComplete(response: Response?) {
                    LogUtil.e("" + position + "下载结束了！")
                    holder.tv_download.text = "下载完成"
                }

                override fun onProgress(progress: Double, contentLength: Long, percentage: String?) {
                    holder.progress.progress = (progress / contentLength * 100).toInt()
                    holder.tv_current_progress.text = percentage
                }

                override fun onError(throwable: Throwable?) {
                    LogUtil.e("" + position + "--->onError：" + throwable!!.message)
                    val currentStatus = downLoadManager.currentStatus
                    if (currentStatus == 3) {
                        holder.tv_download.text = "下载中断"
                    } else if (currentStatus == 4) {
                        holder.tv_download.text = "下载取消"
                    }
                }

                override fun onStart(contentLength: Long) {
                    LogUtil.e("" + position + "开始下载了！")
                    holder.tv_download.text = "下载中"
                }
            })
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(holder.tv_download, position, bean)
            }
        }


        holder.tv_cancel.setOnClickListener {
            // 取消下载
            downLoadManager.cancel(bean.url!!, bean.outputPath!!)

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(holder.tv_cancel, position, bean)
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.item_download;
    }

    class DlHolder(itemView: View) : BaseVH(itemView) {
        val tv_download: Button = itemView.findViewById(R.id.tv_download)
        val tv_cancel: Button = itemView.findViewById(R.id.tv_cancel)
        val tv_current_progress: TextView = itemView.findViewById(R.id.tv_current_progress)
        val progress: ProgressBar = itemView.findViewById(R.id.progress)
    }

    override fun onBindHolder(holder: DlHolder, position: Int) {

    }
}