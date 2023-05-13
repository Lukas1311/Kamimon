package de.uniks.stpmon.k.views;

import java.util.HashMap;

import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.service.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class MessageCell extends ListCell<Message> {

    @FXML
    public HBox textBox;
    @FXML
    public Text bodyText;
    @FXML
    public Text senderName;
    @FXML
    public Text sendTime;


    private final UserService userService;
    private final HashMap<String, String> groupUsers;

    public MessageCell(UserService userService, HashMap<String, String> groupUsers) {
        this.userService = userService;
        this.groupUsers = groupUsers;
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            String sender = groupUsers.get(item.sender());
            final MessageController messageController = new MessageController(item, sender, userService.getMe());
            setGraphic(messageController.render());
        }
    }
}
