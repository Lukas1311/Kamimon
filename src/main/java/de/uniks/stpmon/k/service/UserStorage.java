package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.User;

import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
public class UserStorage {
    private User user;

    @Inject
    public UserStorage(){
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
