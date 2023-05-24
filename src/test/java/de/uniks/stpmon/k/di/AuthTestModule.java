package de.uniks.stpmon.k.di;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.models.ErrorResponse;
import de.uniks.stpmon.k.models.LoginResult;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import javax.inject.Singleton;
import java.util.ArrayList;

@Module
public class AuthTestModule {

    @Provides
    @Singleton
    static AuthenticationApiService authApiService() {
        return new AuthenticationApiService() {
            LoginResult loginResult;
            @Override
            public Observable<LoginResult> login(LoginDto dto) {
                loginResult = new LoginResult(
                        "0",
                        dto.name(),
                        "offline",
                        "picture",
                        new ArrayList<>(),
                        "accessToken",
                        "refreshToken");
                return Observable.just(loginResult);
            }

            @Override
            public Observable<Response<ErrorResponse>> logout() {
                return Observable.just(Response.success(null));
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto refreshToken) {
                return Observable.just(loginResult);
            }
        };
    }
}
