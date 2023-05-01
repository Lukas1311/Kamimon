package de.uniks.stpmon.k;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoginController;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class AppTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        new App(new LoginController()).start(stage);
    }

    @Test
    void testController(){
        Controller login = new LoginController();
        login.render();
        login.destroy();
    }

    @Test
    void loginTest(){
        Controller login = new LoginController();
        Parent parent = login.render();
        // Label errorLabel = (Label) parent.lookup("#errorLabel");
        // clickOn("#usernameInput");
        // write("string");
        // clickOn("#passwordInput");
        // write("stringst");
        // clickOn("#loginButton");
        
        login.destroy();
    }

}
