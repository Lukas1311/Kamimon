package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.FriendCell;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ResourceBundle;

@Singleton
public class FriendListController extends ToastedController {

    private final Subject<String> searchUpdate = PublishSubject.create();

    @FXML
    public CheckBox checkBox;
    @FXML
    public VBox friendListVbox;
    @FXML
    public TextField searchFriend;
    @FXML
    public Button searchButton;

    @Inject
    UserService userService;
    @Inject
    Provider<HybridController> hybridControllerProvider;
    @Inject
    Provider<ResourceBundle> resources;
    @Inject
    Provider<UserService> userServiceProvider;

    private final ObservableList<User> friendSearchRes = FXCollections.observableArrayList();
    private final ObservableList<User> allSearchRes = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();

    private Boolean allUsers = false;

    @Inject
    public FriendListController() {
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        final ListView<User> userList = new ListView<>(this.users);
        userList.setId("userListView");
        userList.getStyleClass().add("chat-ov-list"); // TODO: Add styleclass
        userList.setCellFactory(param -> new FriendCell(this, resources, userServiceProvider));
        friendListVbox.getChildren().add(userList);
        userList.setCache(true);
        userList.setCacheHint(CacheHint.SPEED);
        VBox.setVgrow(userList, Priority.ALWAYS);

        searchButton.setOnAction(e -> searchForFriend());

        allUsers = checkBox.isSelected();
        checkBox.setOnAction(e -> allUsers = checkBox.isSelected());

        searchFriend.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchForFriend();
            }
        });

        searchForFriend();

        return parent;
    }

    @Override
    public void init() {
        subscribe(searchUpdate.flatMap((text) -> userService.searchUser(text)), (values) -> {
            allSearchRes.setAll(values);
            if (allUsers) {
                users.setAll(values);
            }
        }, this::handleError);

        subscribe(searchUpdate.flatMap((text) -> userService.searchUser(text, true)), (values) -> {
            friendSearchRes.setAll(values);
            if (!allUsers) {
                users.setAll(values);
            }
        }, this::handleError);
    }

    @FXML
    private void searchForFriend() {
        String name = searchFriend.getText();
        searchUpdate.onNext(name);
    }

    public void handleFriend(User user) {
        if (userService.isFriend(user)) {
            disposables.add(
                    userService.removeFriend(user).observeOn(FX_SCHEDULER).doOnError(this::handleError).subscribe());
        } else {
            disposables
                    .add(userService.addFriend(user).observeOn(FX_SCHEDULER).doOnError(this::handleError).subscribe());
        }
    }

    public void openChat(User friend) {
        hybridControllerProvider.get().openChat(friend);
    }

}
