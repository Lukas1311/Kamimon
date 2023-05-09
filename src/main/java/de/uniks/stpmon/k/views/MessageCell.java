package de.uniks.stpmon.k.views;

import javax.inject.Provider;

import de.uniks.stpmon.k.controller.ChatController;
import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.dto.Message;
import javafx.scene.control.ListCell;

public class MessageCell extends ListCell<Message> {
    // private final Provider<ChatController> chatControllerProvider;
    // private final Message message;

    public MessageCell() {
        // this.message = msg;
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            final MessageController messageController = new MessageController(item);
            setGraphic(messageController.render());
        }
    }
}
