package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Spawn;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.WorldLoader;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class RegionListControllerTest extends ApplicationTest {

    @Mock
    RegionService regionService;
    @Mock
    WorldLoader worldLoader;
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
        regionListController.doesTrainerExist = true;

        app.start(stage);
        when(resourceBundleProvider.get()).thenReturn(resources);
        app.show(regionListController);
        stage.requestFocus();
    }

    @Test
    void testShow() {
        when(worldLoader.tryEnterRegion(any())).thenReturn(Observable.empty());

        GridPane regionListGridPane = lookup("#regionListGridPane").query();
        assertThat(regionListGridPane.getColumnCount()).isEqualTo(1);
        Text regionName = lookup("#regionNameText").queryText();
        assertEquals("Test", regionName.getText());
        ImageView regionImage = lookup("#regionImage").query();
        clickOn(regionImage);
        waitForFxEvents();

        verify(worldLoader).tryEnterRegion(any());

    }
}
