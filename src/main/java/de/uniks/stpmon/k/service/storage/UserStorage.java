package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.models.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserStorage {

    private User user;

    @Inject
    public UserStorage() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
