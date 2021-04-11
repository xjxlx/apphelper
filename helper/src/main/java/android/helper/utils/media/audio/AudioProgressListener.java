package android.helper.utils.media.audio;

public interface AudioProgressListener {

    /**
     * 当前缓冲的进度
     *
     * @param total   总长度
     * @param current 当前的进度
     * @param percent 百分比
     */
    void onBufferProgress(int total, double current, int percent);

    /**
     * @param total   总长度
     * @param current 当前的进度
     * @param percent 百分比
     */
    void onProgress(int total, int current, String percent);
}



