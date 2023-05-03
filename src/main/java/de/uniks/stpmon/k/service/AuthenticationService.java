package de.uniks.stpmon.k.service;

import javax.inject.Inject;

import de.uniks.stpmon.k.dto.*;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class AuthenticationService  {
    private final TokenStorage tokenStorage;
    private final AuthenticationApiService authApiService;
    private final UserStorage userStorage;
    // TODO: add preferences

    @Inject
    public AuthenticationService(TokenStorage tokenStorage, AuthenticationApiService authApiService, UserStorage userStorage) {
        this.tokenStorage = tokenStorage;
        this.authApiService = authApiService;
        this.userStorage = userStorage;
    }

    public Observable<LoginResult> login(String username, String password) {
        return authApiService.login(new LoginDto(username, password)).map(lr -> {
            tokenStorage.setToken(lr.accessToken());
            //Add User
            User user = new User(lr._id(), lr.name(), lr.status(), lr.avatar(), lr.friends());
            userStorage.setUser(user);
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
