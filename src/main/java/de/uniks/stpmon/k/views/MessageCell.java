package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.InvitationController;
import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserService;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;

import java.util.HashMap;


public class MessageCell extends ListCell<Message> {

    private final UserService userService;
    private final HashMap<String, String> groupUsers;
    private final User me;

    public MessageCell(UserService userService, HashMap<String, String> groupUsers) {
        this.userService = userService;
        this.groupUsers = groupUsers;
        this.me = userService.getMe();
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            String sender = groupUsers.get(item.sender());
            if(item.body().startsWith("JoinInvitation")) {
                final InvitationController invitationController = new InvitationController(item, sender, userService.getMe());
                // setting the alignment directly on the cell makes the trick
                setAlignment(isOwnMessage(item) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                setGraphic(invitationController.render());
                setDisable(!isOwnMessage(item));
            } else {
                final MessageController messageController = new MessageController(item, sender, userService.getMe());
                // setting the alignment directly on the cell makes the trick
                setAlignment(isOwnMessage(item) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                setGraphic(messageController.render());
                setDisable(!isOwnMessage(item));
            }

        }
    }

    private boolean isOwnMessage(Message msg) {
        return msg.sender().equals(me._id());
    }
}
