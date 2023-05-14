package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.GroupService;
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

    private final ObservableList<User> friends = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();


    @Inject
    public FriendListController() {
    }

    @Override
    public void init() {
        disposables.add(userService.getFriends().observeOn(FX_SCHEDULER).subscribe(this.friends::setAll));
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
        disposables.add(userService.filterFriends(name).observeOn(FX_SCHEDULER).subscribe(this.friends::setAll));
        disposables.add(userService.searchFriend(name).observeOn(FX_SCHEDULER).subscribe(this.users::setAll));
    }

    public void handleFriend(Boolean newFriend, User user) {
        if (newFriend) {
            disposables.add(userService.addFriend(user).observeOn(FX_SCHEDULER).subscribe(col -> {
                searchForFriend();
                this.friends.setAll(col);
            }));
        } else {
            disposables.add(userService.removeFriend(user).observeOn(FX_SCHEDULER).subscribe(col -> {
                searchForFriend();
                this.friends.setAll(col);
            }));
        }
    }

    public void openChat(User friend) {
        // the user can only open the chat when the other user is a friend
        if (!friends.contains(friend)) {
            return;
        }
        System.out.println("name: " + friend.name() + ", id: " + friend._id());
        ChatController chat = chatControllerProvider.get();
        User me = userService.getMe(); // is non api call
        ArrayList<String> oneOnOneChat = new ArrayList<>(List.of(friend._id(), me._id()));
        // check if the friend and the user already have a group, if not create one
        disposables.add(
            groupService.getGroupsByMembers(oneOnOneChat)
                .observeOn(FX_SCHEDULER)
                .subscribe(groups -> {
                    // just take the first group
                    if (groups.get(0) != null) {
                        chat.setGroup(groups.get(0));
                    } else {
                        System.out.println("firstGroup is null");
                        disposables.add(groupService.createGroup("%s + %s".formatted(friend.name(),me.name()), oneOnOneChat)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(group -> chat.setGroup(group))
                        );
                    }
                })
        );
        app.show(chat);
    }
}
