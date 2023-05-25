package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.FriendController;
import de.uniks.stpmon.k.controller.FriendListController;
import de.uniks.stpmon.k.dto.User;
import javafx.scene.control.ListCell;

import javax.inject.Provider;
import java.util.ResourceBundle;

public class FriendCell extends ListCell<User> {

    private final FriendListController friendListController;
    private final Boolean isNewFriend;

    private final Provider<ResourceBundle> resources;

    public FriendCell(FriendListController friendListController, Boolean isNewFriend, Provider<ResourceBundle> resources) {
        this.friendListController = friendListController;
        this.isNewFriend = isNewFriend;
        this.resources = resources;
    }

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final FriendController friendController = new FriendController(item, isNewFriend, friendListController, resources);
            setGraphic(friendController.render());
        }
    }
}
