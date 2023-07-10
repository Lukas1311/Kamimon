package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.service.EffectContext;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoadingScreenControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);

    @InjectMocks
    LoadingScreenController loadingController;

    @Mock
    Runnable runTask;

    @Spy
    final EffectContext effectContext = new EffectContext()
            .setSkipLoadImages(true);

    @Override
    public void start(Stage stage) {
        // show app
        app.start(stage);
        stage.requestFocus();
    }

    @Test
    public void showScreen() {
        // Show loading screen with no minimum time
        effectContext.setSkipLoading(false);
        loadingController.setMinTime(0);
        loadingController.startLoading(runTask);

        // Check if the loading screen is shown
        verify(app).show(any());
        // Check if the task is executed immediately
        verify(runTask).run();
    }

    @Test
    public void testSkip() {
        // Skip show loading screen
        effectContext.setSkipLoading(true);
        loadingController.startLoading(runTask);

        // Check if the loading screen is not shown
        verify(app, never()).show(any());
        // Check if the task is executed immediately
        verify(runTask).run();
    }

    @Test
    public void showMin() {
        // Show loading screen but with very high minimum time
        effectContext.setSkipLoading(false);
        loadingController.setMinTime(10000);
        loadingController.startLoading(runTask);

        // Check if the loading screen is shown
        verify(app).show(any());
        // Check if the task is not executed
        verify(runTask, never()).run();
    }

}
