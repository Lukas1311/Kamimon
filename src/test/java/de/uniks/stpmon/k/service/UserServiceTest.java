package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Spy
    UserStorage userStorage;
    @Mock
    UserApiService userApiService;
    @InjectMocks
    IFriendCache friendCache = new FriendCacheDummy();
    @Mock
    Provider<IFriendCache> friendCacheProvider;
    @InjectMocks
    UserService userService;

    @Test
    void addUser() {
        //define mocks
        final ArgumentCaptor<CreateUserDto> captor = ArgumentCaptor.forClass(CreateUserDto.class);
        when(userApiService.addUser(ArgumentMatchers.any(CreateUserDto.class)))
                .thenReturn(Observable.just(new User(
                        "0",
                        "Test",
                        "offline",
                        null,
                        new ArrayList<>()
                )));

        //action
        final User newUser = userService.addUser("Test", "testtest").blockingFirst();

        //check values
        assertEquals("0", newUser._id());
        assertEquals("Test", newUser.name());
        assertEquals("offline", newUser.status());
        assertNull(newUser.avatar());
        assertEquals(new ArrayList<String>(), newUser.friends());

        //check mocks
        verify(userApiService).addUser(captor.capture());
    }

    @Test
    void setUsername() {
        //test when oldUser is null
        final Observable<User> nullUser = userService.setUsername("Test2");
        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //setting up user which will be updated
        User oldUser = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(oldUser);

        //define mock
        final ArgumentCaptor<UpdateUserDto> captor = ArgumentCaptor.forClass(UpdateUserDto.class);
        when(userApiService.updateUser(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(Observable.just(
                        new User(
                                "0",
                                "Test2",
                                "offline",
                                "picture",
                                new ArrayList<>())
                ));

        //action
        final User newUser = userService.setUsername("Test2").blockingFirst();

        //check values
        assertEquals("Test2", newUser.name());

        //check mock
        verify(userApiService).updateUser(ArgumentMatchers.anyString(), captor.capture());
    }

    @Test
    void setPassword() {
        //test when oldUser is null
        final Observable<User> nullUser = userService.setPassword("testtest2");

        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //setting up user which will be updated
        User oldUser = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(oldUser);

        //define mock
        final ArgumentCaptor<UpdateUserDto> captor = ArgumentCaptor.forClass(UpdateUserDto.class);
        when(userApiService.updateUser(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(Observable.just(
                        new User(
                                "0",
                                "Test",
                                "offline",
                                "picture",
                                new ArrayList<>())
                ));

        //action
        final User newUser = userService.setPassword("testtest2").blockingFirst();

        //check values
        assertEquals(oldUser, newUser);

        //check mock
        verify(userApiService).updateUser(ArgumentMatchers.anyString(), captor.capture());
    }

    @Test
    void setAvatar() {
        //test when oldUser is null
        final Observable<User> nullUser = userService.setAvatar("picture2");
        //check value
        assertTrue(nullUser.isEmpty().blockingGet());

        //setting up user which will be updated
        User oldUser = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(oldUser);

        //define mock
        final ArgumentCaptor<UpdateUserDto> captor = ArgumentCaptor.forClass(UpdateUserDto.class);
        when(userApiService.updateUser(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(Observable.just(
                        new User(
                                "0",
                                "Test",
                                "offline",
                                "picture2",
                                new ArrayList<>())
                ));

        //action
        final User newUser = userService.setAvatar("picture2").blockingFirst();

        //check values
        assertEquals("picture2", newUser.avatar());

        //check mock
        verify(userApiService).updateUser(ArgumentMatchers.anyString(), captor.capture());
    }

    @Test
    void searchFriendEmpty() {
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(user);

        when(userApiService.getUsers()).thenReturn(Observable.just(List.of()));
        when(friendCacheProvider.get()).thenReturn(friendCache);
        //action
        List<User> emptyList = userService.searchFriend("").blockingFirst();

        //check values
        assertTrue(emptyList.isEmpty());
    }

    @Test
    void searchFriend() {
        when(friendCacheProvider.get()).thenReturn(friendCache);
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(user);

        //define mock
        List<User> usersFromServer = new ArrayList<>();
        //current user (that should not be shown)
        usersFromServer.add(new User("0", "a", null, null, null));
        // some other user
        usersFromServer.add(new User("1", "a", null, null, null));
        when(userApiService.getUsers()).thenReturn(Observable.just(usersFromServer));

        //action
        final List<User> users = userService.searchFriend("a").blockingFirst();

        //check values
        assertEquals(1, users.size());
        assertEquals("1", users.get(0)._id());

        //check mock
        verify(userApiService).getUsers();
    }

    @Test
    void addFriendAlreadyInFriendList() {
        User friend = new User(
                "1",
                "Test2",
                "offline",
                "picture",
                new ArrayList<>());
        ArrayList<String> friends = new ArrayList<>();
        friends.add(friend._id());
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                friends);
        userStorage.setUser(user);

        //action
        Observable<List<User>> updatedFriends = userService.addFriend(friend);

        //check values
        assertTrue(updatedFriends.isEmpty().blockingGet());

    }

    @Test
    void addNewFriend() {

        when(friendCacheProvider.get()).thenReturn(friendCache);
        User friend = new User(
                "1",
                "Test2",
                "offline",
                "picture",
                new ArrayList<>());
        ArrayList<String> friends = new ArrayList<>();
        friends.add(friend._id());
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(user);

        //define mocks
        final ArgumentCaptor<UpdateUserDto> updateArgumentCaptor = ArgumentCaptor.forClass(UpdateUserDto.class);
        @SuppressWarnings("unchecked") final ArgumentCaptor<List<String>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        when(userApiService.updateUser(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(Observable.just(
                        new User(
                                "0",
                                "Test2",
                                "offline",
                                "picture",
                                friends)
                ));

        ArrayList<User> userFriends = new ArrayList<>();
        userFriends.add(friend);
        when(userApiService.getUsers(ArgumentMatchers.any()))
                .thenReturn(Observable.just(userFriends));

        //action
        List<User> updatedFriends = userService.addFriend(friend).blockingFirst();

        //check values
        assertEquals(1, updatedFriends.size());
        assertEquals(friend, updatedFriends.get(0));

        //check mock
        verify(userApiService).updateUser(ArgumentMatchers.anyString(), updateArgumentCaptor.capture());
        verify(userApiService).getUsers(listArgumentCaptor.capture());
    }

    @Test
    void removeFriendNotInFriendList() {
        User friend = new User(
                "1",
                "Test2",
                "offline",
                "picture",
                new ArrayList<>());
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(user);

        //action
        Observable<List<User>> updatedFriends = userService.removeFriend(friend);

        //check values
        assertTrue(updatedFriends.isEmpty().blockingGet());
    }

    @Test
    void removeFriendFromFriendList() {
        User friend = new User(
                "1",
                "Test2",
                "offline",
                "picture",
                new ArrayList<>());
        ArrayList<String> friends = new ArrayList<>();
        friends.add(friend._id());
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                friends);
        userStorage.setUser(user);

        //define mocks
        final ArgumentCaptor<UpdateUserDto> updateArgumentCaptor = ArgumentCaptor.forClass(UpdateUserDto.class);

        when(userApiService.updateUser(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(Observable.just(
                        new User(
                                "0",
                                "Test2",
                                "offline",
                                "picture",
                                new ArrayList<>())
                ));

        //action
        List<User> updatedFriends = userService.removeFriend(friend).blockingFirst();

        //check values
        assertEquals(0, updatedFriends.size());

        //check mock
        verify(userApiService).updateUser(ArgumentMatchers.anyString(), updateArgumentCaptor.capture());
    }

    @Test
    void getFriends() {
        when(friendCacheProvider.get()).thenReturn(friendCache);
        User friend = new User(
                "1",
                "Test2",
                "offline",
                "picture",
                new ArrayList<>());
        ArrayList<String> friends = new ArrayList<>();
        friends.add(friend._id());
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                friends);
        userStorage.setUser(user);
        ArrayList<User> friendsAsUser = new ArrayList<>();
        friendsAsUser.add(friend);
        //define mocks
        @SuppressWarnings("unchecked") final ArgumentCaptor<List<String>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        when(userApiService.getUsers(ArgumentMatchers.any()))
                .thenReturn(Observable.just(friendsAsUser));

        //action
        List<User> friendlist = userService.getFriends().blockingFirst();

        //check values
        assertEquals(1, friendlist.size());
        assertEquals("1", friendlist.get(0)._id());
        //check mock
        verify(userApiService).getUsers(listArgumentCaptor.capture());
    }

    @Test
    void getEmptyFriends() {

        when(friendCacheProvider.get()).thenReturn(friendCache);
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                new ArrayList<>());
        userStorage.setUser(user);

        //action
        List<User> userFriends = userService.getFriends().blockingFirst();

        //check values
        assertEquals(new ArrayList<>(), userFriends);

    }

    @Test
    void filterFriends() {

        when(friendCacheProvider.get()).thenReturn(friendCache);
        User friend1 = new User(
                "1",
                "Test2",
                "offline",
                "picture",
                new ArrayList<>());

        User friend2 = new User(
                "1",
                "est",
                "offline",
                "picture",
                new ArrayList<>());

        ArrayList<String> friends = new ArrayList<>();
        friends.add(friend1._id());
        friends.add(friend2._id());
        //setting up user which will be updated
        User user = new User(
                "0",
                "Test",
                "offline",
                "picture",
                friends);
        userStorage.setUser(user);
        ArrayList<User> friendsAsUser = new ArrayList<>();
        friendsAsUser.add(friend1);
        friendsAsUser.add(friend2);


        @SuppressWarnings("unchecked") final ArgumentCaptor<List<String>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

        when(userApiService.getUsers(ArgumentMatchers.any()))
                .thenReturn(Observable.just(friendsAsUser));


        //action
        List<User> friendlist = userService.filterFriends("T").blockingFirst();

        //check values
        assertEquals(1, friendlist.size());
        assertEquals("1", friendlist.get(0)._id());


        //check mock
        verify(userApiService).getUsers(listArgumentCaptor.capture());
    }
}