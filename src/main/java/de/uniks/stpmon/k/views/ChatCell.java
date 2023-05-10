package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.ChatEntryController;
import de.uniks.stpmon.k.controller.ChatListController;
import de.uniks.stpmon.k.dto.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;

public class ChatCell extends ListCell<Group> {



    @Override
    protected void updateItem(Group item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            final ChatEntryController chatEntryController = new ChatEntryController(item.name());
            setGraphic(chatEntryController.render());
        }
    }
}
