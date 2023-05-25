package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.storage.IFriendCache;
import de.uniks.stpmon.k.service.storage.UserStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class UserService {

    @Inject
    UserStorage userStorage;
    @Inject
    UserApiService userApiService;
    @Inject
    Provider<IFriendCache> friendCache;

    @Inject
    public UserService() {
    }

    public Observable<User> addUser(String username, String password) {
        return userApiService.addUser(
                new CreateUserDto(username, null, password)
        );
    }

    /**
     * This method changes the username of the user
     *
     * @param username: new username that should be set
     * @return Observable<User> of the updated user,
     * null if no user in UserStorage
     */
    public Observable<User> setUsername(String username) {
        User oldUser = userStorage.getUser();
        if (oldUser == null) {
            return Observable.empty();
        }
        User newUser = new User(oldUser._id(), username, oldUser.status(), oldUser.avatar(), oldUser.friends());
        userStorage.setUser(newUser);
        UpdateUserDto dto = new UpdateUserDto(oldUser.name(), null, null, null, null);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    /**
     * This method changes the password of the user
     *
     * @param password: new password that should be set
     * @return Observable<User> of the updated user,
     * null if no user in UserStorage
     */
    public Observable<User> setPassword(String password) {
        User oldUser = userStorage.getUser();
        if (oldUser == null) {
            return Observable.empty();
        }
        UpdateUserDto dto = new UpdateUserDto(null, null, null, null, password);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    /**
     * This method changes the avatar of the user
     *
     * @param avatar: new avatar that should be set
     * @return Observable<User> of the updated user,
     * null if no user in UserStorage
     */
    public Observable<User> setAvatar(String avatar) {
        User oldUser = userStorage.getUser();
        if (oldUser == null) {
            return Observable.empty();
        }
        User newUser = new User(oldUser._id(), oldUser.name(), oldUser.status(), avatar, oldUser.friends());
        userStorage.setUser(newUser);
        UpdateUserDto dto = new UpdateUserDto(oldUser.name(), null, avatar, null, null);
        return userApiService.updateUser(oldUser._id(), dto);
    }

    public Observable<List<User>> searchFriend(String name) {
        return friendCache.get().getUsers()
                .map((e) -> {
                    if (name.isEmpty()) {
                        return new ArrayList<User>();
                    }
                    return e;
                })
                .flatMap(old -> friendCache.get().getFriends().map((e) -> old))
                .map(users -> {
                    final User user = userStorage.getUser();
                    return users.stream()
                            .filter(f -> f.name().toLowerCase().startsWith(name.toLowerCase())
                                    && !f._id().equals(user._id())) //do not show the searching user
                            .filter(g -> !user.friends().contains(g._id())).toList();
                });
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
        UpdateUserDto dto = new UpdateUserDto(
                null,
                null,
                null,
                new ArrayList<>(friendList),
                null);

        return userApiService.updateUser(user._id(), dto)
                .map(e -> {
                    userStorage.setUser(e);
                    // is true, if last friend is removed
                    if (e.friends().isEmpty()) {
                        return Observable.<List<User>>fromSupplier(ArrayList::new);
                    }
                    return friendCache.get().updateFriends(e);
                }).concatMap(f -> f);
    }

    public Observable<List<User>> getFriends() {
        return friendCache.get().getFriends();
    }

    public Observable<List<User>> filterFriends(String name) {
        return getFriends().map(e -> e.stream().filter(f -> f.name().toLowerCase().startsWith(name.toLowerCase())).toList());
    }

    /**
     * gets a user and all its attributes by a given user id
     *
     * @param userId the id of a user object
     * @return the user found by id
     */
    public Observable<User> getUserById(String userId) {
        return Observable.just(friendCache.get().getUser(userId));
    }

    public Observable<List<User>> getUsers(List<String> ids) {
        List<User> users = ids.stream().map(id -> friendCache.get().getUser(id))
                .filter(Objects::nonNull)
                .toList();
        return Observable.just(users);
    }

    public User getMe() {
        return this.userStorage.getUser();
    }
}
