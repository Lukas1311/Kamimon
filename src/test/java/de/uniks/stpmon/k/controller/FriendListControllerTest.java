package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.text.Text;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    FriendListController friendListController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        friends.add(new User("1", "Peter", "online", null, null));
        when(userService.getFriends()).thenReturn(Observable.just(friends));
        when(userService.filterFriends(anyString())).thenReturn(Observable.just(friends));
        when(userService.searchFriend(anyString())).thenReturn(Observable.just(friends));
        //when(userService.addFriend(any(User.class))).thenReturn(Observable.just(List.of(new User("1", "Peter", null, null, null))));
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
        verify(userService).removeFriend(any(User.class));

        //peter is no longer in friends
        assertNull(listView.lookup("#Peter"));
    }

    @Test
    void addFriend() {

    }
}