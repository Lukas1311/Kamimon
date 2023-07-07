package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import javafx.stage.Stage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;


@ExtendWith(MockitoExtension.class)
public class LoadingEncounterControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @InjectMocks
    LoadingEncounterController loadingEncounterController;


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);

    }
}