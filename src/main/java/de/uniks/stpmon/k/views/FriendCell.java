package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.FriendController;
import de.uniks.stpmon.k.controller.FriendListController;
import de.uniks.stpmon.k.models.User;
import javafx.scene.control.ListCell;

public class FriendCell extends ListCell<User> {

    private final FriendListController friendListController;
    private final Boolean isNewFriend;

    public FriendCell(FriendListController friendListController, Boolean isNewFriend) {
        this.friendListController = friendListController;
        this.isNewFriend = isNewFriend;
    }

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final FriendController friendController = new FriendController(item, isNewFriend, friendListController);
            setGraphic(friendController.render());
        }
    }
}
