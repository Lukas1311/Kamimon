package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.map.Property;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.models.map.layerdata.ChunkData;
import de.uniks.stpmon.k.models.map.layerdata.ObjectData;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import de.uniks.stpmon.k.service.world.TextDeliveryService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.world.RouteData;
import de.uniks.stpmon.k.world.RouteText;
import de.uniks.stpmon.k.world.TileMap;
import de.uniks.stpmon.k.world.Tileset;
import de.uniks.stpmon.k.models.map.Tile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import io.reactivex.rxjava3.core.Observable;
import java.awt.image.BufferedImage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    private TileMapData createDummyMap() {
        ChunkData chunk = new ChunkData(List.of(4, 2, 1, 3),
                2, 2,
                0, 0);
        ObjectData object = new ObjectData(0, "Route 101", List.of(
                new Property("Route 101","Route", "text")
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

    Map<TilesetSource, Tileset> tilesetMap = new HashMap<>() {{
        TilesetData tilesetData = new TilesetData(
            1,
            "i",
            1,
            1,
            1,
            "n",
            1,
            1,
            1,
            1,
            List.of(
                new Tile(
                    1,
                    List.of(
                        new Property("Route 101","Route", "text")
                    )
                )
            ),
            "t"
        );
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
        TilesetSource tilesetSource = new TilesetSource(1, "grass.json");
        Tileset.builder()
            .setData(tilesetData)
            .setImage(image)
            .setSource(tilesetSource)
            .build();
    }};
    TileMap dummyTileMap = new TileMap(dummyRegion, tilesetMap);


    @Override
    public void start(Stage stage) throws Exception {
        app.start(stage);
        when(regionStorage.getRegion()).thenReturn(dummyRegion);
        RouteData dummyData = new RouteData(1, new RouteText("Route 66", "HiWay", "Route"), 1, 2, 16, 32);
        when(textDeliveryService.getRouteData(any())).thenReturn(Observable.just(List.of(dummyData)));
        when(textureSetService.createMap(any()))
            .thenReturn(Observable.just(dummyTileMap));
        app.show(mapOverviewController);
        stage.requestFocus();
    }

    @Test
    void testRender() {
        // prep:
        TileMap tileMap = Mockito.mock(TileMap.class);
        BufferedImage renderedMap = Mockito.mock(BufferedImage.class);
        Image map = Mockito.mock(Image.class);
        // ImageView mapImageViewMock = Mockito.mock(ImageView.class);
        ImageView mapImageView = lookup("#mapImageView").queryAs(ImageView.class);

        // define mocks:
        when(tileMap.renderMap()).thenReturn(renderedMap);
        // Image map = Mockito.mock(Image.class);
        // final Image[] mockImage = new Image[1];
        // doAnswer(invocation -> {
        //     mockImage[0] = Mockito.mock(Image.class);
        //     return null;
        // }).when(mapImageView).setImage(any());
        // action:
        // render is automatically called

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


        // verify mocks:
        // Verify the expected interactions
        // verify(mapImageView).setImage(any());
        // verify(mapImageView.fitWidthProperty()).bind(any());
        // verify(mapImageView.fitHeightProperty()).bind(any());
        // verify(mapContainerMock).setPrefSize(anyDouble(), anyDouble());
        verifyNoMoreInteractions(mapImageView, mapContainer);
    }

    @Test
    void testIfMapNull() {

    }
}
