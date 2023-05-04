package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final UserStorage userStorage;
    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService, UserStorage userStorage){
        this.userApiService = userApiService;
        this.userStorage = userStorage;
    }

    public Observable<User> addUser(String username, String password){
        return userApiService.addUser(
                new CreateUserDto(username, null, password)
        );
    }

    public Observable<User> setUsername(String username){
        //TODO: Wait for ServerResponse before changing the username in UserStorage (new Username could be invalid)
        User oldUser = userStorage.getUser();
        User newUser = new User(oldUser._id(), username, oldUser.status(), oldUser.avatar(), oldUser.friends());
        userStorage.setUser(newUser);
        UpdateUserDto dto = new UpdateUserDto(oldUser.name(), null, null, null, null);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    public Observable<User> setPassword(String password){
        User oldUser = userStorage.getUser();
        UpdateUserDto dto = new UpdateUserDto(null, null, null, null, password);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    public Observable<User> setAvatar(String avatar){
        User oldUser = userStorage.getUser();
        User newUser = new User(oldUser._id(), oldUser.name(), oldUser.status(), avatar, oldUser.friends());
        UpdateUserDto dto = new UpdateUserDto(oldUser.name(), null, avatar, null, null);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    public Observable<List<User>> searchFriend(String name) {
        final User user = userStorage.getUser();
        return userApiService.getUsers().map(e -> e.stream().filter(f ->  f.name().toLowerCase().startsWith(name.toLowerCase()) || !f._id().equals(user._id())).toList());
    }

    public Observable<User> addFriend(User friend) {
        final User user = userStorage.getUser();
        ArrayList<String> friendList = new ArrayList<>(user.friends());
        friendList.add(friend._id());
        UpdateUserDto dto = new UpdateUserDto(null, null, null, friendList, null);
        return userApiService.updateUser(user._id(), dto);
    }
}
