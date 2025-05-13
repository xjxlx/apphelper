package com.android.helper.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.interfaces.listener.CallBackListener;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.subscribers.DisposableSubscriber;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AssetsUtil {

    public static AssetsUtil assetsUtil;

    private AssetsUtil() {
    }

    /**
     * @return 返回一个单利的对象
     */
    public static AssetsUtil getInstance() {
        if (assetsUtil == null) {
            assetsUtil = new AssetsUtil();
        }
        return assetsUtil;
    }

    /**
     * 异步线程获取数据
     */
    public void initJson(Context context, String fileName, CallBackListener<String> callBackListener) {
        Flowable.create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Exception {
                        if (context == null || (TextUtils.isEmpty(fileName))) {
                            emitter.onError(new NullPointerException("对象为空"));
                        } else {
                            String jsonForAssets = getJsonForAssets(context, fileName);
                            if (TextUtils.isEmpty(jsonForAssets)) {
                                emitter.onError(new NullPointerException("获取数据为空"));
                            } else {
                                emitter.onNext(jsonForAssets);
                            }
                        }
                        emitter.onComplete();
                    }
                }, BackpressureStrategy.LATEST)
                .compose(RxUtil.getSchedulerFlowable())
                .subscribe(new DisposableSubscriber<String>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        if (callBackListener != null) {
                            callBackListener.onBack(false, "数据获取中", "");
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (callBackListener != null) {
                            callBackListener.onBack(true, "数据获取成功", s);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (callBackListener != null) {
                            callBackListener.onBack(false, t.getMessage(), "");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (callBackListener != null) {
                            callBackListener.onBack(false, "数据获取完成", "");
                        }
                    }
                });
    }

    /**
     * @param context  上下文
     * @param fileName assets中的文件名字,全文见路径，例如：address.json
     */
    public String getJsonForAssets(Context context, String fileName) {
        String result = "";  // 数据的结果
        if ((context != null) && (!TextUtils.isEmpty(fileName))) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                AssetManager assetManager = context.getAssets();
                BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
                String line;
                while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
                return result;
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return result;
    }


    public void clear() {
    }
}
