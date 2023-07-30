package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.RegionService;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.storage.TrainerStorage;
import de.uniks.stpmon.k.service.storage.WorldRepository;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.world.RouteText;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextFlowMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class MapOverviewControllerTest extends ApplicationTest {

    @Spy
    final App app = new App(null);

    @Spy
    WorldRepository worldRepository;
    @Mock
    RegionService regionService;
    @Mock
    RegionStorage regionStorage;
    @Mock
    TrainerStorage trainerStorage;
    @Mock
    TextDeliveryService textDeliveryService;

    @Spy
    @InjectMocks
    MapOverviewController mapOverviewController;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);


    @Override
    public void start(Stage stage) {
        app.start(stage);

        when(regionStorage.getRegion()).thenReturn(DummyConstants.REGION);
        when(trainerStorage.getTrainer()).thenReturn(DummyConstants.TRAINER_W_VISITED_AREAS);
        when(regionService.getAreas(any())).thenReturn(Observable.just(List.of(DummyConstants.AREA)));

        RouteData dummyData1 = new RouteData(1, new RouteText("Route 66", "HiWay1", "Route"), 0, 0, 0, 0,
                List.of(new PolygonPoint(0, 0), new PolygonPoint(20, 0), new PolygonPoint(15, 15), new PolygonPoint(0, 15)));
        RouteData dummyData2 = new RouteData(2, new RouteText("Route 101", "HiWay2", "Route"), 1, 1, 0, 0, List.of());
        RouteData dummyData3 = new RouteData(3, new RouteText("Route 102", "HiWay3", "Route"), 10, 10, 0, 34, List.of());
        
        when(textDeliveryService.getRouteData(any())).thenReturn(Observable.just(List.of(dummyData1, dummyData2, dummyData3)));
        worldRepository.regionMap().setValue(DummyConstants.EMPTY_IMAGE);
        app.show(mapOverviewController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        // prep:
        ImageView mapImageViewMock = mock(ImageView.class);
        ImageView mapImageView = lookup("#mapImageView").queryAs(ImageView.class);

        // action: render() already done automatically by this time

        // check values:
        Label regionNameLabel = lookup("#regionNameLabel").queryAs(Label.class);
        assertEquals("Test Region", regionNameLabel.getText());
        AnchorPane mapOverviewHolder = lookup("#mapOverviewHolder").queryAs(AnchorPane.class);
        Label areaNameLabel = lookup("#areaNameLabel").queryAs(Label.class);
        Text regionDescription = lookup("#regionDescription").queryText();

        assertNotNull(mapOverviewHolder);
        assertNotNull(mapImageView);
        assertNotNull(areaNameLabel);
        assertNotNull(regionDescription);
        assertTrue(mapOverviewController.mapOverviewHolder.isVisible());

        // check mocks:
        verifyNoMoreInteractions(mapImageViewMock);
        verify(regionStorage).getRegion();
        verify(textDeliveryService).getRouteData(DummyConstants.REGION);
        Polygon detail1 = lookup("#detail_1").query();
        assertNotNull(detail1);
        Rectangle detail2 = lookup("#detail_2").query();
        assertNotNull(detail2);

        // No detail should be visible
        verifyThat(detail1, polygon -> polygon.getFill().equals(Color.SILVER));

        // move to first route
        moveTo(detail1);
        // Detail should be half highlighted
        verifyThat(detail1, polygon -> polygon.getOpacity() >= 0.75);
        // Click on first route
        clickOn(MouseButton.PRIMARY);
        // Detail should be fully highlighted
        verifyThat(detail1, polygon -> polygon.getOpacity() >= 0.95);
        // Route description should be visible
        assertEquals("???", areaNameLabel.getText());
        verifyThat("#textFlowRegionDescription", hasText("???"));
        // Move to second route
        moveTo(detail2);
        waitForFxEvents();
        // First route should still be fully highlighted
        verifyThat(detail1, polygon -> polygon.getOpacity() >= 0.95);
        // Second route should be half highlighted
        verifyThat(detail2, rect -> rect.getOpacity() >= 0.75);
        // Click on second route
        clickOn(MouseButton.PRIMARY);
        // First route should not be highlighted anymore
        verifyThat(detail1, polygon -> polygon.getFill().equals(Color.SILVER));
        // Second route should be fully highlighted
        verifyThat(detail2, rect -> rect.getOpacity() >= 0.95);
    }

}
