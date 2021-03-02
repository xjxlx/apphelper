package android.helper.interfaces;

import android.helper.httpclient.BaseException;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;

public interface HttpClientListener<T> {

    /**
     * @return 返回一个Flowable类型的请求对象
     */
    public Flowable<T> getApiService();

    /**
     * @return 返回一个Call类型的请求对象，作为备选条件使用
     */
    public Call<T> getCall();

    /**
     * @param map 分页参数的集合
     * @return 分页控制器
     */
    public HashMap<String, Object> pageControl(HashMap<String, Object> map);

    /**
     * @return 分页的过滤条件，具体的逻辑，全部交给子类去动态实现
     */
    public boolean filterForPage(T t);

    /**
     * @return 分页的数量
     */
    public int pageSize();

    /**
     * @return 请求数据弹窗的过滤条件，全部交给子类去动态实现
     */
    public boolean filterForDialog();

    /**
     * 开始请求网络数据
     */
    public Disposable requestData();

    /**
     * 请求开始的调用
     */
    public void onHttpStart();

    /**
     * @param t 数据请求成功的回调
     */
    public void onSuccess(T t);

    /**
     * @param throwable 数据请求异常的回调
     */
    public void onFailure(BaseException throwable);

    /**
     * 请求结束的方法
     */
    public void onHttpComplete();
}
