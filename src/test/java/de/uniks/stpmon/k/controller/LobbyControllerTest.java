package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;



@ExtendWith(MockitoExtension.class)
public class LobbyControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    LobbyController lobbyController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(lobbyController);
        stage.requestFocus();
    }
}
