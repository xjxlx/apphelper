package com.android.helper.httpclient;

import com.android.helper.utils.LogUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 此方法启用，因为这么做虽然可以管理整个app的周期，但是每个页面可能会存在单个的功能，不推荐这么使用，因为一旦这么使用了，
 * 那么只要有activity走入了onDestroy方法中，就会清空所有的请求，可能会存在未知的异常，所以最好的办法是在activity的基类
 * 中去每次创建一个静态的对象，然后分别去管理所有的数据，这样会更好。
 *
 
 */
public class RxManager {
    
    private static CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private static RxManager mRxManager;
    
    public RxManager() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
    }
    
    public synchronized static RxManager getInstance() {
        if (mRxManager == null) {
            mRxManager = new RxManager();
        }
        return mRxManager;
    }
    
    /**
     * 添加一个Disposable 对象到管理类中去
     *
     * @param disposable disposable 观察者对象
     */
    public void add(Disposable disposable) {
        if (disposable != null) {
            if (!mCompositeDisposable.isDisposed()) {
                mCompositeDisposable.add(disposable);
                LogUtil.e("添加了一个请求对象！");
            }
        }
    }
    
    /**
     * @param disposable 移除一个被观察者的对象 ,如果给定的disposable容器是此容器的一部分，则删除并处置它
     */
    public void remove(Disposable disposable) {
        if ((disposable != null) && (!disposable.isDisposed())) {
            mCompositeDisposable.remove(disposable);
        }
    }
    
    /**
     * 移除(但不处置)给定的disposable对象(如果它是容器的一部分)，调用remove方法必须先调用这个方法，可以看源码
     *
     * @param disposable Disposable对象
     */
    public void delete(Disposable disposable) {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.delete(disposable);
        }
    }
    
    /**
     * 清空所有的对象，平时就用这个数据就可以了，不要去使用dispose方法，会导致数据无法添加成功
     */
    public void clear() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }
    
    /**
     * 移除对象，此方法禁用，一旦使用了这个方法，则不能再去添加对象到集合中，因为源码中一旦断开，就不可逆了
     */
    private void dispose() {
        LogUtil.e("移除了一个请求对象！");
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }
    
}
