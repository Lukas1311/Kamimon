package de.uniks.stpmon.k.utils;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class ExceptionHelper {

    public static <@NonNull T> Observable<T> justHttp(int code) {
        return justHttp(code, "test");
    }

    public static <@NonNull T> Observable<T> justHttp(int code, String message) {
        Response<Object> response = Response.error(code, ResponseBody.create(null, message));
        return Observable.error(new HttpException(response));
    }

}
