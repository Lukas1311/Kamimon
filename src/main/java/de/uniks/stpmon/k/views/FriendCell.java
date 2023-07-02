package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.FriendController;
import de.uniks.stpmon.k.controller.FriendListController;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.UserService;
import javafx.scene.control.ListCell;

import javax.inject.Provider;
import java.util.ResourceBundle;

public class FriendCell extends ListCell<User> {
    private final FriendListController friendListController;
    private final Provider<UserService> userServiceProvider;

    private final Provider<ResourceBundle> resources;

    public FriendCell(FriendListController friendListController, Provider<ResourceBundle> resources, Provider<UserService> userServiceProvider) {
        this.friendListController = friendListController;
        this.resources = resources;
        this.userServiceProvider = userServiceProvider;
    }

    @Override
    protected void updateItem(User item, boolean empty){
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final FriendController friendController = new FriendController(item, friendListController, resources, userServiceProvider);
            setGraphic(friendController.render());
        }

    }

}
