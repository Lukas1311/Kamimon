package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.storage.UserStorage;
import de.uniks.stpmon.k.service.storage.cache.IFriendCache;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
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
    public IFriendCache init() {
        return this;
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public Optional<User> getValue(String id) {
        return Optional.of(userApiService.getUser(id).blockingFirst());
    }

    @Override
    public Observable<Optional<User>> listenValue(String id) {
        return Observable.empty();
    }

    @Override
    public Observable<User> onCreation() {
        return Observable.empty();
    }

    @Override
    public Observable<User> onDeletion() {
        return Observable.empty();
    }

    @Override
    public Observable<User> onUpdate() {
        return Observable.empty();
    }

    @Override
    public Observable<List<User>> updateFriends(User user) {
        friends.onNext(userApiService.getUsers(userStorage.getUser().friends())
                .blockingFirst());
        return friends;
    }

    @Override
    public Observable<List<User>> getValues() {
        return userApiService.getUsers();
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

    @Override
    public void addOnDestroy(Runnable onDestroy) {
        // do nothing
    }

    @Override
    public Completable onInitialized() {
        return Completable.complete();
    }

    @Override
    public String getId(User value) {
        return value._id();
    }

    @Override
    public boolean hasValue(String id) {
        return false;
    }

    @Override
    public void addValue(User value) {
        // do nothing
    }

    @Override
    public void updateValue(User value) {
        // do nothing
    }

    @Override
    public void removeValue(User value) {
        // do nothing
    }

    @Override
    public IFriendCache setMainUser(String mainUser) {
        return this;
    }

    @Override
    public boolean isMainUser(String userId) {
        // should not be important for tests
        return false;
    }

    @Override
    public Status getStatus() {
        return Status.INITIALIZED;
    }

    @Override
    public Collection<String> getIds() {
        return List.of();
    }
}
