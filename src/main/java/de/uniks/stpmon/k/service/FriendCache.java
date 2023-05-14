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

	@Inject
	public FriendCache() {
	}

	public void reset() {
		initialized = false;
		userById.clear();
		friends.onNext(List.of());
		users.onNext(List.of());
		disposables.dispose();
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

		disposables.add(eventListener
				.listen("users.*.*", User.class).subscribe(event -> {
							final User user = event.data();
							switch (event.suffix()) {
								case "created" -> addUser(user);
								case "updated" -> updateUser(user);
								case "deleted" -> removeUser(user);
							}
						}, this::handleError
				)
		);

		disposables.add(eventListener
				.listen("users.%s.updated".formatted(mainUser._id()), User.class).subscribe(event -> {
							final User user = event.data();
							NotifyUpdateFriends(user);
						}, this::handleError
				)
		);

		return addUsers(userApiService.getUsers()).flatMap((users) -> {
			NotifyUpdateFriends(mainUser);
			return friends.take(1);
		});
	}

	// reusable handle error function for the onError of an Observable
	private void handleError(Throwable error) {
		System.out.println("Look here for the error: " + error);
		error.printStackTrace();
	}

	@Override
	public Observable<List<User>> updateFriends(User user) {
		List<String> newUserIds = user.friends().stream()
				.filter(key -> !userById.containsKey(key))
				.toList();
		if (newUserIds.size() > 0) {
			return addUsers(userApiService.getUsers(newUserIds))
					.flatMap((users) -> {
						NotifyUpdateFriends(user);
						return friends.take(1);
					});
		}
		NotifyUpdateFriends(user);

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

	public void NotifyUpdateFriends(User user) {
		friends.onNext(user.friends()
				.stream()
				.map(userById::get)
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
