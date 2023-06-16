package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.PolygonPoint;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.service.EffectContext;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.world.RouteText;
import de.uniks.stpmon.k.world.TileMap;
import io.reactivex.rxjava3.core.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MapOverviewControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    TextureSetService textureSetService;
    @Spy
    RegionStorage regionStorage;
    @Mock
    TextDeliveryService textDeliveryService;

    @Spy
    @InjectMocks
    MapOverviewController mapOverviewController;
    @Spy
    @SuppressWarnings("unused")
    EffectContext effectContext = new EffectContext().setSkipLoadImages(true);


    private TileMapData createDummyMap() {
        ChunkData chunk = new ChunkData(List.of(4, 2, 1, 3),
                2, 2,
                0, 0);
        ObjectData object = new ObjectData(0, "Route 101", List.of(), List.of(
                new Property("Route 101", "Route", "text")
        ), "Route", false, 0, 0, 0, 0, 0);
        TileLayerData layer = new TileLayerData(1, "Ground", List.of(chunk), List.of(object),
                0, 0,
                2, 2,
                0, 0, "tilelayer", true, List.of());
        return new TileMapData(
                2, 2,
                false, List.of(layer),
                1, 1,
                List.of(new TilesetSource(1, "grass.json")),
                "map");
    }
    TileMapData dummyMap = createDummyMap();
    Region dummyRegion = new Region("1", "reg", null, dummyMap);
    TileMap tileMapMock = mock(TileMap.class);


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        // mapOverviewController.mapImageView = mapImageViewMock;

        when(regionStorage.getRegion()).thenReturn(dummyRegion);
        RouteData dummyData = new RouteData(1, new RouteText("Route 66", "HiWay", "Route"), 1, 2, 16, 32, List.of(new PolygonPoint(1, 2)));
        RouteData dummyData2 = new RouteData(2, new RouteText("Route 101", "HiWay", "Route"), 1, 2, 20, 34, List.of());
        RouteData dummyData3 = new RouteData(3, new RouteText("Route 102", "HiWay", "Route"), 0, 2, 20, 34, List.of());
        when(textDeliveryService.getRouteData(any())).thenReturn(Observable.just(List.of(dummyData, dummyData2, dummyData3)));
        when(textureSetService.createMap(any()))
                .thenReturn(Observable.just(tileMapMock));
        app.show(mapOverviewController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        // prep:
        BufferedImage renderedMapMock = mock(BufferedImage.class);
        ImageView mapImageViewMock = mock(ImageView.class);
        ImageView mapImageView = lookup("#mapImageView").queryAs(ImageView.class);
        MockedStatic<SwingFXUtils> mockedStatic = Mockito.mockStatic(SwingFXUtils.class);
        WritableImage dummyMapWritableImage = new WritableImage(10, 10);

        // mock mocks:
        when(tileMapMock.renderMap()).thenReturn(renderedMapMock);
        mockedStatic.when(() -> SwingFXUtils.toFXImage(renderedMapMock, null)).thenReturn(dummyMapWritableImage);

        // action: render() already done automatically by this time

        // check values:
        Label regionNameLabel = lookup("#regionNameLabel").queryAs(Label.class);
        assertEquals("reg", regionNameLabel.getText());
        BorderPane mapOverviewContent = lookup("#mapOverviewContent").queryAs(BorderPane.class);
        Button closeButton = lookup("#closeButton").queryButton();
        VBox mapContainer = lookup("#mapContainer").queryAs(VBox.class);
        Text regionDescription = lookup("#regionDescription").queryText();

        assertNotNull(mapOverviewContent);
        assertNotNull(closeButton);
        assertNotNull(mapImageView);
        assertNotNull(mapContainer);
        assertNotNull(regionDescription);
        assertTrue(mapOverviewController.mapOverviewContent.isVisible());

        // check mocks:
        verify(mapOverviewController, times(1)).handleError(any());
        verifyNoMoreInteractions(mapImageViewMock);
        verify(app).getStage();
        verify(regionStorage).getRegion();
        verify(textDeliveryService).getRouteData(dummyRegion);
        verify(textureSetService).createMap(dummyRegion);

        // close static mock:
        mockedStatic.close();
    }
}
