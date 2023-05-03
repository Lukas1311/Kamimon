package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class FriendController extends Controller {

    @FXML
    public VBox avatarBox;
    @FXML
    public Text userName;
    @FXML
    public Circle userStatus;
    @FXML
    public Button messageButton;
    @FXML
    public Button removeFriendButton;

    private final User user;


    public FriendController(User user) {
        this.user = user;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        userName.setText(user.name());

        if (user.status().equals("offline")) {
            userStatus.setFill(Color.RED);
        }

        //add avatar-url when avatar != null
        //avatarBox.setBackground(new Background(new BackgroundImage(new Image(""), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        messageButton.setOnAction(e -> openChat());

        removeFriendButton.setOnAction(e -> removeFriend());

        return parent;
    }

    public void openChat() {

    }

    public void removeFriend() {

    }
}
