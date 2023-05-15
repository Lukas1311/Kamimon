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
import static org.testfx.assertions.api.Assertions.assertThat;

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

    @Override
    public void start(Stage stage) throws Exception {
        // we have to do all the stuff here because it is set in the init() method of ChatController :(((
        final HashMap<String, String> groupMembers = new HashMap<>();
        final User bob = new User("b_id", "b", null, null, null);
        final User alice = new User("a_id", "a", null, null, null);
        final List<User> userList = Arrays.asList(bob, alice);
        final List<String> memberIds = Arrays.asList(bob._id(), alice._id());

        final ArrayList<Message> messagesMock = new ArrayList<>(List.of(new Message("2023-05-15T18:43:40.413Z", null, null, "b", "TEST")));
        final ListView<Message> messagesListView = new ListView<>();
        messagesListView.getItems().addAll(messagesMock);

        // create real instance of MessageCell then spy it
        MessageCell messageCell = new MessageCell(bob, groupMembers);
        MessageCell messageCellSpy = spy(messageCell);
        // capture arg passed to updateItem() in cell
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        // stub updateItem to call the real method and then add the item to the ListView
        doAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            boolean empty = invocation.getArgument(1);
            // Call the real updateItem method
            messageCellSpy.updateItem(message, empty);
            // Add the item to the ListView
            messagesListView.getItems().add(message);
            return null;
        }).when(messageCellSpy).updateItem(messageCaptor.capture(), anyBoolean());


        // group has to be initiated beforehand
        when(group.members()).thenReturn(memberIds);
        when(group.name()).thenReturn("g");
        // this is the stuff that happens in init() -> mock this
        when(userService.getUsers(any())).thenReturn(Observable.just(userList));
        when(msgService.getAllMessages(any(), any())).thenReturn(Observable.just(messagesMock));
        when(eventListener.listen(any(), any())).thenReturn(Observable.just(new Event<Object>("created", new Message("2023-05-15T18:43:40.413Z", "2", "i", "a", "TEST"))));
        when(regionService.getRegions()).thenReturn(Observable.just(List.of(new Region("1", "1", "i", "reg"))));
        when(userService.getMe()).thenReturn(bob);

        // show app
        app.start(stage);
        app.show(chatController);
        stage.requestFocus();

        //
        messagesListView.setCellFactory(listView -> messageCellSpy);
    }

    // @Test
    // void testSendMessageOnButtonClick() {
    //     // define mocks:
    //     when(msgService.sendMessage(anyString(), anyString(), anyString())).thenReturn(Observable.just(
    //         new Message("2023-05-15T18:43:40.413Z", any(), "id", "bobs_id", "moin")
    //     ));

    //     // action:
    //     // go into message input
    //     write("\t".repeat(3));
    //     write("moin\t");
    //     Button sendButton = lookup("#sendButton").queryButton();
    //     assertThat(sendButton).isFocused();
    //     press(KeyCode.ENTER).release(KeyCode.ENTER);
    //     TextField messageInput = lookup("#messageField").query();
    //     assertThat(messageInput.getText().isEmpty());

    //     // check values:
    //     Text groupName = lookup("#groupName").queryText();
    //     assertThat(groupName.getText()).isEqualTo("g");
    //     ListView<Message> listView = lookup("#messageArea .list-view").query();
    //     assertNotNull(listView);

    //     ObservableList<Message> items = listView.getItems();
    //     // last item has to be desired message
    //     Message lastItem = items.get(items.size() - 1);
    //     System.out.println(lastItem);

    //     // find the last cell that corresponds to the lastItem
    //     ListCell<Message> lastCell = lookup(
    //         node -> node instanceof ListCell && ((ListCell<Message>) node).getItem() == lastItem
    //     ).query();
    //     assertNotNull(lastCell);
    //     System.out.println(lastCell);

    //     assertThat(lastCell.getText()).isEqualTo("desired text");
    // }

    // @Test
    // void testSendMessageOnEnter() {
    //     // define mocks:
    //     when(msgService.sendMessage(any(), any(), any())).thenReturn(Observable.just(
    //         new Message("2023-05-15T18:43:40.413Z", any(), any(), "bobs_id", "moin")
    //     ));

    //     // action:
    //     write("\t".repeat(10));
    //     TextField messageInput = lookup("#messageField").query();
    //     assertThat(messageInput).isFocused();
    //     press(KeyCode.ENTER).release(KeyCode.ENTER);



    //     // check values:
    //     Text groupName = lookup("#groupName").queryText();
    //     assertThat(groupName.getText()).isEqualTo("g");

    //     // check mocks:

    // }

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
        Text groupName = lookup("#groupName").queryText();
        // still check because why not
        assertThat(groupName.getText()).isEqualTo("g");

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
        Text groupName = lookup("#groupName").queryText();
        // still check because why not
        assertThat(groupName.getText()).isEqualTo("g");

        // check mocks:
        verify(app).show(mock);
        verify(mock).openSidebar("createChat");
    }

    @Test
    void testGetAndSetGroup() {
        // define mocks:
        group = new Group("1", "2", "i", "b", Arrays.asList("m"));

        // action:
        chatController.setGroup(group);

        // check values:
        Group retrievedGroup = chatController.getGroup();
        assertEquals(group, retrievedGroup);
    }


    // @Test
    // void testAddRegionsToChoiceBox() {
    //     // define mocks:
    //     when(regionService.getRegions()).thenReturn(Observable.just(List.of(new Region("1", "2", "i", "r"))));
    //     // action:


    //     // check values:
    //     Text groupName = lookup("#groupName").queryText();
    //     assertThat(groupName.getText()).isEqualTo("g");

    //     // check mocks:
    // }
}
