package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class RegionListControllerTest extends ApplicationTest {

    @Mock
    Provider<HybridController> hybridController;
    @Mock
    RegionApiService regionApiService;
    @Mock
    IngameController ingameController;
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
        final Observable<List<Region>> regionMock = Observable.just(List.of(new Region("0", "Test", null, null)));
        when(regionApiService.getRegions()).thenReturn(regionMock);
        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(regionListController);
        stage.requestFocus();
    }

    @Test
    void testShow() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridController.get()).thenReturn(mock);

        BorderPane borderPane = lookup("#regionsBorderPane").query();
        ListView<?> listView = (ListView<?>) borderPane.getChildren().get(0);
        Button button = (Button) listView.lookup("#regionButton");
        assertEquals("Test", button.getText());

        clickOn(button);
        waitForFxEvents();
        verify(mock).openMain(INGAME);

    }
}
