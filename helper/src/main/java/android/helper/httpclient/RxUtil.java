package android.helper.httpclient;

import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil<T> {
    
    /**
     *
     * @param <T>
     * @return  只做线程的转换，其实如果这里用了flatMap做一次数据转换其实会更好
     */
    public static <T> FlowableTransformer<T, T> getScheduler() {
        return upstream ->
                upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }
    
//    public static <T> FlowableTransformer<T, T> getScheduler2() {
//        return upstream ->
//                upstream
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Function<T, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(@NonNull T t) throws Exception {
//                        return null;
//                    }
//                })
//                ;
//    }

    
    
    
//    private static <T> Flowable<T> createData(final T data) {
//        return Flowable.create(new Observable.OnSubscribe<T>() {
//            @Override
//            public void call(Subscriber<? super T> subscriber) {
//                if (subscriber != null) {
//                    try {
//                        subscriber.onNext(data);
//                        subscriber.onCompleted();
//                    } catch (Exception e) {
//                        subscriber.onError(e);
//                    }
//                }
//            }
//        }, );
//    }
}
