package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.DaggerTestComponent;
import de.uniks.stpmon.k.TestComponent;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserStorage;
import de.uniks.stpmon.k.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class HybridControllerTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    private final HybridController hybridController = component.hybridController();
    private final UserStorage userStorage = component.userStorage();
    private final EventListener eventListener = component.eventListener();
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(hybridController);
        stage.requestFocus();
    }


    @Test
    public void openChat() {
        // pressing Chat Button and check if chatList is shown
        type(KeyCode.ENTER);
        VBox chatList = lookup("#chatList").query();
        assertNotNull(chatList);

        when(eventListener.<Message>listen(any(), any())).thenReturn(Observable.empty());
        // pressing on a chat and check if chatScreen is shown
        clickOn("#TestGroup0");
        waitForFxEvents();
        VBox chatScreen = lookup("#chatScreen").query();
        assertNotNull(chatScreen);
    }

    @Test
    public void openFriends() {
        userStorage.setUser(new User("1", "Bob", "", "", new ArrayList<>()));
        // pressing Friends Button and check if friendList is shown
        write("\t");
        type(KeyCode.ENTER);
        VBox chatList = lookup("#friendList").query();
        assertNotNull(chatList);
    }

    @Test
    public void toIngame() {
        // pressing Region button and check if ingame is shown
        write("\t\t\t\t");
        type(KeyCode.ENTER);
        VBox ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }

    @Test
    public void Pause() {
        toIngame();
        // pressing Pause button and check if pause is shown
        write("\t\t");
        type(KeyCode.ENTER);
        Pane pause = lookup("#pauseScreen").query();
        assertNotNull(pause);

        // pressing Pause button again and check if ingame is shown
        type(KeyCode.ENTER);
        VBox ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }

    @Test
    public void toHome() {
        // pressing home button and check if lobby is shown
        toIngame();
        write("\t\t\t");
        type(KeyCode.ENTER);
        Pane pane = lookup("#pane").query();
        assertNotNull(pane);
    }

    @Test
    public void
    logout() {
        // pressing logout button and check if login is shown
        write("\t\t");
        type(KeyCode.ENTER);
    }
}