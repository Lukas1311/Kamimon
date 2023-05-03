package de.uniks.stpmon.k.service;

import javax.inject.Inject;

import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.LoginResult;
import de.uniks.stpmon.k.dto.ErrorResponse;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class AuthenticationService  {
    private final TokenStorage tokenStorage;
    private final AuthenticationApiService authApiService;
    // TODO: add preferences

    @Inject
    public AuthenticationService(TokenStorage tokenStorage, AuthenticationApiService authApiService) {
        this.tokenStorage = tokenStorage;
        this.authApiService = authApiService;
    }

    public Observable<LoginResult> login(String username, String password) {
        return authApiService.login(new LoginDto(username, password)).map(lr -> {
            tokenStorage.setToken(lr.accessToken());
            return lr;
        });
    }

    public Observable<Response<ErrorResponse>> logout() {
        return authApiService.logout().map(res -> {
            return res;
        });
    }

    // TODO: implement isRememberMe

    public Observable<LoginResult> refresh() {
        // TODO: add refresh token from preferences
        return authApiService.refresh(new RefreshDto(null) ).map(lr -> {
            tokenStorage.setToken(lr.accessToken());
            return lr;
        });
    }
}
