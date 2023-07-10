package de.uniks.stpmon.k.controller.chat;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.controller.ToastController;
import de.uniks.stpmon.k.models.Event;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.GroupService;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.utils.ExceptionHelper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class ChatListControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);
    @Mock
    GroupService groupService;
    @Mock
    UserService userService;
    @Mock
    EventListener eventListener;
    @Spy
    final ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;
    @Spy
    @InjectMocks
    ToastController toastController;

    @InjectMocks
    ChatListController chatListController;

    final Subject<Event<Group>> groupEvents = PublishSubject.create();
    final ArrayList<Group> groups = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        groups.add(new Group(null, null, "0", "Peter", null));
        when(eventListener.<Group>listen(eq(Socket.WS), any(), any())).thenReturn(groupEvents);
        when(groupService.getOwnGroups()).thenReturn(Observable.just(groups));
        when(userService.getMe()).thenReturn(DummyConstants.USER_ALICE);
        app.show(chatListController);
        stage.requestFocus();
    }

    @Test
    void updateList() {
        verifyThat("#chatListView", hasItems(1));

        // Create new group
        groupEvents.onNext(new Event<>("groups.1.created", new Group(null, null, "1", "Bob", null)));
        waitForFxEvents();
        verifyThat("#chatListView", hasItems(2));

        // Update group
        Group group2 = new Group(null, null, "1", "Alice", null);
        groupEvents.onNext(new Event<>("groups.1.updated", group2));
        waitForFxEvents();
        verifyThat("#chatListView", hasListCell(group2));

        // Delete group
        groupEvents.onNext(new Event<>("groups.1.deleted", group2));
        waitForFxEvents();
        verifyThat("#chatListView", hasItems(1));
    }

    @Test
    void handleError() {
        when(groupService.getOwnGroups()).thenReturn(ExceptionHelper.justHttp(429));
        app.show(chatListController);
        verify(toastController).openToast(any());
    }

}
