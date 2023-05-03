package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.UpdateUserDto;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.AuthenticationApiService;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class UserService {

    private User user;
    @Inject
    private final UserApiService userApiService;

    @Inject
    public UserService(User user, UserApiService userApiService){
        this.user = user;
        this.userApiService = userApiService;
    }

    public Observable<User> setUsername(String username){
        this.user = new User(user._id(), username, user.status(), user.avatar(), user.friends());
        UpdateUserDto dto = new UpdateUserDto(this.user.name(), null, null, null, null);
        return userApiService.updateUser(this.user._id(), dto);
    }

    public Observable<User> setPassword(String password){
        UpdateUserDto dto = new UpdateUserDto(this.user.name(), null, null, null, password);
        return userApiService.updateUser(this.user._id(), dto);
    }

    public Observable<User> setAvatar(String avatar){
        this.user = new User(user._id(), user.name(), user.status(), avatar, user.friends());
        UpdateUserDto dto = new UpdateUserDto(this.user.name(), null, avatar, null, null);
        return userApiService.updateUser(this.user._id(), dto);
    }

}
