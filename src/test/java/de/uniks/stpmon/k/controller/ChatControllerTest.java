package de.uniks.stpmon.k.controller;

import javax.inject.Provider;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TextMatchers;

import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Event;
import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.rest.GroupApiService;
import de.uniks.stpmon.k.service.MessageService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.views.MessageCell;
import de.uniks.stpmon.k.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.Subject;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest extends ApplicationTest {
    @Mock
    MessageService msgService;
    @Mock
    RegionService regionService;
    @Mock
    GroupApiService groupApiService;
    @Mock
    UserService userService;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    EventListener eventListener;
    @Mock
    Group group;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    App app = new App(null);

    @InjectMocks
    ChatController chatController;

    Subject<Event<Message>> events = BehaviorSubject.create();

    @Override
    public void start(Stage stage) throws Exception {
        // we have to do all the stuff here because it is set in the init() method of ChatController :(((
        final User bob = new User("b_id", "b", null, null, null);
        final User alice = new User("a_id", "a", null, null, null);
        final List<User> userList = Arrays.asList(bob, alice);
        final List<String> memberIds = Arrays.asList(bob._id(), alice._id());
        final Group group = new Group("", "", "g_id", "a + b", memberIds);
        // group has to be initiated beforehand
        chatController.setGroup(group);

        // create these two message in the beginning so there is already something to see
        final ArrayList<Message> messagesMock = new ArrayList<>(List.of(
            new Message("2023-05-15T00:00:00.000Z", "2023-05-15T00:00:00.000Z", "a_msg_id", "a_id", "A"),
            new Message("2023-05-15T00:00:00.000Z", "2023-05-15T00:00:00.000Z", "b_msg_id", "b_id", "B")
        ));

        // this is the stuff that happens in init() -> mock this
        when(userService.getUsers(any())).thenReturn(Observable.just(userList));
        when(msgService.getAllMessages(any(), any())).thenReturn(Observable.just(messagesMock));
        when(eventListener.<Message>listen(any(), any())).thenReturn(events);
        
        when(regionService.getRegions()).thenReturn(Observable.just(List.of(new Region("1", "1", "i", "reg"))));
        when(userService.getMe()).thenReturn(bob);

        // show app
        app.start(stage);
        app.show(chatController);
        stage.requestFocus();
    }

    @Test
    void testSendMessageOnButtonClick() {
        // define mocks:
        Message msg = new Message("2023-05-15T18:30:00.000Z", "1", "id", "b_id", "moin");
        // this simulates the send message call and the listener in one action
        when(msgService.sendMessage(any(), any(), any()))
        .thenAnswer((a)->{
            events.onNext(new Event<Message>("groups.g_id.messages.1.created", msg));
            return Observable.just(msg);
        });

        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // action:
        // go into message input and send a message
        write("\t".repeat(3));
        write("moin\t");
        Button sendButton = lookup("#sendButton").queryButton();
        assertThat(sendButton).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        TextField messageInput = lookup("#messageField").query();
        assertThat(messageInput.getText().isEmpty());

        // check values:
        waitForFxEvents();
        verifyThat(listView, ListViewMatchers.hasItems(3));
        
        ObservableList<Message> items = listView.getItems();
        // last item has to be desired message
        Message lastItem = items.get(items.size() - 1);

        // find the last cell that corresponds to the lastItem
        ListCell<Message> lastCell = lookup(
            node -> node instanceof ListCell && ((ListCell<Message>) node).getItem() == lastItem
        ).query();
        assertNotNull(lastCell);

        Text bodyText = lookup("#bodyText").queryText();
        verifyThat(bodyText, TextMatchers.hasText("moin"));
    }

    @Test
    void testSendMessageOnEnter() {
        // define mocks:
        when(msgService.sendMessage(any(), any(), any())).thenReturn(Observable.just(
            new Message("2023-05-15T18:43:40.413Z", any(), any(), "bobs_id", "moin")
        ));

        // action:
        write("\t".repeat(10));
        TextField messageInput = lookup("#messageField").query();
        assertThat(messageInput).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);



        // check values:

        // check mocks:

    }

    @Test
    void testListenerMessageCreated() {
        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        assertEquals(2, listView.getItems().size());

        events.onNext(new Event<Message>("groups.g_id.messages.1.created", new Message("2023-05-15T18:43:40.413Z", "2023-05-15T18:43:40.413Z", "1", "s", "C")));

        waitForFxEvents();
        assertEquals(3, listView.getItems().size());
    }

    @Test
    void testListenerMessageEdited() {
        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        assertEquals(2, listView.getItems().size());

        events.onNext(new Event<Message>("groups.g_id.messages.bobs_msg_id.updated", new Message("2023-05-15T00:00:00.000Z", "2023-05-15T18:43:40.413Z", "bobs_msg_id", "b", "B")));

        waitForFxEvents();
        assertEquals(2, listView.getItems().size());
    }

    @Test
    void testListenerMessageDeleted() {
        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        assertEquals(2, listView.getItems().size());

        events.onNext(new Event<Message>("groups.g_id.messages.bobs_msg_id.deleted", new Message("2023-05-15T00:00:00.000Z", "2023-05-15T00:00:00.000Z", "bobs_msg_id", "b", "B")));

        waitForFxEvents();
        assertEquals(1, listView.getItems().size());
    }

    @Test // TODO: add test
    void testEditMessage() {}

    @Test // TODO: add test
    void testDeleteMessage() {}

    @Test // TODO: add test
    void testSendInvite() {}

    @Test
    void testLeaveChat() {
        // define mocks:
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(app).show(mock);
        doNothing().when(mock).openSidebar("chatList");

        // action:
        clickOn("#backButton");

        // no values to check

        // check mocks:
        verify(app).show(mock);
        verify(mock).openSidebar("chatList");
    }

    @Test
    void testOpenSettings() {
        // define mocks:
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(app).show(mock);
        doNothing().when(mock).openSidebar("createChat");

        // action:
        clickOn("#settingsButton");

        // no values to check

        // check mocks:
        verify(app).show(mock);
        verify(mock).openSidebar("createChat");
    }

    @Test
    void testGetAndSetGroup() {
        // define mocks:
        group = new Group("1", "2", "i", "new", Arrays.asList("m"));

        // action:
        chatController.setGroup(group);

        // check values (values from startup):
        Text groupName = lookup("#groupName").queryText();
        assertThat(groupName.getText()).isEqualTo("a + b");

        // check mocks:
        Group retrievedGroup = chatController.getGroup();
        assertEquals(group, retrievedGroup);
    }
}
