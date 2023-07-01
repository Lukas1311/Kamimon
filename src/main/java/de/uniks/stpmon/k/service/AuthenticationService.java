package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.models.ErrorResponse;
import de.uniks.stpmon.k.models.LoginResult;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import de.uniks.stpmon.k.service.storage.TokenStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;
import de.uniks.stpmon.k.service.world.PreparationService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.prefs.Preferences;

import static de.uniks.stpmon.k.service.UserService.OnlineStatus;


@Singleton
public class AuthenticationService {

    @Inject
    TokenStorage tokenStorage;
    @Inject
    AuthenticationApiService authApiService;
    @Inject
    UserService userService;
    @Inject
    UserStorage userStorage;
    @Inject
    CacheManager cacheManager;
    @Inject
    Preferences preferences;
    @Inject
    PreparationService preparationService;
    // Do not inject this, it is retrieved from cacheManager
    private IFriendCache friendCache;

    @Inject
    public AuthenticationService() {
    }

    private Observable<LoginResult> setupCache(LoginResult old, User user) {
        friendCache = cacheManager.requestFriends(user._id());
        //init cache
        return friendCache
                // wait for cache to be initialized
                .onInitialized()
                .andThen(preparationService.prepareLobby())
                // return old login result
                .andThen(Observable.just(old));

    }

    public Observable<LoginResult> login(String username, String password, boolean rememberMe) {
        return authApiService.login(new LoginDto(username, password)).flatMap(lr -> {
            tokenStorage.setToken(lr.accessToken());
            if (rememberMe) {
                preferences.put("refreshToken", lr.refreshToken());
            }
            //Add User to UserStorage
            userStorage.setUser(new User(lr._id(), lr.name(), lr.status(), lr.avatar(), lr.friends()));
            return userService.updateStatus(OnlineStatus.ONLINE).map(res -> lr);
        }).concatMap(old -> setupCache(old, userStorage.getUser()));
    }

    public Observable<Response<ErrorResponse>> logout() {
        return authApiService.logout().flatMap(res -> {
            if (friendCache != null) {
                friendCache.destroy();
                friendCache = null;
            }
            return userService.updateStatus(OnlineStatus.OFFLINE).map(res2 -> res);
        });
    }

    public boolean isRememberMe() {
        return preferences.get("refreshToken", null) != null;
    }

    public Observable<LoginResult> refresh() {
        return authApiService.refresh(new RefreshDto(preferences.get("refreshToken", null))).flatMap(lr -> {
            tokenStorage.setToken(lr.accessToken());
            userStorage.setUser(new User(lr._id(), lr.name(), lr.status(), lr.avatar(), lr.friends()));
            return userService.updateStatus(OnlineStatus.ONLINE).map(res -> lr);
        }).concatMap(old -> setupCache(old, userStorage.getUser()));
    }

}
