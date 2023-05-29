package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.User;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import javax.inject.Provider;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

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
    @FXML
    public FontIcon removeFriendText;
    @FXML
    public FontIcon chat;

    private final FriendListController friendListController;

    private final User user;
    private final boolean newFriend;


    public FriendController(User user, Boolean newFriend, FriendListController friendListController, Provider<ResourceBundle> resources) {
        this.user = user;
        this.newFriend = newFriend;
        this.friendListController = friendListController;
        this.resources = resources;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        parent.setId(user.name());

        userName.setText(user.name());
        messageButton.setTooltip(new Tooltip(translateString("chatWithFriend")));
        removeFriendButton.setTooltip(new Tooltip(translateString(newFriend ? ("addFriend") : ("removeFriend"))));

        if (newFriend) {
            removeFriendText.setIconLiteral("mdral-add");
            removeFriendText.setIconColor(Color.GREEN);
        } else {
            removeFriendText.setIconLiteral("mdral-clear");
            removeFriendText.setIconColor(Color.RED);
        }

        if (user.status().equals("offline")) {
            userStatus.setFill(Color.RED);
        } else {
            userStatus.setFill(Color.GREEN);
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
