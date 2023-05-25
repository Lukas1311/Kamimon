package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.User;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface IFriendCache {

    Observable<List<User>> init(User user);

    void reset();

    void addUser(User user);

    void updateUser(User user);

    void removeUser(User user);

    void notifyUpdateFriends(User user);

    User getUser(String id);

    Observable<List<User>> getUsers();

    Observable<List<User>> updateFriends(User user);


    Observable<List<User>> getFriends();
}
