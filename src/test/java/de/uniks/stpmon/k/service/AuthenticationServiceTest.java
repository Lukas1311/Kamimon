package de.uniks.stpmon.k.service;


import de.uniks.stpmon.k.dto.ErrorResponse;
import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.LoginResult;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Response;

import java.util.List;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Spy
    TokenStorage tokenStorage;
    @Spy
    UserStorage userStorage;
    @Mock
    AuthenticationApiService authApiService;
    @Mock
    IFriendCache friendCache;
    @Mock
    Preferences prefs;

    @InjectMocks
    AuthenticationService authService;

    @Test
    void testLogin() {
        // define mocks:
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(prefs).put(ArgumentMatchers.eq("refreshToken"), captor.capture());
        when(friendCache.init(any())).thenReturn(Observable.just(List.of()));
        Mockito.when(authApiService.login(any()))
                .thenReturn(Observable.just(new LoginResult("i", "n", "s", "a", null, "a", "r")));

        // action:
        final LoginResult result = authService.login("Alice", "12345678", true).blockingFirst();

        // check values:
        assertEquals("a", result.accessToken());
        assertEquals("a", tokenStorage.getToken());
        assertEquals("r", result.refreshToken());
        assertEquals("r", captor.getValue());
        assertEquals(new User("i", "n", "s", "a", null), userStorage.getUser());

        // check mocks:
        verify(prefs).put("refreshToken", "r");
        verify(authApiService).login(new LoginDto("Alice", "12345678"));
    }

    @Test
    void testIsRememberMe() {
        // define mocks:
        Mockito.when(prefs.get("refreshToken", null)).thenReturn("r");

        // action:
        boolean result = authService.isRememberMe();

        // check values:
        assertTrue(result);

        // check mocks:
        verify(prefs).get("refreshToken", null);
    }

    @Test
    void testLogout() {
        // define mocks:
        Mockito.when(authApiService.logout()).thenReturn(
                Observable.just(Response.success(new ErrorResponse(200, "e", "m")))
        );

        // action:
        final Response<ErrorResponse> response = authService.logout().blockingFirst();

        // check values:
        assertEquals(200, response.code());
        assertEquals("e", response.body().error());
        assertEquals("m", response.body().message());

        // check mocks:
        verify(authApiService).logout();
    }

    @Test
    void testRefresh() {
        // define mocks:
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.doCallRealMethod().when(tokenStorage).setToken(captor.capture());
        when(friendCache.init(any())).thenReturn(Observable.just(List.of()));
        Mockito.when(authApiService.refresh(any()))
                .thenReturn(Observable.just(new LoginResult("i", "n", "s", "a", null, "a", "r")));
        Mockito.when(prefs.get("refreshToken", null)).thenReturn("r"); // mock the pref get call

        // action:
        final LoginResult result = authService.refresh().blockingFirst();

        // check values:
        assertEquals("a", result.accessToken());
        assertEquals("a", tokenStorage.getToken());
        assertEquals("a", captor.getValue());
        assertEquals(new User("i", "n", "s", "a", null), userStorage.getUser());

        // check mocks:
        verify(tokenStorage).setToken("a");
        verify(authApiService).refresh(new RefreshDto("r"));
    }
}