package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.dto.CreateUserDto;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.ArrayList;

public interface UserApiService {
    @POST("users")
    Observable<User> addUser(@Body CreateUserDto dto);
    @GET("users")
    Observable<ArrayList<User>> getUsers();
    @GET("users")
    Observable<ArrayList<User>> getUsers(@Query("ids") ArrayList<String> ids);
    @GET("users/{id}")
    Observable<User> getUser(@Path("id") String id);
    @PATCH("users/{id}")
    Observable<User> updateUser(@Path("id") String id, @Body UpdateUserDto dto);
    @DELETE("users/{id}")
    Observable<User> deleteUser(@Path("id") String id);
}
