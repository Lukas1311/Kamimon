package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.models.User;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface IFriendCache extends ICache<User, String> {

    IFriendCache init();

    IFriendCache setMainUser(String mainUser);

    default IFriendCache setMainUser(User mainUser) {
        return setMainUser(mainUser._id());
    }

    Observable<List<User>> updateFriends(User user);

    Observable<List<User>> getFriends();

    /**
     * Checks if the given user is the main user this cache was initialized with.
     *
     * @param userId the id of the user
     * @return true if the given user is the main user
     */
    boolean isMainUser(String userId);

}
