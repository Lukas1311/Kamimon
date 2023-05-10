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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Spy
    UserStorage userStorage;
    @Mock
    UserApiService userApiService;
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
        assertNull(nullUser);

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
        assertNull(nullUser);

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
        assertNull(nullUser);

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
    void searchFriend() {
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
    void addFriend() {
    }

    @Test
    void removeFriend() {
    }

    @Test
    void getFriends() {
    }

    @Test
    void filterFriends() {
    }
}