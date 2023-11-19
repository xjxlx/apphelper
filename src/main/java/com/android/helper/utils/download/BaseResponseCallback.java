package com.android.helper.utils.download;

import com.android.helper.interfaces.ResponseCallBackListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseResponseCallback<T> implements Callback<T>, ResponseCallBackListener<T> {

    @Override
    public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
        boolean successful = response.isSuccessful();
        if (successful) {
            T body = response.body();
            onSuccess(call, response, body);
        } else {
            String message = response.message();
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 返回一个错误
            onError(call, new Exception(message));
        }
    }

    @Override
    public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
        onError(call, t);
    }
}
