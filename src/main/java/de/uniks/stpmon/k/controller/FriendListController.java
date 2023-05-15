package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.FriendCell;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class FriendListController extends Controller {

    @FXML
    public TextField searchFriend;
    @FXML
    public Button searchButton;

    @FXML
    public VBox friendList;

    @Inject
    UserService userService;
    @Inject
    GroupService groupService;
    @Inject
    Provider<ChatController> chatControllerProvider;
    @Inject
    Provider<HybridController> hybridControllerProvider;

    private final ObservableList<User> friends = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private Subject<String> searchUpdate = PublishSubject.create();

    @Inject
    public FriendListController() {
    }

    @Override
    public void init() {
        disposables.add(searchUpdate.flatMap((text) -> userService.filterFriends(text))
                .observeOn(FX_SCHEDULER)
                .subscribe(this.friends::setAll));
        disposables.add(searchUpdate.flatMap((text) -> userService.searchFriend(text))
                .observeOn(FX_SCHEDULER)
                .subscribe(this.users::setAll));
        searchUpdate.onNext("");
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        final ListView<User> friends = new ListView<>(this.friends);
        friendList.getChildren().add(friends);
        friends.setCellFactory(e -> new FriendCell(this, false));

        final ListView<User> users = new ListView<>(this.users);
        friendList.getChildren().add(users);
        VBox.setVgrow(users, Priority.ALWAYS);
        users.setCellFactory(e -> new FriendCell(this, true));

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
        searchUpdate.onNext(name);
    }

    public void handleFriend(Boolean newFriend, User user) {
        if (newFriend) {
            disposables.add(userService.addFriend(user).observeOn(FX_SCHEDULER).subscribe());
        } else {
            disposables.add(userService.removeFriend(user).observeOn(FX_SCHEDULER).subscribe());
        }
    }

    public void openChat(User friend) {
        if (!friends.contains(friend)) {
            return;
        }
        hybridControllerProvider.get().openChat(friend);
    }
}
