package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.User;
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
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
class FriendListControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Mock
    Provider<UserService> userServiceProvider;

    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    final ArrayList<User> friends = new ArrayList<>();
    final ArrayList<User> users = new ArrayList<>();

    final BehaviorSubject<List<User>> friendsObs = BehaviorSubject.createDefault(friends);
    final BehaviorSubject<List<User>> usersObs = BehaviorSubject.createDefault(users);

    @InjectMocks
    FriendListController friendListController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);

        friends.add(DummyConstants.USER_ALICE);
        friends.add(DummyConstants.USER_BOB);

        users.add(DummyConstants.USER_ALICE);
        users.add(DummyConstants.USER_BOB);
        users.add(DummyConstants.USER_EVE);

        when(userService.searchUser(anyString())).thenReturn(usersObs);
        when(userService.searchUser(anyString(), eq(true))).thenReturn(friendsObs);
        //when(userService.searchUser(anyString(), eq(false))).thenReturn(user2);

        when(userService.isFriend(eq(DummyConstants.USER_ALICE))).thenReturn(true);
        when(userService.isFriend(eq(DummyConstants.USER_BOB))).thenReturn(true);
        //when(userService.isFriend(eq(DummyConstants.USER_EVE))).thenReturn(false);

        when(userServiceProvider.get()).thenReturn(userService);

        app.show(friendListController);
        stage.requestFocus();
    }

    @Test
    void searchUser() {
        //search in friendList for friend "Alice"
        clickOn("#searchFriend");
        write("Al");

        verifyThat("#searchFriend", hasText("Al"));

        clickOn("#searchButton");

        sleep(1000);

        //get friendList
        final VBox friendList = lookup("#friendListVbox").query();

        //when null then peter doesn't exist
        assertNotNull(friendList.lookup("#Alice"));
        assertNotNull(friendList.lookup("#Bob"));

        clickOn("#checkBox");
        clickOn("#searchButton");

        assertNotNull(friendList.lookup("#Alice"));
        assertNotNull(friendList.lookup("#Bob"));
        assertNotNull(friendList.lookup("#Eve"));

    }

    @Test
    void removeFriend() {
        //get friendList
        //final ScrollPane scrollPane = lookup("#scrollPane").query();
        final VBox userList = lookup("#friendListVbox").query();
        final ListView<User> listView = (ListView<User>) userList.lookup("#userListView");

        verifyThat(listView, ListViewMatchers.hasItems(2));
        assertNotNull(userList.lookup("#Alice"));
        assertNotNull(userList.lookup("#Bob"));

        friends.remove(DummyConstants.USER_ALICE);
        when(userService.removeFriend(any(User.class))).thenReturn(friendsObs);

        //remove friend from local list
        friends.remove(0);
        clickOn("#removeFriendButton");
        friendsObs.onNext(friends);
        sleep(200);
        verify(userService).removeFriend(any(User.class));

        //peter is no longer in friends
        assertNull(userList.lookup("#Alice"));
    }

    @Test
    void addFriend() {
        //get friendList
        final ScrollPane scrollPane = lookup("#scrollPane").query();
        final VBox userList = (VBox) scrollPane.getContent();
        final VBox friendView = (VBox) userList.lookup("#friendSection");
        final VBox userView = (VBox) userList.lookup("#userSection");

        //"Alice" is not displayed before search
        assertNull(userView.lookup("#Alice"));

        //search in friendList for friend "Alice"
        clickOn("#searchFriend");
        write("Alice");

        verifyThat("#searchFriend", hasText("Alice"));

        clickOn("#searchButton");

        friendsObs.onNext(List.of());
        usersObs.onNext(users);
        sleep(200);

        when(userService.addFriend(any(User.class))).thenReturn(Observable.just(users));

        //add "Alice" to friends
        friends.add(users.get(0));

        clickOn("#Alice #removeFriendButton");
        friendsObs.onNext(friends);
        usersObs.onNext(users);
        sleep(200);

        verify(userService).addFriend(any(User.class));

        //"Alice" is in friends
        assertNotNull(friendView.lookup("#Alice"));
    }

}