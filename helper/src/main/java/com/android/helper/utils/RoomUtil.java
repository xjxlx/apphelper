package com.android.helper.utils;

import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.room.RoomDeleteListener;
import com.android.helper.interfaces.room.RoomInsertListener;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Room数据库的操作类，适用于不持有观察者对象的操作
 */
public class RoomUtil {

    /**
     * @param insertListener room数据库添加数据的回调，如果成功了就返回插入的id,如果失败了，就返回-1
     */
    public static void executeInsert(RoomInsertListener insertListener) {
        if (insertListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Long>) emitter -> {

                        long insert = insertListener.insert();
                        emitter.onNext(insert);
                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST) //create方法中多了一个BackpressureStrategy类型的参数
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<Long>() {
                        @Override
                        public void onNext(Long aLong) {
                            insertListener.onResult(true, aLong);
                        }

                        @Override
                        public void onError(Throwable t) {
                            insertListener.onResult(false, -1);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * @param deleteListener room数据库添加数据的回调
     */
    public static <T> void executeDelete(RoomDeleteListener deleteListener) {
        if (deleteListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Integer>) emitter -> {

                        int delete = deleteListener.delete();
                        emitter.onNext(delete);
                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST) //create方法中多了一个BackpressureStrategy类型的参数
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<Integer>() {
                        @Override
                        public void onNext(Integer integer) {
                            deleteListener.onResult(true, integer);
                        }

                        @Override
                        public void onError(Throwable t) {
                            deleteListener.onResult(false, 0);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

}
