package de.uniks.stpmon.k.service;


import de.uniks.stpmon.k.dto.LoginDto;
import de.uniks.stpmon.k.dto.RefreshDto;
import de.uniks.stpmon.k.models.ErrorResponse;
import de.uniks.stpmon.k.models.LoginResult;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import de.uniks.stpmon.k.service.UserService.OnlineStatus;
import de.uniks.stpmon.k.service.dummies.CacheManagerDummy;
import de.uniks.stpmon.k.service.dummies.FriendCacheDummy;
import de.uniks.stpmon.k.service.storage.TokenStorage;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.world.PreparationService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Response;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Spy
    TokenStorage tokenStorage;
    @Spy
    UserStorage userStorage;
    @Mock
    UserService userService;
    @Mock
    AuthenticationApiService authApiService;
    @Spy
    final CacheManager cacheManager = new CacheManagerDummy();
    @Mock
    Preferences prefs;

    @InjectMocks
    AuthenticationService authService;
    @Mock
    PreparationService preparationService;

    @Test
    void testLogin() {
        // define mocks:
        when(preparationService.prepareLobby()).thenReturn(Completable.complete());
        CacheManagerDummy.init(cacheManager, FriendCacheDummy::new);
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(prefs).put(ArgumentMatchers.eq("refreshToken"), captor.capture());
        Mockito.when(authApiService.login(any()))
                .thenReturn(Observable.just(new LoginResult("i", "n", "s", "a", null, "a", "r")));
        when(userService.updateStatus(any())).thenReturn(Observable.just(
                new User("1", "b", "online", "a", null)
        ));
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
        verify(userService).updateStatus(any());
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
        when(userService.updateStatus(any())).thenReturn(Observable.just(
                new User("1", "b", "online", "a", null)
        ));

        // action:
        final Response<ErrorResponse> response = authService.logout().blockingFirst();

        // check values:
        assertEquals(200, response.code());
        assertNotNull(response.body());
        assertEquals("e", response.body().error());
        assertEquals("m", response.body().message());

        // check mocks:
        verify(authApiService).logout();
        verify(userService).updateStatus(any());
    }

    @Test
    void testRefresh() {
        // define mocks:
        CacheManagerDummy.init(cacheManager, FriendCacheDummy::new);
        when(preparationService.prepareLobby()).thenReturn(Completable.complete());
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doCallRealMethod().when(tokenStorage).setToken(captor.capture());
        when(authApiService.refresh(any()))
                .thenReturn(Observable.just(new LoginResult("i", "n", "s", "a", null, "a", "r")));
        when(prefs.get("refreshToken", null)).thenReturn("r"); // mock the pref get call
        when(userService.updateStatus(any())).thenReturn(Observable.just(
                new User("1", "b", "online", "a", null)
        ));

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
        final ArgumentCaptor<OnlineStatus> statusCaptor = ArgumentCaptor.forClass(OnlineStatus.class);

        verify(userService).updateStatus(statusCaptor.capture());
        assertEquals(OnlineStatus.ONLINE, statusCaptor.getValue());
    }

}
