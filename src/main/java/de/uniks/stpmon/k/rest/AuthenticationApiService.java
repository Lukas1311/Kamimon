package de.uniks.stpmon.k.rest;

import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.models.ErrorResponse;
import de.uniks.stpmon.k.models.LoginResult;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationApiService {

    // Log in with user credentials.
    @POST("auth/login")
    Observable<LoginResult> login(@Body LoginDto dto);

    // Logs out the current user by invalidating the refresh token.
    @POST("auth/logout")
    Observable<Response<ErrorResponse>> logout();

    // Log in with a refresh token.
    @POST("auth/refresh")
    Observable<LoginResult> refresh(@SuppressWarnings("unused") @Body RefreshDto refreshToken);

}
