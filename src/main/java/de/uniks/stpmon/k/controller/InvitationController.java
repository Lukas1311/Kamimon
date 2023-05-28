package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Message;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Provider;
import java.util.ResourceBundle;

public class InvitationController extends Controller {
    @FXML
    public VBox messageBox;
    @FXML
    public Text invited_text;
    @FXML
    public Button joinButton;
    @FXML
    public Text senderName;
    @FXML
    public Text sendTime;

    ChatController chatController;

    private final Message message;
    private final String username;
    private final String region;

    public InvitationController(Message msg, String senderUsername, String region, ChatController chatController, Provider<ResourceBundle> resources) {
        this.message = msg;
        this.username = senderUsername;
        this.chatController = chatController;
        this.resources = resources;
        this.region = region;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        senderName.setText(username);
        sendTime.setText(MessageController.convertDateTimeToTime(message.createdAt()));

        return parent;
    }

    public void joinRegion() {
        chatController.openRegion(region);
    }
}