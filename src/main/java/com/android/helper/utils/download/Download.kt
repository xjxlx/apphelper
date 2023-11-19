package com.android.helper.utils.download

class Download {

    var id: String = "" // 唯一的id，这个字段一定要唯一，否则就会导致下载异常
    var url: String = "" // 下载路径的url
    var outputPath: String = "" // 文件存储的名字，如果是多个文件同时下载的话，需要设置唯一的文件名字
    var contentLength: Long = 0 // 文件的总长度
    var tempFileLength: Long = 0 // 当前文件的长度

    override fun toString(): String {
        return "DownLoad(id='$id', url='$url', outputPath='$outputPath', contentLength=$contentLength, tempFileLength=$tempFileLength)"
    }
}