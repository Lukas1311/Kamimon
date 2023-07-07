package de.uniks.stpmon.k.controller.chat;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.storage.UserStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class CreateChatControllerTest extends ApplicationTest {

    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    GroupService groupService;
    @Mock
    UserService userService;
    @Mock
    UserStorage userStorage;

    @Spy
    final App app = new App(null);

    @InjectMocks
    CreateChatController createChatController;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    final List<User> members = new ArrayList<>();
    final List<User> friends = new ArrayList<>();
    final List<String> memberNames = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        final User alice = new User("Alice", "Alice", null, null, null);
        final User bob = new User("Bob", "Bob", "online", null, null);
        final User peter = new User("Peter", "Peter", "online", null, null);
        when(userStorage.getUser()).thenReturn(alice);
        when(userService.getMe()).thenReturn(alice);
        members.add(alice);
        members.add(bob);
        members.add(peter);
        friends.add(bob);
        friends.add(peter);
        when(userService.getFriends()).thenReturn(Observable.just(friends));
        app.show(createChatController);
        stage.requestFocus();
    }

    @Test
    void leaveChat() {
        when(userService.getUsers(anyList())).thenReturn(Observable.just(List.of()));

        // add User id to memberNames
        memberNames.add(members.get(0)._id());
        memberNames.add(members.get(1)._id());

        final Group group = new Group(null, null, null, "", memberNames);
        createChatController.setGroup(group);
        Platform.runLater(() -> app.show(createChatController));
        waitForFxEvents();

        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);

        when(groupService.deleteOrUpdateGroup(any(), any(), any())).thenCallRealMethod();
        when(groupService.updateGroup(any(), any(), any())).thenReturn(Observable.just(group));

        clickOn("#leaveGroupButton");
        verify(groupService).updateGroup(any(), any(), any());

        verify(mock, times(2)).popTab();
    }

    @Test
    void updateChat() {
        when(userService.getUsers(anyList())).thenReturn(Observable.just(List.of()));

        // add User id to memberNames
        memberNames.add(members.get(0)._id());
        memberNames.add(members.get(1)._id());

        final Group group = new Group(null, null, null, "Coole", memberNames);
        createChatController.setGroup(group);
        Platform.runLater(() -> app.show(createChatController));
        waitForFxEvents();

        verifyThat("#groupNameField", hasText("Coole"));

        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);

        when(groupService.updateGroup(any(), any(), any())).thenReturn(Observable.just(group));

        clickOn("#groupNameField");
        write("Gruppe");

        verifyThat("#groupNameField", hasText("CooleGruppe"));

        clickOn("#Peter");
        clickOn("#Bob");

        clickOn("#createGroupButton");
        verify(groupService).updateGroup(any(), any(), any());

        verify(mock, times(2)).popTab();
    }

    @Test
    void deleteChat() {
        when(userService.getUsers(anyList())).thenReturn(Observable.just(List.of()));

        // add User id to memberNames
        memberNames.add(members.get(0)._id());

        final Group group = new Group(null, null, null, "", memberNames);
        createChatController.setGroup(group);
        Platform.runLater(() -> app.show(createChatController));
        waitForFxEvents();

        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);

        when(groupService.deleteOrUpdateGroup(any(), any(), any())).thenCallRealMethod();
        when(groupService.deleteGroup(any())).thenReturn(Observable.just(group));

        clickOn("#leaveGroupButton");
        verify(groupService).deleteGroup(any());

        verify(mock, times(2)).popTab();
    }

    @Test
    void createGroupWith3Member() {
        // add User id to memberNames
        memberNames.add(members.get(1)._id());
        memberNames.add(members.get(0)._id());
        memberNames.add(members.get(2)._id());

        final Group group = new Group(null, null, null, "", memberNames);
        when(groupService.createGroup(any(), anyList())).thenReturn(Observable.just(
                group
        ));

        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);

        //get checkboxList
        final VBox friendListBox = lookup("#groupMemberList").query();
        final ListView<?> memberView = (ListView<?>) friendListBox.getChildren().get(0);

        //"Bob" and "Peter" is displayed
        assertNotNull(memberView.lookup("#Bob"));
        assertNotNull(memberView.lookup("#Peter"));

        //select "Bob" and "Peter"
        clickOn("#Bob");
        clickOn("#Peter");

        verifyThat("#Bob", CheckBox::isSelected);
        verifyThat("#Peter", CheckBox::isSelected);

        //write group name
        clickOn("#groupNameField");
        write("NeueCooleGruppe");

        verifyThat("#groupNameField", hasText("NeueCooleGruppe"));

        clickOn("#createGroupButton");

        verify(groupService).createGroup(anyString(), anyList());

        //verify -> left createChatController
        verify(mock).openChat(group);
    }

}
