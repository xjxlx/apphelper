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
import kotlinx.android.synthetic.main.item_download.view.*
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
            holder.itemView.tv_download.text = "继续下载"

            val fl = tempFileLength.toFloat() / contentLength * 100
            // 进度条
            holder.itemView.progress.progress = fl.toInt()

            // 百分比进度
            holder.itemView.tv_current_progress.text = fl.toString()
        }

        holder.itemView.tv_download.setOnClickListener {

            downLoadManager.download(bean.url!!, bean.outputPath!!, object : ProgressListener {
                override fun onComplete(response: Response?) {
                    LogUtil.e("" + position + "下载结束了！")
                    holder.itemView.tv_download.text = "下载完成"
                }

                override fun onProgress(progress: Double, contentLength: Long, percentage: String?) {
                    holder.itemView.progress.progress = (progress / contentLength * 100).toInt()
                    holder.itemView.tv_current_progress.text = percentage
                }

                override fun onError(throwable: Throwable?) {
                    LogUtil.e("" + position + "--->onError：" + throwable!!.message)
                    val currentStatus = downLoadManager.currentStatus
                    if (currentStatus == 3) {
                        holder.itemView.tv_download.text = "下载中断"
                    } else if (currentStatus == 4) {
                        holder.itemView.tv_download.text = "下载取消"
                    }
                }

                override fun onStart(contentLength: Long) {
                    LogUtil.e("" + position + "开始下载了！")
                    holder.itemView.tv_download.text = "下载中"
                }
            })
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(holder.itemView.tv_download, position, bean)
            }
        }


        holder.itemView.tv_cancel.setOnClickListener {
            // 取消下载
            downLoadManager.cancel(bean.url!!, bean.outputPath!!)

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(holder.itemView.tv_cancel, position, bean)
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.item_download;
    }

    class DlHolder(itemView: View) : BaseVH(itemView) {
    }

    override fun onBindHolder(holder: DlHolder, position: Int) {

    }
}