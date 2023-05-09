package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

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
    }

    @Test
    void setPassword() {
    }

    @Test
    void setAvatar() {
    }

    @Test
    void searchFriend() {
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