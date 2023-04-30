package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class FriendListController extends Controller {
    @FXML
    public VBox friendList;
    @FXML
    public TextField searchFriend;
    @FXML
    public Button searchButton;

    private final User user;


    public FriendListController() {
        this(new User("", "", "0", "Alice", "online", "", new ArrayList<>()));
    }

    public FriendListController(User user) {
        this.user = user;
    }


    @Override
    public Parent render() {
        final Parent parent = super.render();

        for (String user : user.friends()) {
            final FriendController friendController = new FriendController();
            friendList.getChildren().add(friendController.render());
        }

        searchButton.setOnAction(e -> searchForFriend());

        return parent;
    }

    @FXML
    private void searchForFriend() {
        //TODO: create search function
    }

}
