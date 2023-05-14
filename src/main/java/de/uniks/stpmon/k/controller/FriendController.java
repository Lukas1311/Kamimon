package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

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

    private final FriendListController friendListController;

    private final User user;
    private final boolean newFriend;


    public FriendController(User user, Boolean newFriend, FriendListController friendListController) {
        this.user = user;
        this.newFriend = newFriend;
        this.friendListController = friendListController;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        parent.setId(user.name());

        userName.setText(user.name());

        if (newFriend) {
            removeFriendButton.setText("+");
            removeFriendButton.setStyle("-fx-text-fill: green");
        }

        if (user.status().equals("offline")) {
            userStatus.setFill(Color.RED);
        }

        //add avatar-url when avatar != null
        //avatarBox.setBackground(new Background(new BackgroundImage(new Image(""), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        messageButton.setOnAction(e -> openChat());

        removeFriendButton.setOnAction(e -> handleFriend());

        return parent;
    }

    @FXML
    public void openChat() {
        friendListController.openChat(user);
    }

    @FXML
    public void handleFriend() {
        friendListController.handleFriend(newFriend, user);
    }
}
