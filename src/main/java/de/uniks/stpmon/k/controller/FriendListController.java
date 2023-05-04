package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.FriendCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class FriendListController extends Controller {

    @FXML
    public TextField searchFriend;
    @FXML
    public Button searchButton;
    @FXML
    public VBox otherUser;
    @FXML
    public VBox currentFriendBox;
    public VBox friendList;

    @Inject
    UserService userService;

    private final ObservableList<User> friends = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();


    @Inject
    public FriendListController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

//        final ListView<User> friends = new ListView<>(this.friends);
//        friendList.getChildren().add(friends);
//        friendList.getChildren().add(new Line());


        final ListView<User> users = new ListView<>(this.users);
        otherUser.getChildren().add(users);
        VBox.setVgrow(users, Priority.ALWAYS);
        users.setCellFactory(e -> new FriendCell(this));

        searchButton.setOnAction(e -> searchForFriend());

        searchFriend.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchForFriend();
            }
        });

        return parent;
    }

    @FXML
    private void searchForFriend() {
        String name = searchFriend.getText();
        disposables.add(userService.searchFriend(name).observeOn(FX_SCHEDULER).subscribe(this.users::setAll));
    }

    @Override
    public void destroy() {

    }
}
