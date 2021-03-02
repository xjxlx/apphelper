package android.helper.interfaces;

import retrofit2.Call;
import retrofit2.Response;

public interface ResponseCallBackListener<T> {
    void onSuccess(Call<T> call, Response<T> response, T t);

    void onError(Call<T> call, Throwable t);
}
