package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Spawn;
import de.uniks.stpmon.k.service.RegionService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static de.uniks.stpmon.k.controller.sidebar.MainWindow.INGAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class RegionListControllerTest extends ApplicationTest {

    @Mock
    Provider<HybridController> hybridController;
    @Mock
    RegionService regionService;
    @Mock
    IngameController ingameController;
    @Mock
    CreateTrainerController createTrainerController;
    @InjectMocks
    RegionListController regionListController;
    @Spy
    App app = new App(null);
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stpmon/k/lang/lang", Locale.ROOT);
    @Mock
    Provider<ResourceBundle> resourceBundleProvider;

    @Override
    public void start(Stage stage) throws Exception {
        final Observable<List<Region>> regionMock = Observable.just(List.of(new Region("0", "Test", new Spawn("0", 0, 0), null)));
        when(regionService.getRegions()).thenReturn(regionMock);
        app.start(stage);
        regionListController.setNewTrainer(false);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(regionListController);
        stage.requestFocus();
    }

    @Test
    void testShowWithOldTrainer() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridController.get()).thenReturn(mock);

        when(regionService.enterRegion(any()))
                .thenReturn(Observable.just(new Area("0", "0", "Test", null)));

        BorderPane borderPane = lookup("#regionsBorderPane").query();
        ListView<?> listView = (ListView<?>) borderPane.getChildren().get(0);
        Button button = (Button) listView.lookup("#regionButton");
        assertEquals("Test", button.getText());

        clickOn(button);
        waitForFxEvents();

        verify(createTrainerController, never()).render();
        verify(regionService).enterRegion(any());

        verify(mock).openMain(INGAME);
    }

    @Test
    void testShowWithNewTrainer() {
        BorderPane borderPane = lookup("#regionsBorderPane").query();
        // New trainer
        regionListController.setNewTrainer(true);

        Parent createTrainer = createTrainerController.render();

        clickOn("#regionButton");
        waitForFxEvents();

        assertEquals(createTrainer, borderPane.getCenter());

        // Verify that regionService.enterRegion is not called
        verify(regionService, never()).enterRegion(any(Region.class));
    }
}
