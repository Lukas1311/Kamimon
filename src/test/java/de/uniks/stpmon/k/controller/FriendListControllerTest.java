package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
class FriendListControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Spy
    App app = new App(null);

    final ArrayList<User> friends = new ArrayList<>();
    final ArrayList<User> users = new ArrayList<>();
    final BehaviorSubject<List<User>> updatedFriends = BehaviorSubject.createDefault(friends);
    final BehaviorSubject<List<User>> filteredUsers = BehaviorSubject.createDefault(List.of());

    @InjectMocks
    FriendListController friendListController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        friends.add(new User(null, "Peter", "online", null, null));
        users.add(new User(null, "Alice", "online", null, null));
        when(userService.filterFriends(anyString())).thenReturn(updatedFriends);
        when(userService.searchFriend(anyString())).thenReturn(filteredUsers);
        app.show(friendListController);
        stage.requestFocus();
    }

    @Test
    void searchUser() {
        //search in friendList for friend "Peter"
        clickOn("#searchFriend");
        write("Pe");

        FxAssert.verifyThat("#searchFriend", hasText("Pe"));

        clickOn("#searchButton");

        //get friendList
        final ScrollPane scrollPane = lookup("#scrollPane").query();
        final VBox friendList = (VBox) scrollPane.getContent();

        //when null then peter doesn't exist
        assertNotNull(friendList.lookup("#Peter"));
    }

    @Test
    void removeFriend() {
        //get friendList
        final ScrollPane scrollPane = lookup("#scrollPane").query();
        final VBox userList = (VBox) scrollPane.getContent();
        final ListView<?> listView = (ListView<?>) userList.getChildren().get(0);

        assertNotNull(listView.lookup("#Peter"));

        when(userService.removeFriend(any(User.class))).thenReturn(Observable.just(friends));

        //remove friend from local list
        friends.remove(0);
        clickOn("#removeFriendButton");
        updatedFriends.onNext(friends);
        sleep(200);
        verify(userService).removeFriend(any(User.class));

        //peter is no longer in friends
        assertNull(listView.lookup("#Peter"));
    }

    @Test
    void addFriend() {
        //get friendList
        final ScrollPane scrollPane = lookup("#scrollPane").query();
        final VBox userList = (VBox) scrollPane.getContent();
        final ListView<?> friendView = (ListView<?>) userList.getChildren().get(0);
        final ListView<?> userView = (ListView<?>) userList.getChildren().get(1);

        //"Alice" is not displayed before search
        assertNull(userView.lookup("#Alice"));

        //search in friendList for friend "Alice"
        clickOn("#searchFriend");
        write("Alice");

        FxAssert.verifyThat("#searchFriend", hasText("Alice"));

        clickOn("#searchButton");

        updatedFriends.onNext(List.of());
        filteredUsers.onNext(users);
        sleep(200);

        when(userService.addFriend(any(User.class))).thenReturn(Observable.just(users));

        //add "Alice" to friends
        friends.add(users.get(0));

        clickOn("#Alice #removeFriendButton");
        updatedFriends.onNext(friends);
        filteredUsers.onNext(users);
        sleep(200);

        verify(userService).addFriend(any(User.class));

        //"Alice" is in friends
        assertNotNull(friendView.lookup("#Alice"));
    }
}