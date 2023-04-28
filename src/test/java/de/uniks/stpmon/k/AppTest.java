package de.uniks.stpmon.k;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.DummyController;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class AppTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        new App(new DummyController()).start(stage);
    }

    @Test
    void testController(){
        Controller dummy = new DummyController();
        dummy.render();
        dummy.destroy();
    }
}