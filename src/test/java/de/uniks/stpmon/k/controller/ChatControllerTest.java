package de.uniks.stpmon.k.controller;

import javax.inject.Provider;

import javafx.collections.ObservableList;
import javafx.scene.Node;
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
import org.testfx.matcher.base.NodeMatchers;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        .thenAnswer((invocation)->{
            events.onNext(new Event<Message>("groups.g_id.messages.1.created", msg));
            return Observable.just(msg);
        });

        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // action: go into message input and send a message with enter keycode
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
        
        // last item has to be desired message
        ObservableList<Message> items = listView.getItems();
        Message lastMessage = items.get(items.size() - 1);

        // check mocks:
        verifyThat(lastMessage.body(), TextMatchers.hasText("moin"));
    }

    @Test
    void testSendMessageOnEnter() {
        // define mocks:
        Message msg = new Message("2023-05-15T00:00:00.000Z", "2023-05-15T00:00:00.000Z", "b_msg_id", "b_id", "moin");
        // this simulates the send message call and the listener in one action
        when(msgService.sendMessage(any(), any(), any()))
        .thenAnswer((invocation)->{
            events.onNext(new Event<Message>("groups.g_id.messages.1.created", msg));
            return Observable.just(msg);
        });

        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // action: go into message input and send a message with enter keycode
        write("\t".repeat(3));
        write("moin");
        TextField messageInput = lookup("#messageField").query();
        assertThat(messageInput).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertThat(messageInput.getText().isEmpty());

        // check values:
        waitForFxEvents();
        verifyThat(listView, ListViewMatchers.hasItems(3));
        
        // last item has to be desired message
        ObservableList<Message> items = listView.getItems();
        Message lastMessage = items.get(items.size() - 1);

        // check mocks:
        verifyThat(lastMessage.body(), TextMatchers.hasText("moin"));
    }

    @Test
    void testEditMessage() {
        // define mocks:
        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        Message editMsg = new Message("2023-05-15T18:30:00.000Z", "1", "b_msg_id", "b_id", "new text from B");
        ObservableList<Message> items = listView.getItems();
        // find old message and verify old text of message which is just "B"
        Message oldMsg = null;
        for (Message msg : items)  {
            if (msg._id() == editMsg._id()) {
                oldMsg = msg;
                break;
            }
        };
        assertNotNull(oldMsg);
        verifyThat(oldMsg.body(), TextMatchers.hasText("B"));
        verifyThat(listView, ListViewMatchers.hasItems(2));
        // this simulates the edit message call and the listener in one action
        when(msgService.editMessage(any(), any(), any(), any()))
        .thenAnswer((invocation)->{
            events.onNext(new Event<Message>("groups.g_id.messages.1.updated", editMsg));
            return Observable.just(editMsg);
        });

        // action: go into messages list and click on a list item (message)
        ListCell<Message> desiredCell = null;
        for (Node node : listView.lookupAll(".list-cell")) {
            if (node instanceof ListCell) {
                ListCell<Message> cell = (ListCell<Message>) node;
                Message message = cell.getItem();
                // Check the desired condition or property of the message object
                if (message.body() == "B") {
                    desiredCell = cell;
                    break;
                }
            }
        }

        // action: click the old message and then edit the text
        clickOn(desiredCell);
        TextField messageInput = lookup("#messageField").query();
        clickOn(messageInput);
        write("trololo");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertThat(messageInput.getText().isEmpty());

        // check values:
        waitForFxEvents();
        // messages are still of count 2
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // check mocks:
        items.forEach(msg -> {
            if (msg._id() == editMsg._id()) {
                verifyThat(msg.body(), TextMatchers.hasText("new text from B"));
            }
        });
    }

    @Test
    void testDeleteMessage() {
        // preperation:
        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        Message deleteMsg = new Message("2023-05-15T18:30:00.000Z", "1", "b_msg_id", "b_id", "new text from B");
        ObservableList<Message> items = listView.getItems();
        // find old message and verify old text of message which is just "B"
        Message oldMsg = null;
        for (Message msg : items)  {
            if (msg._id() == deleteMsg._id()) {
                oldMsg = msg;
                break;
            }
        };
        assertNotNull(oldMsg);
        verifyThat(oldMsg.body(), TextMatchers.hasText("B"));
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // define mocks: this simulates the edit message call and the listener in one action
        when(msgService.deleteMessage(any(), any(), any()))
        .thenAnswer((invocation)->{
            events.onNext(new Event<Message>("groups.g_id.messages.1.deleted", deleteMsg));
            return Observable.just(deleteMsg);
        });

        // action: go into messages list and click on a list item (message)
        ListCell<Message> desiredCell = null;
        for (Node node : listView.lookupAll(".list-cell")) {
            if (node instanceof ListCell) {
                ListCell<Message> cell = (ListCell<Message>) node;
                Message message = cell.getItem();
                // Check the desired condition or property of the message object
                if (message.body() == "B") {
                    desiredCell = cell;
                    break;
                }
            }
        }

        // action: click the old message and then edit the text
        clickOn(desiredCell);
        TextField messageInput = lookup("#messageField").query();
        clickOn(messageInput);
        press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        // delete text and hit enter
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertThat(messageInput.getText().isEmpty());

        // check values:
        waitForFxEvents();
        // messages have to be count of one now
        verifyThat(listView, ListViewMatchers.hasItems(1));
    }

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
