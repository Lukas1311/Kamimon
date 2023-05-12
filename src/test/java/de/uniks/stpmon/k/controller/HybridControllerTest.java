package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.DaggerTestComponent;
import de.uniks.stpmon.k.TestComponent;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HybridControllerTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
    private final HybridController hybridController = component.hybridController();


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(hybridController);
        stage.requestFocus();
    }


    @Test
    public void testOpenSidebar() {
        StackPane stackPane = lookup("#stackPane").query();
        assertEquals(hybridController.stackPane.getId(), stackPane.getId());
        Platform.runLater(() -> {
            // open ChatList, check if ID are equal
            hybridController.sidebarController.get().openChat();
            assertEquals("chatList", hybridController.stackPane.getChildren().get(1).getId());
            /*
            // open FriendList, check if ID are equal
            hybridController.openSidebar("friends");
            assertEquals("friendList", hybridController.stackPane.getChildren().get(1).getId());
             */

            // open Pause, check if ID are equal
            hybridController.sidebarController.get().toPause();
            assertEquals("pause", hybridController.stackPane.getChildren().get(0).getId());

            // clicking again Pause to go back to ingame, check if ID are equal
            hybridController.sidebarController.get().toPause();
            assertEquals("ingame", hybridController.stackPane.getChildren().get(0).getId());

            // Lobby, check if ID are equal
            hybridController.sidebarController.get().backtoLobby();
            assertEquals("pane", hybridController.stackPane.getChildren().get(0).getId());

            // Ingame, check if ID are equal
            hybridController.openSidebar("ingame");
            assertEquals("ingame", hybridController.stackPane.getChildren().get(0).getId());

            // Logout, check if app shows login
            hybridController.sidebarController.get().logout();
        });
    }
}