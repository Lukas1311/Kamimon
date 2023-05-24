package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.Event;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.storages.FriendCache;
import de.uniks.stpmon.k.ws.EventListener;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendCacheTest {
    @Mock
    UserApiService userApiService;
    @Mock
    EventListener eventListener;
    @Spy
    @InjectMocks
    FriendCache cache;
    Subject<Event<User>> userEvents = BehaviorSubject.create();
    Subject<Event<User>> mainUserEvents = BehaviorSubject.create();

    @BeforeEach
    void setUp() {
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
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
        when(eventListener.<User>listen(eq("users.*.*"), any())).thenReturn(userEvents);

        // initialise cache with user
        List<User> cachedFriends = cache.init(user).blockingFirst();
        assertEquals(1, cache.getUsers().blockingFirst().size());

        userEvents.onNext(new Event<>("users.1.created", created));
        verify(cache).addUser(any(User.class));
        assertEquals(2, cache.getUsers().blockingFirst().size());

        userEvents.onNext(new Event<>("users.1.updated", updated));
        verify(cache).updateUser(any(User.class));
        assertEquals(2, cache.getUsers().blockingFirst().size());
        assertEquals("online", cache.getUser("1").status());

        userEvents.onNext(new Event<>("users.1.deleted", updated));
        verify(cache).removeUser(any(User.class));
        assertEquals(1, cache.getUsers().blockingFirst().size());
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
        when(eventListener.<User>listen(eq("users.*.*"), any())).thenReturn(mainUserEvents);

        // initialise cache with user
        List<User> cachedFriends = cache.init(user).blockingFirst();
        verify(cache).notifyUpdateFriends(any(User.class));
        assertEquals(0, cache.getFriends().blockingFirst().size());

        mainUserEvents.onNext(new Event<>("users.0.updated", new User(
                "0",
                "Test",
                "offline",
                "picture",
                friends)));
        verify(cache, times(2)).notifyUpdateFriends(any(User.class));
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
        List<User> cachedFriends = cache.init(user).blockingFirst();
        // Check if user is cached
        assertEquals(user, cache.getUser("0"));
        cache.addUser(friend);
        // Check if new user is cached
        assertEquals(friend, cache.getUser("1"));
        cache.removeUser(user);
        // Check if user is removed
        assertNull(cache.getUser("0"));
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
        List<User> cachedFriends = cache.init(user).blockingFirst();
        // Check if friends are cached
        assertEquals(2, cachedFriends.size());
        // Check if friends are cached correctly
        assertEquals(friendFirst, cachedFriends.get(0));
        assertEquals(friendSecond, cachedFriends.get(1));
        // Check if all users are cached
        assertEquals(3, cache.getUsers().blockingFirst().size());
        cache.reset();
        // Check if cache is not empty
        assertEquals(0, cache.getFriends().blockingFirst().size());
        assertEquals(0, cache.getUsers().blockingFirst().size());
    }

    @Test
    void initExisting() {
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(user)));
        // initialise cache with user
        List<User> cachedFriends = cache.init(user).blockingFirst();
        // Check if no friends are cached
        assertEquals(0, cachedFriends.size());
        // initialise cache with user
        cachedFriends = cache.init(user).blockingFirst();
        // Check if no friends are cached
        assertEquals(0, cachedFriends.size());
        // check if cache was reset between initialisations
        verify(cache).reset();
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
        List<User> cachedFriends = cache.init(user).blockingFirst();
        assertEquals(0, cachedFriends.size());
        user.friends().add(friendFirst._id());
        user.friends().add(friendSecond._id());
        cachedFriends = cache.updateFriends(user).blockingFirst();
        // Check if friends are cached
        assertEquals(2, cachedFriends.size());
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
        List<User> cachedFriends = cache.init(user).blockingFirst();
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

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of(friend)));
        // initialise cache with user
        List<User> cachedFriends = cache.init(user).blockingFirst();
        List<User> lastUsers = new ArrayList<>();
        Disposable disposable = cache.getUsers().subscribe(newUsers -> {
            lastUsers.clear();
            lastUsers.addAll(newUsers);
        });
        // Check if friends are cached
        assertEquals(1, lastUsers.size());
        cache.addUser(user);
        // Check if friends are cached
        assertEquals(2, lastUsers.size());
        assertEquals(2, cache.getUsers().blockingFirst().size());

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
        List<User> cachedFriends = cache.init(user).blockingFirst();
        List<User> lastUsers = new ArrayList<>();
        Disposable disposable = cache.getUsers().subscribe(newUsers -> {
            lastUsers.clear();
            lastUsers.addAll(newUsers);
        });
        // Check if friends are cached
        assertEquals(2, lastUsers.size());
        cache.removeUser(user);
        // Check if friends are cached
        assertEquals(1, lastUsers.size());

        disposable.dispose();
    }
}
