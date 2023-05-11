package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.*;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import de.uniks.stpmon.k.rest.MessageApiService;
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


    @Provides
    static MessageApiService messageApiService() {
        return new MessageApiService() {
            @Override
            public Observable<Message> sendMessage(String namespace, String parent, CreateMessageDto msg) {
                return null;
            }

            @Override
            public Observable<ArrayList<Message>> getMessages(String namespace, String parent, String dateTimeAfter, String dateTimeBefore, Integer limit) {
                return null;
            }

            @Override
            public Observable<Message> getMessage(String namespace, String parent, String id) {
                return null;
            }

            @Override
            public Observable<Message> editMessage(String namespace, String parent, String id, UpdateMessageDto msg) {
                return null;
            }

            @Override
            public Observable<Message> deleteMessage(String namespace, String parent, String id) {
                return null;
            }
        };
    }

}
