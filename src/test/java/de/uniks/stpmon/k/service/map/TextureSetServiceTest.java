package de.uniks.stpmon.k.service.map;

import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.models.Spawn;
import de.uniks.stpmon.k.models.map.ChunkData;
import de.uniks.stpmon.k.models.map.TileLayerData;
import de.uniks.stpmon.k.models.map.TileMapData;
import de.uniks.stpmon.k.models.map.TilesetData;
import de.uniks.stpmon.k.models.map.TilesetSource;
import de.uniks.stpmon.k.service.IResourceService;
import de.uniks.stpmon.k.service.world.TextureSetService;
import de.uniks.stpmon.k.world.TileMap;
import de.uniks.stpmon.k.world.Tileset;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TextureSetServiceTest {

    @Mock
    IResourceService resourceService;

    @InjectMocks
    TextureSetService msgService;

    private TileMapData createDummyMap() {
        ChunkData chunk = new ChunkData(List.of(4, 2, 1, 3),
                2, 2,
                0, 0);
        TileLayerData layer = new TileLayerData(1, "Ground", List.of(chunk),
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

    @Test
    public void renderMap() {
        when(resourceService.getTilesetData("grass.json")).thenReturn(Observable.just(new TilesetData(
                2, "grass.png",
                2, 2,
                0, "grass", 0, 1,
                1, 1, List.of(), ""
        )));

        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(0, 1, Color.BLACK.getRGB());
        image.setRGB(1, 0, Color.BLUE.getRGB());
        image.setRGB(1, 1, Color.GREEN.getRGB());

        when(resourceService.getTilesetImage("grass.png")).thenReturn(Observable.just(image));

        TileMapData map = createDummyMap();
        Region region = new Region("1", "2", new Spawn("1", 0, 0), map);
        TileMap tileMap = msgService.createMap(region)
                .blockingFirst();

        //check if map is created
        Map<TilesetSource, Tileset> tilesets = tileMap.getTilesets();
        assertEquals(1, tilesets.size());

        assertDoesNotThrow(() -> {
            BufferedImage resultImage = tileMap.renderMap();
            assertEquals(2, resultImage.getWidth());
            assertEquals(2, resultImage.getHeight());
            assertEquals(Color.GREEN.getRGB(), resultImage.getRGB(0, 0));
            assertEquals(Color.BLUE.getRGB(), resultImage.getRGB(1, 0));
            assertEquals(Color.RED.getRGB(), resultImage.getRGB(0, 1));
            assertEquals(Color.BLACK.getRGB(), resultImage.getRGB(1, 1));
        });
    }
}
