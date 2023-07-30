package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.ILifecycleService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FriendCache extends ListenerCache<User, String> implements IFriendCache, ILifecycleService {

    private final InternalFriends friends = new InternalFriends();

    @Inject
    public UserApiService userApiService;

    private String mainUser;

    @Inject
    public FriendCache() {
    }

    @Override
    public void destroy() {
        super.destroy();
        friends.destroy();
        this.mainUser = null;
    }

    @Override
    protected Observable<List<User>> getInitialValues() {
        return userApiService.getUsers();
    }

    @Override
    protected Class<? extends User> getDataClass() {
        return User.class;
    }

    @Override
    protected String getEventName() {
        return "users.*.*";
    }

    @Override
    public String getId(User value) {
        return value._id();
    }

    public FriendCache setMainUser(String mainUser) {
        this.mainUser = mainUser;
        return this;
    }

    public String getMainUser() {
        return mainUser;
    }

    @Override
    public IFriendCache init() {
        if (mainUser == null) {
            throw new IllegalStateException("Main user not set");
        }
        friends.setFriendCache(this);
        friends.init();
        super.init();
        return this;
    }

    @Override
    public Completable onInitialized() {
        return super.onInitialized()
                .andThen(friends.onInitialized());
    }

    @Override
    public Observable<List<User>> updateFriends(User user) {
        List<String> newUserIds = user.friends().stream()
                .filter(key -> !hasValue(key))
                .toList();
        if (!newUserIds.isEmpty()) {
            return userApiService.getUsers(newUserIds).flatMap(users -> {
                addValues(users);
                notifyUpdateFriends(user);
                return friends.getValues().take(1);
            });
        }
        notifyUpdateFriends(user);
        return friends.getValues()
                .take(1);
    }

    public void notifyUpdateFriends(User user) {
        friends.addValues(user.friends()
                .stream()
                .map(this::getValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    }

    @Override
    public void updateValue(User value) {
        super.updateValue(value);
        if (friends.hasValue(value._id())) {
            friends.updateValue(value);
            return;
        }
        if (mainUser == null || !Objects.equals(value._id(), mainUser)) {
            return;
        }
        notifyUpdateFriends(value);
    }

    @Override
    public Observable<List<User>> getFriends() {
        return friends.getValues();
    }

    @Override
    public boolean isMainUser(String userId) {
        return Objects.equals(mainUser, userId);
    }

    private static class InternalFriends extends SimpleCache<User, String> {

        private FriendCache friendCache;

        public InternalFriends() {
        }

        public void setFriendCache(FriendCache friendCache) {
            this.friendCache = friendCache;
        }

        @Override
        protected Observable<List<User>> getInitialValues() {
            // skip the first value, which is the empty list
            return friendCache.getValues()
                    .skip(1).take(1)
                    .flatMap((users -> {
                        User mainUser = friendCache.getValue(friendCache.getMainUser()).orElseThrow();
                        return Observable.just(mainUser.friends()
                                .stream()
                                .map(friendCache::getValue)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList());
                    }));
        }

        @Override
        public String getId(User value) {
            return value._id();
        }

    }

}
