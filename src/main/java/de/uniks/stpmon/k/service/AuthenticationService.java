package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.ErrorResponse;
import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.LoginResult;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.prefs.Preferences;

public class AuthenticationService {
    @Inject
    TokenStorage tokenStorage;
    @Inject
    AuthenticationApiService authApiService;
    @Inject
    UserStorage userStorage;
    @Inject
    IFriendCache friendCache;
    @Inject
    Preferences preferences;

    @Inject
    public AuthenticationService() {
    }

    public Observable<LoginResult> login(String username, String password, boolean rememberMe) {
        return authApiService.login(new LoginDto(username, password)).map(lr -> {
            tokenStorage.setToken(lr.accessToken());
            if (rememberMe) {
                preferences.put("refreshToken", lr.refreshToken());
            }
            //Add User to UserStorage
            userStorage.setUser(new User(lr.createdAt(), lr.updatedAt(), lr._id(), lr.name(), lr.status(), lr.avatar(), lr.friends()));
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
            userStorage.setUser(new User(lr.createdAt(), lr.updatedAt(), lr._id(), lr.name(), lr.status(), lr.avatar(), lr.friends()));
            return lr;
        }).concatMap(old -> friendCache.init(userStorage.getUser()).map((ignore) -> old));
    }
}
