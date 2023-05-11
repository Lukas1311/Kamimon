package de.uniks.stpmon.k.views;

import java.io.IOException;

import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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


    private final User user;

    public MessageCell(User user) {
        this.user = user;
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            // final MessageController messageController = new MessageController(item);
            Text senderName = new Text(isOwnMessage(item) ? user.name() : item.sender());
            senderName.setFont(new Font(10.0));
            Text sendTime = new Text(item.createdAt());
            sendTime.setFont(new Font(10.0));
            Text bodyText = new Text(item.body());
            HBox textBox = new HBox(bodyText);
            VBox senderBox = new VBox(senderName);
            VBox timeBox = new VBox(sendTime);
            HBox senderAndTimeBox = new HBox(senderBox, timeBox);
            VBox messageBox = new VBox(textBox, senderAndTimeBox);
            
            // Set the alignment of the text based on the item's alignment property
            messageBox.setAlignment(isOwnMessage(item) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            setGraphic(messageBox);
            // setGraphic(messageController.render());
        }
    }

    private boolean isOwnMessage(Message msg) {
        return msg.sender().equals(user._id());
    }
}
