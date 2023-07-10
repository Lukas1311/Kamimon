package de.uniks.stpmon.k.controller.encounter;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.Trainer;
import de.uniks.stpmon.k.models.builder.TrainerBuilder;
import de.uniks.stpmon.k.service.EffectContext;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class LoadingEncounterControllerTest extends ApplicationTest {
    @Spy
    App app = new App(null);

    @InjectMocks
    LoadingEncounterController loadingEncounterController;

    @Spy
    final EffectContext effectContext = new EffectContext()
            .setSkipLoadImages(true);


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        loadingEncounterController = new LoadingEncounterController();
    }

    @Test
    public void showScreen() {
        effectContext.setSkipLoading(false);
        loadingEncounterController.render();

        // Check if the loading encounter screen is shown
        verify(app).show(any());
    }


    @Test
    public void testSkip() {
        // Skip show loading screen
        effectContext.setSkipLoading(true);
        loadingEncounterController.render();

        // Check if the loading screen is not shown
        verify(app, never()).show(any());
    }
}