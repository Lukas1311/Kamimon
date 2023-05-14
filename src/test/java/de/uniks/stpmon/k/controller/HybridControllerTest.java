package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.DaggerTestComponent;
import de.uniks.stpmon.k.TestComponent;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserStorage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class HybridControllerTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    private final HybridController hybridController = component.hybridController();
    UserStorage userStorage = component.userStorage();


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(hybridController);
        stage.requestFocus();
    }


    @Test
    public void openChat() {
        type(KeyCode.ENTER);
        VBox chatList = lookup("#chatList").query();
        assertNotNull(chatList);
    }

    @Test
    public void openFriends() {
        userStorage.setUser(new User("1", "Bob", "", "", new ArrayList<>()));
        write("\t");
        type(KeyCode.ENTER);
        VBox chatList = lookup("#friendList").query();
        assertNotNull(chatList);
    }

    @Test
    public void toIngame() {
        StackPane stackPane = lookup("#stackPane").query();
        write("\t\t\t\t");
        type(KeyCode.ENTER);
        VBox ingame = lookup("#ingame").query();
        assertNotNull(ingame);
    }
}