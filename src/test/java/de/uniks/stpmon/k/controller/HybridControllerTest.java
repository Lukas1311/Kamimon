package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.di.DaggerTestComponent;
import de.uniks.stpmon.k.di.TestComponent;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.models.Message;
import de.uniks.stpmon.k.models.User;
import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import de.uniks.stpmon.k.service.dummies.TestHelper;
import de.uniks.stpmon.k.service.storage.UserStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @SuppressWarnings("unused")
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    @SuppressWarnings("unused")
    Provider<ResourceBundle> resourceBundleProvider;

    @Override
    public void start(Stage stage) {
        app.start(stage);
        userStorage.setUser(new User("1", "Bob", "", "", new ArrayList<>()));
        app.show(hybridController);
        stage.requestFocus();
    }

    @Test
    public void openChat() {
        // pressing Chat Button and check if chatList is shown
        when(eventListener.<Group>listen(eq(Socket.WS), any(), any())).thenReturn(Observable.empty());
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        VBox chatList = lookup("#chatList").query();
        assertNotNull(chatList);

        userStorage.setUser(new User("1", "Bob", "", "", new ArrayList<>()));
        when(eventListener.<Message>listen(eq(Socket.WS), any(), any())).thenReturn(Observable.empty());
        // pressing on a chat and check if chatScreen is shown
        write("\t\t\t\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
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
        VBox chatList = lookup("#friendListVbox").query();
        assertNotNull(chatList);
    }

    @Test
    public void toIngameAndPause() {
        // mock udp listener
        TestHelper.addMovementDummy(component.eventListener());

        // pressing Region button and check if ingame is shown
        clickOn("#regionVBox");
        waitForFxEvents();

        // create a new trainer
        clickOn("#createTrainerInput");
        write("Tom");
        clickOn("#createTrainerButton");

        // popup
        clickOn("#approveButton");
        waitForFxEvents();

        BorderPane ingame = lookup("#ingame").query();
        assertNotNull(ingame);

        waitForFxEvents();
        // pressing Pause button and check if pause is shown
        write("\t\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        BorderPane pause = lookup("#pauseScreen").query();
        assertNotNull(pause);

        // pressing Pause button again and check if ingame is shown
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }

    @Test
    public void settings() {
        userStorage.setUser(new User("1", "Bob", "", "", new ArrayList<>()));
        when(eventListener.<Message>listen(eq(Socket.WS), any(), any())).thenReturn(Observable.empty());
        // pressing settings button and check if settings is shown
        write("\t\t");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        waitForFxEvents();
        VBox settings = lookup("#settingsScreen").query();
        assertNotNull(settings);
    }

    @Test
    public void closeSidebar() {
        // mock udp listener
        TestHelper.addMovementDummy(component.eventListener());


        when(eventListener.<Group>listen(eq(Socket.WS), any(), any())).thenReturn(Observable.empty());
        StackPane stackPane = lookup("#stackPane").query();
        assertEquals(1, stackPane.getChildren().size());

        // start Lobby with ohne Lobby inside Stackpane
        clickOn("#chat");
        waitForFxEvents();
        assertEquals(2, stackPane.getChildren().size());

        // lobby: close sidebar by clicking inside the lobby
        clickOn("#lobbyPane");
        waitForFxEvents();

        clickOn("#regionVBox");
        waitForFxEvents();

        // create a new trainer
        clickOn("#createTrainerInput");
        write("Tom");
        clickOn("#createTrainerButton");
        // popup pops here
        clickOn("#approveButton");
        waitForFxEvents();

        // get new stack pane from ingame sidebar
        stackPane = lookup("#stackPane").query();

        clickOn("#chat");
        waitForFxEvents();
        assertEquals(2, stackPane.getChildren().size());

        // ingame: close sidebar by clicking inside the game
        BorderPane ingame = lookup("#ingame").query();
        assertNotNull(ingame);
        clickOn("#ingame");
        waitForFxEvents();
        assertEquals(1, stackPane.getChildren().size());

        clickOn("#pause");
        waitForFxEvents();
        BorderPane pause = lookup("#pauseScreen").query();
        assertNotNull(pause);
        clickOn("#chat");
        assertEquals(2, stackPane.getChildren().size());

        // pause: close Sidebar by clicking inside the pause
        clickOn("#pauseScreen");
        waitForFxEvents();
        assertEquals(1, stackPane.getChildren().size());
    }

}
