package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.controller.chat.ChatController;
import de.uniks.stpmon.k.controller.chat.InvitationController;
import de.uniks.stpmon.k.models.Message;
import de.uniks.stpmon.k.models.User;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.ResourceBundle;


public class MessageCell extends ListCell<Message> {

    private final HashMap<String, String> groupUsers;
    private final User me;
    private final ChatController chatController;
    private final Provider<ResourceBundle> resources;

    public MessageCell(User me, HashMap<String, String> groupUsers, ChatController chatController, Provider<ResourceBundle> resources) {
        this.groupUsers = groupUsers;
        this.me = me;
        this.chatController = chatController;
        this.resources = resources;
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            String sender = groupUsers.get(item.sender());
            if (item.body().startsWith(InvitationController.INVITATION_START_PATTERN)) {
                String regionId = item.body().substring(15);

                final InvitationController invitationController = new InvitationController(item, sender, regionId,
                        chatController, resources, isOwnMessage(item));
                setGraphic(invitationController.render());
                setDisable(false);
            } else {
                final MessageController messageController = new MessageController(item, sender, isOwnMessage(item));
                setGraphic(messageController.render());
                setDisable(!isOwnMessage(item));
            }
            // setting the alignment directly on the cell makes the trick
            setAlignment(isOwnMessage(item) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);


        }
    }

    private boolean isOwnMessage(Message msg) {
        return msg.sender().equals(me._id());
    }

}