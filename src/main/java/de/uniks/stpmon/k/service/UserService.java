package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class UserService {
    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService){
        this.userApiService = userApiService;
    }

    public Observable<User> setUsername(String username){
        //Auth Service is needed to get id of user
        return null;
    }
}
