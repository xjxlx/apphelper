package android.helper.interfaces.listener;

/**
 * 数据结果返回的回调
 *
 * @param <T> 指定的数据类型
 */
public interface CallBackListener<T> {

    /**
     * @param successful 事件是否成功
     * @param tag        事件的标记
     * @param t          返回的指定类型的具体数据
     */
    void onBack(boolean successful, Object tag, T t);
}
