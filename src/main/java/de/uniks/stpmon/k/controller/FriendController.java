package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.inject.Provider;
import java.util.ResourceBundle;

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

    private final boolean isFriend;


    public FriendController(User user, FriendListController friendListController, Provider<ResourceBundle> resources, Provider<UserService> userServiceProvider) {
        this.user = user;
        this.isFriend = userServiceProvider.get().isFriend(user);
        this.friendListController = friendListController;
        this.resources = resources;

    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        parent.setId(user.name());

        userName.setText(user.name());
        messageButton.setTooltip(new Tooltip(translateString("chatWithFriend")));

        removeFriendButton.setTooltip(new Tooltip(translateString(isFriend ? ("removeFriend") : ("addFriend"))));
        if (isFriend) {
            removeFriendText.setIconLiteral("mdral-clear");
            removeFriendText.setIconColor(Color.rgb(207, 42, 39));
        } else {
            removeFriendText.setIconLiteral("mdral-add");
            removeFriendText.setIconColor(Color.rgb(106, 168, 79));
        }

        if (user.status().equals("offline")) {
            userStatus.setFill(Color.rgb(207, 42, 39));
        } else {
            userStatus.setFill(Color.rgb(106, 168, 79));
        }

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
        friendListController.handleFriend(user);
    }

}
