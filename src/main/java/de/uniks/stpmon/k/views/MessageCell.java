package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.InvitationController;
import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.ResourceBundle;


public class MessageCell extends ListCell<Message> {

    private final HashMap<String, String> groupUsers;
    private final User me;
    private final Provider<HybridController> hybridController;
    private final Provider<ResourceBundle> resources;

    public MessageCell(User me, HashMap<String, String> groupUsers, Provider<HybridController> hybridController, Provider<ResourceBundle> resources) {
        this.groupUsers = groupUsers;
        this.me = me;
        this.hybridController = hybridController;
        this.resources = resources;
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
                final InvitationController invitationController = new InvitationController(item, sender, me, hybridController, resources);
                // setting the alignment directly on the cell makes the trick
                setAlignment(isOwnMessage(item) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                setGraphic(invitationController.render());
                setDisable(false);
            } else {
                final MessageController messageController = new MessageController(item, sender, me);
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