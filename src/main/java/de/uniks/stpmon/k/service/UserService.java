package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserService {

    private final UserStorage userStorage;
    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService, UserStorage userStorage) {
        this.userApiService = userApiService;
        this.userStorage = userStorage;
    }

    public Observable<User> addUser(String username, String password) {
        return userApiService.addUser(
                new CreateUserDto(username, null, password)
        );
    }

    public Observable<User> setUsername(String username) {
        User oldUser = userStorage.getUser();
        User newUser = new User(oldUser._id(), username, oldUser.status(), oldUser.avatar(), oldUser.friends());
        userStorage.setUser(newUser);
        UpdateUserDto dto = new UpdateUserDto(oldUser.name(), null, null, null, null);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    public Observable<User> setPassword(String password) {
        User oldUser = userStorage.getUser();
        UpdateUserDto dto = new UpdateUserDto(null, null, null, null, password);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    public Observable<User> setAvatar(String avatar) {
        User oldUser = userStorage.getUser();
        User newUser = new User(oldUser._id(), oldUser.name(), oldUser.status(), avatar, oldUser.friends());
        userStorage.setUser(newUser);
        UpdateUserDto dto = new UpdateUserDto(oldUser.name(), null, avatar, null, null);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    public Observable<List<User>> searchFriend(String name) {
        final User user = userStorage.getUser();
        return userApiService.getUsers().map(e -> e.stream().filter(f -> f.name().toLowerCase().startsWith(name.toLowerCase()) && !f._id().equals(user._id())).filter(g -> !user.friends().contains(g._id())).toList());
    }

    public Observable<List<User>> addFriend(User friend) {
        final User user = userStorage.getUser();
        HashSet<String> friendList = new HashSet<>(user.friends());
        if (!friendList.add(friend._id())) {
            return Observable.empty();
        }
        return updateFriendList(user, friendList);
    }

    public Observable<List<User>> removeFriend(User friend) {
        final User user = userStorage.getUser();
        HashSet<String> friendList = new HashSet<>(user.friends());
        if (!friendList.remove(friend._id())) {
            return Observable.empty();
        }
        return updateFriendList(user, friendList);
    }

    private Observable<List<User>> updateFriendList(User user, HashSet<String> friendList) {
        UpdateUserDto dto = new UpdateUserDto(null, null, null, new ArrayList<>(friendList), null);
        return userApiService.updateUser(user._id(), dto).map(e -> {
            userStorage.setUser(e);
            if (e.friends().isEmpty()) {
                return Observable.<List<User>>fromSupplier(ArrayList::new);
            }
            return userApiService.getUsers(e.friends());
        }).concatMap(f -> f);
    }

    public Observable<List<User>> getFriends() {
        if (userStorage.getUser().friends().isEmpty()) {
            return Observable.fromSupplier(ArrayList::new);
        }
        return userApiService.getUsers(userStorage.getUser().friends());
    }
}
