package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FriendCacheDummy implements IFriendCache {

    @Inject
    UserApiService userApiService;
    @Inject
    UserStorage userStorage;

    @Inject
    public FriendCacheDummy() {
    }

    @Override
    public Observable<List<User>> init(User user) {
        return getUsers();
    }

    @Override
    public void reset() {
        // do nothing
    }

    @Override
    public User getUser(String id) {
        return null;
    }

    @Override
    public Observable<List<User>> updateFriends(User user) {
        return userApiService.getUsers(user.friends());
    }

    @Override
    public Observable<List<User>> getUsers() {
        return userApiService.getUsers();
    }

    @Override
    public void addUser(User user) {
    }

    @Override
    public void updateUser(User user) {
    }


    @Override
    public void removeUser(User user) {
    }

    @Override
    public void NotifyUpdateFriends(User user) {

    }

    @Override
    public Observable<List<User>> getFriends() {
        if (userStorage.getUser().friends().isEmpty()) {
            return Observable.fromSupplier(ArrayList::new);
        }
        return userApiService.getUsers(userStorage.getUser().friends());
    }
}
