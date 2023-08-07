package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith(MockitoExtension.class)
public class LoadingRegionControllerTest extends ApplicationTest {

    @Mock
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
        when(regionStorage.getRegion()).thenReturn(DummyConstants.REGION);
        when(regionStorage.getArea()).thenReturn(DummyConstants.AREA);
        app.show(loadingRegionController);
        stage.requestFocus();
    }

    @Test
    public void testShow() {
        verifyThat("#regionLabel", hasText("- Test Region -"));
        verifyThat("#areaLabel", hasText("Test Area"));
    }

    @Test
    public void testSetMinTime() {
        int minTime = 2000;
        loadingRegionController.setMinTime(minTime);
        assertEquals(minTime, loadingRegionController.minTime);
    }

}
