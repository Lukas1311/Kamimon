package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.DaggerTestComponent;
import de.uniks.stpmon.k.TestComponent;
import de.uniks.stpmon.k.dto.Group;
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
        when(eventListener.<Group>listen(any(), any())).thenReturn(Observable.empty());
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        VBox chatList = lookup("#chatList").query();
        assertNotNull(chatList);

        userStorage.setUser(new User("1", "Bob", "", "", new ArrayList<>()));
        when(eventListener.<Message>listen(any(), any())).thenReturn(Observable.empty());
        // pressing on a chat and check if chatScreen is shown
        write("\t\t\t\t\t");
        type(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        VBox chatScreen = lookup("#chatScreen").query();
        assertNotNull(chatScreen);
    }

    @Test
    public void openFriends() {
        userStorage.setUser(new User("1", "Bob", "", "", new ArrayList<>()));
        // pressing Friends Button and check if friendList is shown
        write("\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        VBox chatList = lookup("#friendList").query();
        assertNotNull(chatList);
    }

    @Test
    public void toIngame() {
        // pressing Region button and check if ingame is shown
        write("\t\t\t\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        VBox ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }

    @Test
    public void Pause() {
        toIngame();
        waitForFxEvents();
        // pressing Pause button and check if pause is shown
        write("\t\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        Pane pause = lookup("#pauseScreen").query();
        assertNotNull(pause);

        // pressing Pause button again and check if ingame is shown
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        VBox ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }

    @Test
    public void toHome() {
        // pressing home button and check if lobby is shown
        toIngame();
        waitForFxEvents();
        write("\t\t\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        Pane pane = lookup("#pane").query();
        assertNotNull(pane);
    }

    @Test
    public void logout() {
        // pressing logout button and check if login is shown
        write("\t\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        Pane pane = lookup("#loginScreen").query();
        assertNotNull(pane);
    }
}