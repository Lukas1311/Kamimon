package de.uniks.stpmon.k;

import dagger.Module;
import dagger.Provides;
import de.uniks.stpmon.k.dto.CreateUserDto;
import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Module
public class UserTestModule {

    @Provides
    @Singleton
    static UserApiService userApiService() {
        return new UserApiService() {
            final ArrayList<User> users = new ArrayList<>();

            /**
             * 3 DummyUsers are added to the users list
             * ids are of type "id0" and names are of type "TestUser0"
             */
            private void initDummyUsers() {
                int amount = 3;
                int size = users.size();
                for (int i = size; i < size + amount; i++) {
                    String id = "id" + i;
                    String name = "TestUser" + i;
                    users.add(new User(id, name, "offline", "someAvatar", new ArrayList<>()));
                }

            }

            @Override
            public Observable<User> addUser(CreateUserDto dto) {
                String avatar = "someAvatar";
                if (dto.avatar() != null) {
                    avatar = dto.avatar();
                }
                User user = new User(
                        String.valueOf(users.size()),
                        dto.name(),
                        "offline",
                        avatar,
                        new ArrayList<>()
                );
                users.add(user);
                return Observable.just(user);
            }

            /**
             * returns the Users (if list is empty, it gets initialized with dummyUsers)
             */
            @Override
            public Observable<List<User>> getUsers() {
                //1 because test bot registers, and is added to list
                if (users.size() <= 1) {
                    initDummyUsers();
                }
                return Observable.just(users);
            }

            @Override
            public Observable<List<User>> getUsers(List<String> ids) {
                //1 because test bot registers, and is added to list
                if (users.size() <= 1) {
                    initDummyUsers();
                }
                List<User> returnUsers = users.stream()
                        .filter(u -> ids.contains(u._id()))
                        .toList();
                return Observable.just(returnUsers);
            }

            @Override
            public Observable<User> getUser(String id) {
                //1 because test bot registers, and is added to list
                if (users.size() <= 1) {
                    initDummyUsers();
                }
                Optional<User> returnUser = users.stream()
                        .filter(u -> id.equals(u._id()))
                        .findFirst();

                return returnUser.map(r -> Observable.just(returnUser.get())).orElseGet(()
                        -> Observable.error(new Throwable("404 Not found")));
            }

            /**
             * updates attribute(s) of user
             * @param id: id of User
             * @param dto: dto with attributes that should be changed (password has no effect in this method)
             */
            @Override
            public Observable<User> updateUser(String id, UpdateUserDto dto) {
                //1 because test bot registers, and is added to list
                if (users.size() <= 1) {
                    initDummyUsers();
                }

                Optional<User> oldUserOptional = users.stream()
                        .filter(u -> id.equals(u._id()))
                        .findFirst();
                if (oldUserOptional.isPresent()) {
                    User oldUser = oldUserOptional.get();
                    User newUser = applyUpdate(dto, oldUser);
                    users.remove(oldUser);
                    users.add(newUser);
                    return Observable.just(newUser);
                }
                return Observable.error(new Throwable("404 Not found"));
            }

            private static User applyUpdate(UpdateUserDto dto, User oldUser) {
                String name = oldUser.name();
                String status = oldUser.status();
                String avatar = oldUser.avatar();
                ArrayList<String> friends = oldUser.friends();
                if (dto.name() != null) {
                    name = dto.name();
                }
                if (dto.status() != null) {
                    status = dto.status();
                }
                if (dto.avatar() != null) {
                    avatar = dto.avatar();
                }
                if (dto.friends() != null) {
                    friends = dto.friends();
                }
                return new User(
                        oldUser._id(), name, status, avatar, friends
                );
            }

            @Override
            public Observable<User> deleteUser(String id) {
                if (users.isEmpty()) {
                    initDummyUsers();
                }

                Optional<User> deleteUser = users.stream()
                        .filter(u -> id.equals(u._id()))
                        .findFirst();

                if (deleteUser.isPresent()) {
                    users.remove(deleteUser.get());
                    return Observable.just(deleteUser.get());
                }

                return Observable.error(new Throwable("404 Not found"));
            }
        };
    }

}
