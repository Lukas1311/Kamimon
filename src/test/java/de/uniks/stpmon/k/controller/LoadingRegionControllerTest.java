package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoadingRegionControllerTest extends ApplicationTest {

    @Spy
    RegionStorage regionStorage;
    @Spy
    final App app = new App(null);
    @InjectMocks
    LoadingRegionController loadingRegionController;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);


    @Override
    public void start(Stage stage) {
        app.start(stage);
        loadingRegionController = new LoadingRegionController();
        regionStorage = mock(RegionStorage.class);
        final Region currentRegion = new Region("1", "r", null, null);
        when(regionStorage.getRegion()).thenReturn(currentRegion);

        loadingRegionController.regionStorage = regionStorage;
        stage.requestFocus();
    }

    @Test
    public void testShow() {
        Platform.runLater(() -> {
            assertNotNull(loadingRegionController.render());
            assertEquals("r", loadingRegionController.regionLabel.getText());
            assertNotNull(loadingRegionController.imageViewKamimonLettering.getImage());
        });
    }

    @Test
    public void testSetMinTime() {
        int minTime = 2000;
        loadingRegionController.setMinTime(minTime);
        assertEquals(minTime, loadingRegionController.minTime);
    }

}
