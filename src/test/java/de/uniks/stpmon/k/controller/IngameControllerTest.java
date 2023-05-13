package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;



@ExtendWith(MockitoExtension.class)
public class IngameControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @InjectMocks
    IngameController ingameController;

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        app.show(ingameController);
        stage.requestFocus();
    }

    @Test
    void testShow() {
        //ingameController.render();
        Text text = lookup("#ingame").query();
        assertEquals("INGAME", text.getText());
    }

    @Test
    void testShow() {
        Text text = lookup("#ingame").query();
        assertEquals("INGAME", text.getText());
    }
}
