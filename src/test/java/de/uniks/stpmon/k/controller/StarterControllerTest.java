package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

public class StarterControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @InjectMocks
    StarterController starterController;

    @Override
    public void start(Stage stage) throws Exception {
        // show app
        app.start(stage);
        app.show(starterController);
        stage.requestFocus();
    }

    @Test
    public void testUI() {

    }
}
