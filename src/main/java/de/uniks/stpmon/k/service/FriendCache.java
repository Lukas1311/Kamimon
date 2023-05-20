package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class FriendCache implements IFriendCache {
	@Inject
	public UserApiService userApiService;
	@Inject
	public EventListener eventListener;
	private final Subject<List<User>> users = BehaviorSubject.createDefault(List.of());
	private final Subject<List<User>> friends = BehaviorSubject.createDefault(List.of());
	private final Map<String, User> userById = new LinkedHashMap<>();
	protected CompositeDisposable disposables = new CompositeDisposable();
	private boolean initialized = false;
	private String mainUser;

	@Inject
	public FriendCache() {
	}

	public void reset() {
		initialized = false;
		userById.clear();
		friends.onNext(List.of());
		users.onNext(List.of());
		disposables.dispose();
		this.mainUser = null;
	}

	@Override
	public User getUser(String id) {
		return userById.get(id);
	}

	@Override
	public Subject<List<User>> getUsers() {
		return users;
	}

	@Override
	public Observable<List<User>> init(User mainUser) {
		if (initialized) {
			reset();
		}
		disposables = new CompositeDisposable();
		initialized = true;
		this.mainUser = mainUser._id();

		disposables.add(eventListener
				.listen("users.*.*", User.class).subscribe(event -> {
							final User user = event.data();
							switch (event.suffix()) {
								case "created" -> addUser(user);
								case "updated" -> updateUser(user);
								case "deleted" -> removeUser(user);
							}
						}, error -> {}
				)
		);

		return addUsers(userApiService.getUsers()).flatMap((users) -> {
			notifyUpdateFriends(mainUser);
			return friends.take(1);
		});
	}

	@Override
	public Observable<List<User>> updateFriends(User user) {
		List<String> newUserIds = user.friends().stream()
				.filter(key -> !userById.containsKey(key))
				.toList();
		if (newUserIds.size() > 0) {
			return addUsers(userApiService.getUsers(newUserIds))
					.flatMap((users) -> {
						notifyUpdateFriends(user);
						return friends.take(1);
					});
		}
		notifyUpdateFriends(user);

		return friends.take(1);
	}

	private Observable<List<User>> addUsers(Observable<List<User>> source) {
		return source.map(users -> {
			users.forEach(u -> userById.put(u._id(), u));
			List<User> newUsers = new ArrayList<>(userById.values());
			this.users.onNext(newUsers);
			return newUsers;
		});
	}

	public void notifyUpdateFriends(User user) {
		friends.onNext(user.friends()
				.stream()
				.map(userById::get)
				.filter(Objects::nonNull)
				.toList());
	}

	@Override
	public void addUser(User user) {
		userById.put(user._id(), user);
		users.onNext(new ArrayList<>(userById.values()));
	}

	@Override
	public void updateUser(User user) {
		userById.put(user._id(), user);
		users.onNext(new ArrayList<>(userById.values()));
		if (mainUser == null || !Objects.equals(user._id(), mainUser)) {
			return;
		}
		notifyUpdateFriends(user);
	}

	@Override
	public void removeUser(User user) {
		userById.remove(user._id());
		users.onNext(new ArrayList<>(userById.values()));
	}

	@Override
	public Observable<List<User>> getFriends() {
		return friends;
	}
}
