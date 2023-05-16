package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.dto.Region;
import de.uniks.stpmon.k.rest.RegionApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;
import javax.inject.Provider;

import java.awt.image.SinglePixelPackedSampleModel;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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

    @Override
    public void start(Stage stage) throws Exception {
        final Observable<List<Region>> regionMock = Observable.just(List.of(new Region("", "", "0", "Test")));
        when(regionApiService.getRegions()).thenReturn(regionMock);
        app.start(stage);
        app.show(regionListController);
        stage.requestFocus();
    }

    @Test
    void testShow() {
        final HybridController mock = Mockito.mock(HybridController.class);
        when(hybridController.get()).thenReturn(mock);

        AnchorPane anchorPane = lookup("#regionList").query();
        ListView<?> listView = (ListView<?>) anchorPane.getChildren().get(0);
        Button button = (Button) listView.lookup("#regionButton");
        assertEquals("Test", button.getText());


        hybridController.get().openSidebar("ingame");
        verify(mock).openSidebar("ingame");

    }
}