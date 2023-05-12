package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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


    }
}