package de.uniks.stpmon.k.controller.chat;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.ToastController;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.*;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.rest.GroupApiService;
import de.uniks.stpmon.k.service.MessageService;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.world.WorldLoader;
import de.uniks.stpmon.k.utils.ExceptionHelper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TextMatchers;

import javax.inject.Provider;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest extends ApplicationTest {

    @Mock
    MessageService msgService;
    @Mock
    RegionService regionService;
    @Mock
    @SuppressWarnings("unused")
    GroupApiService groupApiService;
    @Mock
    UserService userService;
    @Mock
    Provider<HybridController> hybridControllerProvider;
    @Mock
    EventListener eventListener;
    @Mock
    Group group;
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    @Spy
    final App app = new App(null);

    @InjectMocks
    ChatController chatController;
    @Mock
    WorldLoader worldLoader;
    @Spy
    @InjectMocks
    ToastController toastController;

    final Subject<Event<Message>> events = BehaviorSubject.create();

    @Override
    public void start(Stage stage) {
        when(resourceBundleProvider.get()).thenReturn(resources);
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
        when(eventListener.<Message>listen(eq(Socket.WS), any(), any())).thenReturn(events);

        when(regionService.getRegions()).thenReturn(Observable.just(List.of(new Region("i", "reg", new Spawn("0", 0, 0), null))));
        when(userService.getMe()).thenReturn(bob);

        // show app
        app.start(stage);
        app.show(chatController);
        stage.requestFocus();
    }

    // suppress: Value is never used as Publisher
    @SuppressWarnings("all")
    private <T> void mockSend(T methodCall, String type, Message msg) {
        when(methodCall).thenAnswer((invocation) -> {
            events.onNext(new Event<>("groups.g_id.messages.1." + type, msg));
            return Observable.just(msg);
        });
    }

    private void findAndClick(ListView<Message> listView) {
        ListCell<?> desiredCell = null;
        for (Node node : listView.lookupAll(".list-cell")) {
            if (!(node instanceof ListCell<?> cell)) {
                continue;
            }
            if (!(cell.getItem() instanceof Message message)) {
                continue;
            }
            // Check the desired condition or property of the message object
            if (message.body().equals("B")) {
                desiredCell = cell;
                break;
            }
        }

        clickOn(desiredCell);
    }

    @Test
    void testSendMessageOnButtonClick() {
        // define mocks:
        Message msg = new Message("2023-05-15T18:30:00.000Z", "1", "id", "b_id", "moin");
        // this simulates the send message call and the listener in one action
        mockSend(msgService.sendMessage(any(), any(), any()), "created", msg);

        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // action: go into message input and send a message with enter keycode
        write("\t".repeat(3));
        write("moin\t");
        Button sendButton = lookup("#sendButton").queryButton();
        assertThat(sendButton).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        TextField messageInput = lookup("#messageField").query();
        assertTrue(messageInput.getText().isEmpty());

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
        mockSend(msgService.sendMessage(any(), any(), any()), "created", msg);

        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // action: go into message input and send a message with enter keycode
        write("\t".repeat(3));
        write("moin");
        TextField messageInput = lookup("#messageField").query();
        assertThat(messageInput).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertTrue(messageInput.getText().isEmpty());

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
        for (Message msg : items) {
            if (msg._id().equals(editMsg._id())) {
                oldMsg = msg;
                break;
            }
        }
        assertNotNull(oldMsg);
        verifyThat(oldMsg.body(), TextMatchers.hasText("B"));
        verifyThat(listView, ListViewMatchers.hasItems(2));
        // this simulates the edit message call and the listener in one action
        mockSend(msgService.editMessage(any(), any(), any(), any()), "updated", editMsg);

        // action: go into messages list and click on a list item (message)
        findAndClick(listView);

        TextField messageInput = lookup("#messageField").query();
        clickOn(messageInput);

        write("trololo");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertTrue(messageInput.getText().isEmpty());

        // check values:
        waitForFxEvents();
        // messages are still of count 2
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // check mocks:
        items.forEach(msg -> {
            if (msg._id().equals(editMsg._id())) {
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
        for (Message msg : items) {
            if (msg._id().equals(deleteMsg._id())) {
                oldMsg = msg;
                break;
            }
        }
        assertNotNull(oldMsg);
        verifyThat(oldMsg.body(), TextMatchers.hasText("B"));
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // define mocks: this simulates the edit message call and the listener in one action
        mockSend(msgService.deleteMessage(any(), any(), any()), "deleted", deleteMsg);

        // action: go into messages list and click on a list item (message)
        findAndClick(listView);

        // action: click the old message and then edit the text
        TextField messageInput = lookup("#messageField").query();
        clickOn(messageInput);
        press(KeyCode.CONTROL).press(KeyCode.A).release(KeyCode.A).release(KeyCode.CONTROL);
        // delete text and hit enter
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertTrue(messageInput.getText().isEmpty());

        // check values:
        waitForFxEvents();
        // messages have to be counted of one now
        verifyThat(listView, ListViewMatchers.hasItems(1));
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void testSendInvite() {
        // define mocks:
        Message msg = new Message("2023-05-15T00:00:00.000Z", "2023-05-15T00:00:00.000Z", "b_msg_id", "b_id", "JoinInvitation i");
        // this simulates the send message call and the listener in one action
        mockSend(msgService.sendMessage(any(), any(), any()), "created", msg);
        when(worldLoader.tryEnterRegion(any())).thenReturn(Observable.just(DummyConstants.TRAINER));
        when(regionService.getRegion("i")).thenReturn(ExceptionHelper.justHttp(404), Observable.just(
                new Region("i", "reg", new Spawn("0", 0, 0), null)
        ));


        final ListView<Message> listView = lookup("#messageArea .list-view").queryListView();
        verifyThat(listView, ListViewMatchers.hasItems(2));

        // action: tab into region list and chose a region
        write("\t".repeat(5));
        press(KeyCode.DOWN).release(KeyCode.DOWN).press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        // tab back into message input and send the invite with enter keycode
        press(KeyCode.SHIFT).press(KeyCode.TAB).release(KeyCode.TAB).release(KeyCode.SHIFT);
        press(KeyCode.SHIFT).press(KeyCode.TAB).release(KeyCode.TAB).release(KeyCode.SHIFT);
        TextField messageInput = lookup("#messageField").query();
        assertThat(messageInput).isFocused();
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertTrue(messageInput.getText().isEmpty());

        // check values:
        waitForFxEvents();
        verifyThat(listView, ListViewMatchers.hasItems(3));

        // last item has to be desired message
        ObservableList<Message> items = listView.getItems();
        Message joinMessage = items.get(items.size() - 1);

        // check mocks:
        assertEquals("JoinInvitation i", joinMessage.body());
        // Navigate to invitation button
        press(KeyCode.SHIFT).press(KeyCode.TAB).release(KeyCode.TAB).release(KeyCode.SHIFT);

        Button joinButton = lookup("#joinButton").query();
        assertThat(joinButton).isFocused();

        // Activate invitation button
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        // Verify error toast
        verify(toastController).openToast(any());

        // Toast is show on different stage, so we have to request focus on the main stage again
        Platform.runLater(app.getStage()::requestFocus);
        waitForFxEvents();

        // check if still focused
        assertThat(joinButton).isFocused();

        // Activate invitation button
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        // Check if the region was entered
        verify(worldLoader).tryEnterRegion(any());
    }

    @Test
    void testLeaveChat() {
        // define mocks:
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).popTab();

        // action:
        clickOn("#backButton");

        // no values to check

        // check mocks:
        verify(mock).popTab();
    }

    @Test
    void testOpenSettings() {
        // define mocks:
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridControllerProvider.get()).thenReturn(mock);
        doNothing().when(mock).createChat(any());

        // action:
        clickOn("#settingsButton");

        // no values to check

        // check mocks:
        verify(mock).createChat(chatController.getGroup());
    }

    @Test
    void testGetAndSetGroup() {
        // define mocks:
        group = new Group("1", "2", "i", "new", List.of("m"));

        // action:
        chatController.setGroup(group);

        // check values (values from startup):
        Text groupName = lookup("#groupName").queryText();
        assertThat(groupName.getText()).isEqualTo("a");

        // check mocks:
        Group retrievedGroup = chatController.getGroup();
        assertEquals(group, retrievedGroup);
    }

}
