package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.UserApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

//    @Inject
//    UserApiService userApiService;

//    private final ObservableList<User> friends = FXCollections.observableArrayList();

    private final List<FriendController> friendControllers = new ArrayList<>();

    private final ArrayList<String> friends = new ArrayList<>();
    private final User user = new User("", "", "0", "Alice", "online", "", friends);


    public FriendListController() {
        friends.add("Dummy1");
        friends.add("Dummy2");
        friends.add("Dummy3");
        friends.add("Dummy4");
    }

//    public FriendListController(User user) {
//        this.user = user;
//    }


//    @Override
//    public void init() {
//        disposable.add(userApiService.getUsers().subscribe(this.friends::setAll));
//    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

//        final ListView<User> friends = new ListView<>(this.friends);


        for (String user : user.friends()) {
            final FriendController friendController = new FriendController();
            friendControllers.add(friendController);
            friendList.getChildren().add(friendController.render());
            friendController.init();
        }

        searchButton.setOnAction(e -> searchForFriend());

        return parent;
    }

    @FXML
    private void searchForFriend() {
        //TODO: create search function
    }

    @Override
    public void destroy() {
        for (final FriendController i : friendControllers) {
            i.destroy();
        }
    }
}
