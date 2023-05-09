package de.uniks.stpmon.k;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoginController;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class AppTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        new App().start(stage);
    }

    @Test
    void testController(){
        Controller login = new LoginController();
        login.render();
        login.destroy();
    }
}
