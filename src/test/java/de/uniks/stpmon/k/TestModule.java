package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.*;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import de.uniks.stpmon.k.rest.RegionApiService;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static org.mockito.Mockito.mock;

@Module
public class TestModule {

    @Provides
    static Preferences prefs() {
        return mock(Preferences.class);
    }

    @Provides
    static ResourceBundle resources() {
        return ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    }

    @Provides
    @Singleton
    static EventListener eventListener() {
        return mock(EventListener.class);
    }

    @Provides
    static AuthenticationApiService authApiService() {
        return new AuthenticationApiService() {
            @Override
            public Observable<LoginResult> login(LoginDto dto) {
                return Observable.just(new LoginResult(
                        "0",
                        "Test",
                        "offline",
                        "picture",
                        new ArrayList<>(),
                        "accessToken",
                        "refreshToken"));
            }

            @Override
            public Observable<Response<ErrorResponse>> logout() {
                return Observable.just(Response.success(null));
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto refreshToken) {
                return Observable.just(
                        new LoginResult(
                                "0",
                                "Test",
                                "offline",
                                "picture",
                                new ArrayList<>(),
                                "accessToken",
                                "refreshToken"));
            }
        };
    }


    @Provides
    static UserApiService userApiService() {
        return new UserApiService() {
            @Override
            public Observable<User> addUser(CreateUserDto dto) {
                return Observable.empty();
            }

            @Override
            public Observable<List<User>> getUsers() {
                return Observable.empty();
            }

            @Override
            public Observable<List<User>> getUsers(List<String> ids) {
                return Observable.empty();
            }

            @Override
            public Observable<User> getUser(String id) {
                return Observable.empty();
            }

            @Override
            public Observable<User> updateUser(String id, UpdateUserDto dto) {
                return Observable.empty();
            }

            @Override
            public Observable<User> deleteUser(String id) {
                return Observable.empty();
            }
        };
    }

    @Provides
    static RegionApiService regionApiService() {
        return new RegionApiService() {
            @Override
            public Observable<List<Region>> getRegions() {
                return Observable.just(List.of(new Region("", "", "1", "TestRegion")));
            }

            @Override
            public Observable<Region> getRegion(String id) {
                return Observable.empty();
            }
        };
    }
}
