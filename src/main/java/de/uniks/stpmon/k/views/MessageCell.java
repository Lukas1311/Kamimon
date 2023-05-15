package de.uniks.stpmon.k.views;

import java.util.HashMap;


import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;


public class MessageCell extends ListCell<Message> {

    private final HashMap<String, String> groupUsers;
    private final User me;

    public MessageCell(User me, HashMap<String, String> groupUsers) {
        this.groupUsers = groupUsers;
        this.me = me;
    }

    @Override
    public void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            String sender = groupUsers.get(item.sender());
            final MessageController messageController = new MessageController(item, sender, me);
            // setting the alignment directly on the cell makes the trick
            setAlignment(isOwnMessage(item) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            setGraphic(messageController.render());
        }
    }

    private boolean isOwnMessage(Message msg) {
        return msg.sender().equals(me._id());
    }
}
