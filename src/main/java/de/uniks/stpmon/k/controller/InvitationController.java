package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class InvitationController extends Controller {
    @FXML
    public VBox messageBox;
    @FXML
    public Text invited_text;
    @FXML
    public Button join_button;
    @FXML
    public Text senderName;

    private final Message message;
    private final String username;

    public InvitationController(Message msg, String senderUsername, User me) {
        this.message = msg;
        this.username = senderUsername;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();


        return parent;
    }
}
