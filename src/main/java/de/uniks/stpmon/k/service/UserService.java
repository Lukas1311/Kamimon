package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.storage.cache.CacheManager;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class UserService implements ILifecycleService {

    public enum OnlineStatus {
        ONLINE("online"),
        OFFLINE("offline");
        private final String status;

        OnlineStatus(final String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }

    @Inject
    UserStorage userStorage;
    @Inject
    UserApiService userApiService;
    @Inject
    CacheManager cacheManager;

    @Inject
    public UserService() {
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void destroy() {
        updateStatus(OnlineStatus.OFFLINE).blockingFirst();
    }

    public Observable<User> addUser(String username, String password) {
        return userApiService.addUser(new CreateUserDto(username, null, password));
    }

    private IFriendCache friendCache() {
        return cacheManager.requestFriends(userStorage.getUser()._id());
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
        UpdateUserDto dto = new UpdateUserDto(newUser.name(), null, null, null, null);
        return userApiService.updateUser(newUser._id(), dto);
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
        UpdateUserDto dto = new UpdateUserDto(null, null, avatar, null, null);
        return userApiService.updateUser(newUser._id(), dto);
    }

    /**
     * This method changes the status of the user
     *
     * @param status: new status of type OnlineStatus Enum
     * @return Observable<User> of the updated user,
     * null if no user in UserStorage
     */
    public Observable<User> updateStatus(OnlineStatus status) {
        User oldUser = userStorage.getUser();
        if (oldUser == null) {
            return Observable.empty();
        }
        User newUser = new User(oldUser._id(), oldUser.name(), status.toString(), oldUser.avatar(), oldUser.friends());
        userStorage.setUser(newUser);
        UpdateUserDto dto = new UpdateUserDto(null, status.toString(), null, null, null);
        return userApiService.updateUser(newUser._id(), dto);
    }

    /**
     * This method filters all users, where the username begins with the given string.
     * If the string is empty, all users get returned.
     *
     * @param name: the search term
     * @return Observable list of users that match the search term
     */
    public Observable<List<User>> searchUser(String name) {
        return friendCache().getValues().flatMap(old -> friendCache().getFriends().map((e) -> old)).map(users -> {
            final User user = userStorage.getUser();
            return users.stream().filter(f -> f.name().toLowerCase().startsWith(name.toLowerCase())
                            && !f._id().equals(user._id())) //do not show the searching user
                    .toList();
        });
    }

    public Observable<List<User>> searchUser(String name, boolean onlyFriends) {
        return friendCache().getValues().flatMap(old -> friendCache().getFriends().map((e) -> old)).map(users -> {
            final User user = userStorage.getUser();
            return users.stream().filter(f -> f.name().toLowerCase().startsWith(name.toLowerCase())
                            && !f._id().equals(user._id())) //do not show the searching user
                    .filter(g -> isFriend(g) || !onlyFriends)
                    .toList();
        });
    }

    public boolean isFriend(User user) {
        return userStorage.getUser().friends().contains(user._id());
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
            // is true, if last friend is removed
            if (e.friends().isEmpty()) {
                return Observable.<List<User>>fromSupplier(ArrayList::new);
            }
            return friendCache().updateFriends(e);
        }).concatMap(f -> f);
    }

    public Observable<List<User>> getFriends() {
        return friendCache().getFriends();
    }

    public Observable<List<User>> filterFriends(String name) {
        return getFriends().map(e -> e.stream().filter(f -> f.name().toLowerCase().startsWith(name.toLowerCase())).toList());
    }

    public Observable<List<User>> getUsers(List<String> ids) {
        List<User> users = ids.stream().map(id -> friendCache().getValue(id)).filter(Optional::isPresent).map(Optional::get).toList();
        return Observable.just(users);
    }

    public User getMe() {
        return this.userStorage.getUser();
    }

    public Observable<User> deleteMe() {
        User currentUser = userStorage.getUser();
        if (currentUser == null) {
            return Observable.empty();
        }
        return userApiService.deleteUser(currentUser._id());
    }


    /**
     * Checks if the user is online
     *
     * @param id id of the user
     * @return observable which emits true if the user is online
     */
    public Observable<Boolean> isOnline(String id) {
        return userApiService.getUser(id)
                .map(e -> e.status().equals(OnlineStatus.ONLINE.toString()));
    }

}
