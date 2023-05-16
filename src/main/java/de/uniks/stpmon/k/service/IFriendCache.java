package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.User;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface IFriendCache{

	Observable<List<User>> init(User user);

	void reset();

	void addUser(User user);

	void updateUser(User user);

	void removeUser(User user);

	void NotifyUpdateFriends(User user);

	User getUser(String id);

	Observable<List<User>> getUsers();

	Observable<List<User>> updateFriends(User user);


	Observable<List<User>> getFriends();
}