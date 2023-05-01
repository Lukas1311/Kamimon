package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.LoginResult;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationApiService {
    @POST("auth/login")
    Observable<LoginResult> login(@Body LoginDto dto);

    // Logs out the current user by invalidating the refresh token.
    @POST("auth/logout")
    Observable<LoginResult> logout();
}
