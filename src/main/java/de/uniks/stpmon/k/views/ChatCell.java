package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.ChatEntryController;
import de.uniks.stpmon.k.controller.ChatListController;
import de.uniks.stpmon.k.dto.Group;
import javafx.scene.control.ListCell;

public class ChatCell extends ListCell<Group> {

    private final ChatListController chatListController;

    public ChatCell(ChatListController chatListController) {
        this.chatListController = chatListController;
    }

    @Override
    protected void updateItem(Group item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            final ChatEntryController chatEntryController = new ChatEntryController(item, chatListController);
            setGraphic(chatEntryController.render());
        }
    }
}
