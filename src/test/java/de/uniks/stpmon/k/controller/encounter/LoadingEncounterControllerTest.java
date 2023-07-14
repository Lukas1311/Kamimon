package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.EffectContext;
import javafx.stage.Stage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;


@ExtendWith(MockitoExtension.class)
public class LoadingEncounterControllerTest extends ApplicationTest {
    @Spy
    final
    App app = new App(null);

    @InjectMocks
    @SuppressWarnings("unused")
    LoadingEncounterController loadingEncounterController;

    @Spy
    @SuppressWarnings("unused")
    final EffectContext effectContext = new EffectContext()
            .setSkipLoadImages(true);


    @Override
    public void start(Stage stage) {
        app.start(stage);
        loadingEncounterController = new LoadingEncounterController();
    }
}