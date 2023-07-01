package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.models.User;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface UserApiService {

    @POST("users")
    Observable<User> addUser(@Body CreateUserDto dto);

    @GET("users")
    Observable<List<User>> getUsers();

    @GET("users")
    Observable<List<User>> getUsers(@Query("ids") List<String> ids);

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") String id);

    @PATCH("users/{id}")
    Observable<User> updateUser(@Path("id") String id, @Body UpdateUserDto dto);

    @DELETE("users/{id}")
    Observable<User> deleteUser(@Path("id") String id);

}
