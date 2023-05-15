package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.service.GroupService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatListControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);
    @Mock
    GroupService groupService;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);

    final ArrayList<Group> groups = new ArrayList<>();
    @InjectMocks
    ChatListController chatListController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        groups.add(new Group(null, null, null, "Peter", null));
        when(groupService.getOwnGroups()).thenReturn(Observable.just(groups));
        app.show(chatListController);
        stage.requestFocus();
    }

    @Test
    void openChat() {
        final VBox groupList = lookup("#chatList").query();
        assertNotNull(groupList.getChildren());
    }
}