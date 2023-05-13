package de.uniks.stpmon.k;


import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class AppTest extends ApplicationTest {

    private final App app = new App(null);
    private final TestComponent component = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();

    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
    }

    @Test
    void testController() {
        app.show(component.loginController());
    }
}
