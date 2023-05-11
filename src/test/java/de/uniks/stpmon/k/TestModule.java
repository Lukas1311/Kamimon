package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.ErrorResponse;
import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.LoginResult;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.EventListener;
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
                return Observable.empty();
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




}
