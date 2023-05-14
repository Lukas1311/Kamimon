package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.*;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.prefs.Preferences;

public class AuthenticationService {
    private final TokenStorage tokenStorage;
    private final AuthenticationApiService authApiService;
    private final UserStorage userStorage;
    private final FriendCache friendCache;
    private final Preferences preferences;

    @Inject
    public AuthenticationService(TokenStorage tokenStorage, AuthenticationApiService authApiService, UserStorage userStorage, Preferences preferences, FriendCache friendCache) {
        this.tokenStorage = tokenStorage;
        this.authApiService = authApiService;
        this.userStorage = userStorage;
        this.preferences = preferences;
        this.friendCache = friendCache;
    }

    public Observable<LoginResult> login(String username, String password, boolean rememberMe) {
        return authApiService.login(new LoginDto(username, password)).map(lr -> {
            tokenStorage.setToken(lr.accessToken());
            if (rememberMe) {
                preferences.put("refreshToken", lr.refreshToken());
            }
            //Add User to UserStorage
            userStorage.setUser(new User(lr._id(), lr.name(), lr.status(), lr.avatar(), lr.friends()));
            return lr;
        }).concatMap(old -> friendCache.init(userStorage.getUser()).map((ignore) -> old));
    }


    public Observable<Response<ErrorResponse>> logout() {
        return authApiService.logout().map(res -> {
            friendCache.reset();
            return res;
        });
    }

    public boolean isRememberMe() {
        return preferences.get("refreshToken", null) != null;
    }

    public Observable<LoginResult> refresh() {
        return authApiService.refresh(new RefreshDto(preferences.get("refreshToken", null))).map(lr -> {
            tokenStorage.setToken(lr.accessToken());
            userStorage.setUser(new User(lr._id(), lr.name(), lr.status(), lr.avatar(), lr.friends()));
            return lr;
        }).concatMap(old -> friendCache.init(userStorage.getUser()).map((ignore) -> old));
    }
}
