package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.rest.AuthenticationApiService;

import javax.inject.Inject;

public class AuthenticationService  {
    private final TokenStorage tokenStorage;
    private final AuthenticationApiService authApiService;

    @Inject
    public AuthenticationService(TokenStorage tokenStorage, AuthenticationApiService authApiService) {
        this.tokenStorage = tokenStorage;
        this.authApiService = authApiService;
    }

    public Observable<LoginResult> login(String username, String password) {
        return authApiService.login(new LoginDto(username, password)),map(lr -> {
            tokenStorage.setToken(lr.accessToken());
            return lr;
        })
    }
}
