package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.FriendController;
import de.uniks.stpmon.k.controller.FriendListController;
import de.uniks.stpmon.k.dto.User;
import javafx.scene.control.ListCell;

public class FriendCell extends ListCell<User> {

    private final FriendListController friendListController;

    public FriendCell(FriendListController friendListController) {
        this.friendListController = friendListController;
    }

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final FriendController friendController = new FriendController(item, true, friendListController);
            setGraphic(friendController.render());
        }
    }
}
