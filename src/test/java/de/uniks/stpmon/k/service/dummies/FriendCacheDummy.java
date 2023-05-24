package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.storage.IFriendCache;
import de.uniks.stpmon.k.service.storage.UserStorage;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.List;

public class FriendCacheDummy implements IFriendCache {

    @Inject
    UserApiService userApiService;
    @Inject
    UserStorage userStorage;
    private final BehaviorSubject<List<User>> friends = BehaviorSubject.create();

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
        return userApiService.getUser(id).blockingFirst();
    }

    @Override
    public Observable<List<User>> updateFriends(User user) {
        friends.onNext(userApiService.getUsers(userStorage.getUser().friends())
                .blockingFirst());
        return friends;
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
    public void notifyUpdateFriends(User user) {

    }

    @Override
    public Observable<List<User>> getFriends() {
        if (!friends.hasValue()) {
            if (userStorage.getUser().friends().isEmpty()) {
                friends.onNext(List.of());
            } else {
                friends.onNext(userApiService.getUsers(userStorage.getUser().friends())
                        .blockingFirst());
            }
        }
        return friends;
    }
}
