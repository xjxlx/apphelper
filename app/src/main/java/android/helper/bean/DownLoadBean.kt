package android.helper.bean

class DownLoadBean {
    
    var url: String? = null
    var outputPath: String? = null
    var id: String? = null
    var contentLength: Long = 0 // 文件的总长度
    var tempFileLength: Long = 0 // 当前文件的长度
    
    override fun toString(): String {
        return "DownLoadBean(url=$url, name=$outputPath, id=$id)"
    }
    
}