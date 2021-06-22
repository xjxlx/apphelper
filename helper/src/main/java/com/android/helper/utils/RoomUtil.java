package com.android.helper.utils;

import android.annotation.SuppressLint;

import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.room.RoomDeleteListener;
import com.android.helper.interfaces.room.RoomInsertListener;
import com.android.helper.interfaces.room.RoomQueryListener;
import com.android.helper.interfaces.room.RoomUpdateListener;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Room数据库的操作类，适用于不持有观察者对象的操作,适用于普通的异步操作数据库的增删改查操作
 */
public class RoomUtil {

    /**
     * <p>
     * 使用room数据库去插入一条数据局，如果成功了，就会返回插入的id，如果失败了，就返回-1
     * </p>
     *
     * @param insertListener room数据库添加数据的回调，如果成功了就返回插入的id,如果失败了，就返回-1
     */
    @SuppressLint("CheckResult")
    public static void insert(RoomInsertListener insertListener) {
        if (insertListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Long>) emitter -> {
                        try {
                            long insert = insertListener.insert();
                            emitter.onNext(insert);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST)
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
     * <p>
     * room数据库删除一条数据，如果成功了，则返回删除条目的数量，如果失败了，则返回0
     * </p>
     *
     * @param deleteListener room数据库添加数据的回调
     */
    public static void delete(RoomDeleteListener deleteListener) {
        if (deleteListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Integer>) emitter -> {
                        try {
                            int delete = deleteListener.delete();
                            emitter.onNext(delete);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST)
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

    /**
     * <p>
     * 使用room数据库更新一条数据，如果成功了，则返回更新数量的条目，如果更新失败了，则返回0
     * </p>
     *
     * @param updateListener room数据库添加数据的回调
     */
    public static void update(RoomUpdateListener updateListener) {
        if (updateListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<Integer>) emitter -> {
                        try {
                            int update = updateListener.update();
                            emitter.onNext(update);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }

                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST)
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<Integer>() {
                        @Override
                        public void onNext(Integer integer) {
                            updateListener.onResult(true, integer);
                        }

                        @Override
                        public void onError(Throwable t) {
                            updateListener.onResult(false, 0);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * <p>
     * 使用room数据库，查询数据，根据传入的类型，返回单个或者多个数据，如果成功了，则返回对应的查询数据，
     * 如果失败了就返回null
     * </p>
     *
     * @param queryListener room数据库添加数据的回调
     */
    public static <T> void query(RoomQueryListener<T> queryListener) {
        if (queryListener != null) {
            Flowable
                    .create((FlowableOnSubscribe<T>) emitter -> {

                        try {
                            T query = queryListener.query();
                            emitter.onNext(query);
                        } catch (Exception e) {
                            emitter.onError(e);
                        }

                        emitter.onComplete();

                    }, BackpressureStrategy.LATEST) //create方法中多了一个BackpressureStrategy类型的参数
                    .compose(RxUtil.getScheduler())
                    .subscribe(new DisposableSubscriber<T>() {
                        @Override
                        public void onNext(T t) {
                            queryListener.onResult(true, t);
                        }

                        @Override
                        public void onError(Throwable t) {
                            queryListener.onResult(false, null);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

}
