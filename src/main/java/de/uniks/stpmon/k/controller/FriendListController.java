package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.views.FriendCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FriendListController extends Controller {
    @FXML
    public VBox friendList;
    @FXML
    public TextField searchFriend;
    @FXML
    public Button searchButton;

    @Inject
    UserApiService userApiService;

    @Inject
    UserService userService;

    private final ObservableList<User> friends = FXCollections.observableArrayList();


    @Inject
    public FriendListController() {
    }


    @Override
    public void init() {
        disposables.add(userApiService.getUsers().subscribe(this.friends::setAll));
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        final ListView<User> friends = new ListView<>(this.friends);
        friendList.getChildren().add(friends);
        VBox.setVgrow(friends, Priority.ALWAYS);
        friends.setCellFactory(e -> new FriendCell());

        searchButton.setOnAction(e -> searchForFriend());

        return parent;
    }

    @FXML
    private void searchForFriend() {
        String name = searchFriend.getText();
        disposables.add(userService.searchFriend(name).subscribe(this.friends::setAll));
        System.out.println(friends);
    }

    @Override
    public void destroy() {

    }
}
