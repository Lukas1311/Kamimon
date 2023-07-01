package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.storage.cache.FriendCache;
import de.uniks.stpmon.k.service.storage.cache.ICache;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendCacheTest {

    @Mock
    UserApiService userApiService;
    @Mock
    EventListener eventListener;
    @Spy
    @InjectMocks
    FriendCache cache;

    @BeforeEach
    void setUp() {
        when(eventListener.listen(any(), any(), any())).thenReturn(Observable.empty());
    }

    @Test
    void userEvents() {
        User created = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User updated = new User(
                "1",
                "Test",
                "online",
                "picture",
                new ArrayList<>());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user)));
        Subject<Event<User>> userEvents = BehaviorSubject.create();
        when(eventListener.<User>listen(eq(Socket.WS), eq("users.*.*"), any())).thenReturn(userEvents);

        // initialise cache with user
        cache.setMainUser(user).init();
        assertEquals(1, cache.getValues().blockingFirst().size());

        userEvents.onNext(new Event<>("users.1.created", created));
        verify(cache).addValue(any(User.class));
        assertEquals(2, cache.getValues().blockingFirst().size());

        userEvents.onNext(new Event<>("users.1.updated", updated));
        verify(cache).updateValue(any(User.class));
        assertEquals(2, cache.getValues().blockingFirst().size());
        Optional<User> first = cache.getValue("1");
        assertTrue(first.isPresent());
        assertEquals("online", first.get().status());

        userEvents.onNext(new Event<>("users.1.deleted", updated));
        verify(cache).removeValue(any(User.class));
        assertEquals(1, cache.getValues().blockingFirst().size());
    }

    @Test
    void friendEvents() {
        User friendFirst = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        ArrayList<String> friends = new ArrayList<>();
        friends.add(friendFirst._id());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user, friendFirst)));
        Subject<Event<User>> mainUserEvents = BehaviorSubject.create();
        when(eventListener.<User>listen(eq(Socket.WS), eq("users.*.*"), any())).thenReturn(mainUserEvents);

        // initialise cache with user
        cache.setMainUser(user).init();
        cache.onInitialized().test().assertComplete();
        assertEquals(0, cache.getFriends().blockingFirst().size());

        mainUserEvents.onNext(new Event<>("users.0.updated", new User(
                "0",
                "Test",
                "offline",
                "picture",
                friends)));
        verify(cache, times(1)).notifyUpdateFriends(any(User.class));
        assertEquals(1, cache.getFriends().blockingFirst().size());
    }

    @Test
    void getUser() {
        User friend = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user)));
        // initialise cache with user
        cache.setMainUser(user).init();
        // Check if user is cached
        Optional<User> first = cache.getValue("0");
        assertTrue(first.isPresent());
        cache.addValue(friend);
        // Check if new user is cached
        Optional<User> second = cache.getValue("1");
        assertTrue(second.isPresent());
        cache.removeValue(user);
        // Check if user is removed
        Optional<User> firstDeleted = cache.getValue("0");
        assertTrue(firstDeleted.isEmpty());
    }

    @Test
    void initAndReset() {
        User friendFirst = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User friendSecond = new User(
                "2",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        ArrayList<String> friends = new ArrayList<>();
        friends.add(friendFirst._id());
        friends.add(friendSecond._id());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                friends);

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user, friendFirst, friendSecond)));
        // initialise cache with user
        List<User> cachedFriends = cache.setMainUser(user)
                .init().getFriends()
                .blockingFirst();
        // Check if friends are cached
        assertEquals(2, cachedFriends.size());
        // Check if friends are cached correctly
        assertEquals(friendFirst, cachedFriends.get(0));
        assertEquals(friendSecond, cachedFriends.get(1));
        // Check if all users are cached
        assertEquals(3, cache.getValues().blockingFirst().size());
    }

    @Test
    void initStatusAndErrors() {
        User other = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user)));

        // Can't init cache without main user
        assertThrows(IllegalStateException.class, () -> cache.init());

        Runnable onDestroy = mock(Runnable.class);
        cache.addOnDestroy(onDestroy);
        // initialise cache with user
        List<User> cachedFriends = cache.setMainUser(user)
                .init().getFriends()
                .blockingFirst();
        // Check if main user is set correctly
        assertTrue(cache.isMainUser(user._id()));
        assertFalse(cache.isMainUser(other._id()));

        assertThrows(IllegalStateException.class, () -> cache.addOnDestroy(onDestroy));
        assertThrows(IllegalArgumentException.class, () -> cache.getValue(null));

        // Check if no friends are cached
        assertEquals(0, cachedFriends.size());
        assertEquals(ICache.Status.INITIALIZED, cache.getStatus());
        // Throw exception if cache is already initialised
        assertThrows(IllegalStateException.class, () -> cache.init());
        cache.destroy();
        verify(onDestroy, times(1)).run();
        assertThrows(IllegalStateException.class, () -> cache.getValue(""));
        assertEquals(ICache.Status.DESTROYED, cache.getStatus());
        // Check if cache is now destroyed
        assertThrows(IllegalStateException.class, () -> cache.getFriends().blockingFirst().size());
        cache.onInitialized().test().assertError(IllegalStateException.class);
    }

    @Test
    void updateFriends() {
        User friendFirst = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User friendSecond = new User(
                "2",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user, friendFirst, friendSecond)));
        // initialise cache with user
        cache.setMainUser(user);
        cache.init();
        List<User> cachedFriends = cache.getFriends().blockingFirst();
        assertEquals(0, cachedFriends.size());
        user.friends().add(friendFirst._id());
        user.friends().add(friendSecond._id());
        cachedFriends = cache.updateFriends(user).blockingFirst();
        // Check if friends are cached
        assertEquals(2, cachedFriends.size());
        // Update existing friends value
        cache.updateValue(new User(
                "1",
                "Test",
                "online",
                "picture",
                new ArrayList<>()));
        // Check if friends value is updated
        assertTrue(cache.getValue("1").isPresent());
        assertEquals("online", cache.getValue("1").get().status());
    }

    @Test
    void updateFriendMissing() {
        User friendFirst = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user)));
        when(userApiService.getUsers(anyList())).thenReturn(Observable.just(List.of(friendFirst)));
        // initialise cache with user
        List<User> cachedFriends = cache.setMainUser(user).init().getFriends().blockingFirst();
        assertEquals(0, cachedFriends.size());
        user.friends().add(friendFirst._id());
        cachedFriends = cache.updateFriends(user).blockingFirst();
        // Check if friends are cached
        assertEquals(1, cachedFriends.size());
    }

    @Test
    void addUser() {
        User friend = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user)));
        // initialise cache with user
        cache.setMainUser(user).init();
        List<User> lastUsers = new ArrayList<>();
        Disposable disposable = cache.getValues().subscribe(newUsers -> {
            lastUsers.clear();
            lastUsers.addAll(newUsers);
        });
        // Check if friends are cached
        assertEquals(1, lastUsers.size());
        cache.addValue(friend);
        // Check if friends are cached
        assertEquals(2, lastUsers.size());
        assertEquals(2, cache.getValues().blockingFirst().size());

        disposable.dispose();
    }

    @Test
    void removeUser() {
        User friend = new User(
                "1",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(friend, user)));
        // initialise cache with user
        cache.setMainUser(user).init();
        List<User> lastUsers = new ArrayList<>();
        Disposable disposable = cache.getValues().subscribe(newUsers -> {
            lastUsers.clear();
            lastUsers.addAll(newUsers);
        });
        // Check if friends are cached
        assertEquals(2, lastUsers.size());
        cache.removeValue(user);
        // Check if friends are cached
        assertEquals(1, lastUsers.size());

        disposable.dispose();
    }

}
