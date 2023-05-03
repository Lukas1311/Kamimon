package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.FriendController;
import de.uniks.stpmon.k.dto.User;
import javafx.scene.control.ListCell;

public class FriendCell extends ListCell<User> {

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            final FriendController friendController = new FriendController(item);
            setGraphic(friendController.render());
        }
    }
}